package co.pshekhar.authserver.service;

import co.pshekhar.authserver.domain.AccessConfig;
import co.pshekhar.authserver.domain.Credentials;
import co.pshekhar.authserver.domain.Scope;
import co.pshekhar.authserver.domain.enums.AccessConfigStatus;
import co.pshekhar.authserver.domain.enums.AccessMode;
import co.pshekhar.authserver.domain.enums.CredStatus;
import co.pshekhar.authserver.domain.enums.ScopeType;
import co.pshekhar.authserver.model.request.AccessConfigRequest;
import co.pshekhar.authserver.model.request.CredentialOpsRequest;
import co.pshekhar.authserver.model.request.CredentialRequest;
import co.pshekhar.authserver.model.request.CredentialRotateRequest;
import co.pshekhar.authserver.model.request.ScopeRequest;
import co.pshekhar.authserver.model.response.AccessConfigResponse;
import co.pshekhar.authserver.model.response.CredentialsResponse;
import co.pshekhar.authserver.model.response.GenericResponse;
import co.pshekhar.authserver.model.response.ScopeResponse;
import co.pshekhar.authserver.model.response.Status;
import co.pshekhar.authserver.model.response.helper.AccessConfigData;
import co.pshekhar.authserver.model.response.helper.AccessResponse;
import co.pshekhar.authserver.model.response.helper.CredentialData;
import co.pshekhar.authserver.model.response.helper.ScopeData;
import co.pshekhar.authserver.repository.AccessConfigRepository;
import co.pshekhar.authserver.repository.CredentialsRepository;
import co.pshekhar.authserver.repository.ScopeRepository;
import co.pshekhar.authserver.util.Constant;
import co.pshekhar.authserver.util.Utilities;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
public class AdminService {

    private static final Logger log = LoggerFactory.getLogger(AdminService.class);
    private final ScopeRepository scopeRepository;
    private final CredentialsRepository credentialsRepository;
    private final AccessConfigRepository accessConfigRepository;

    public AdminService(ScopeRepository scopeRepository, CredentialsRepository credentialsRepository, AccessConfigRepository accessConfigRepository) {
        this.scopeRepository = scopeRepository;
        this.credentialsRepository = credentialsRepository;
        this.accessConfigRepository = accessConfigRepository;
    }

    public Mono<ScopeResponse> createScope(@NonNull ScopeRequest request) {
        // check existing record based on scope and scopeId; if exists, return error else return new data
        return scopeRepository.findByTypeAndIdentifier(ScopeType.valueOf(request.getScope()), request.getScopeId())
                .log()
                .map(existScope -> (ScopeResponse) ScopeResponse.builder().status(Status.FAILURE).reason("Scope with supplied data already exists!").build())
                .switchIfEmpty(saveNewScope(request))
                .onErrorReturn(ScopeResponse.builder().status(Status.FAILURE).reason("Internal error").build());
    }

    public Mono<ScopeResponse> getScope(@NonNull ScopeRequest request) {
        // check existing record based on scope and scopeId; if exists, return details else return error
        return scopeRepository.findByTypeAndIdentifier(ScopeType.valueOf(request.getScope()), request.getScopeId())
                .log()
                .map(existScope -> (ScopeResponse) ScopeResponse.builder()
                        .status(Status.SUCCESS)
                        .data(ScopeData.builder()
                                .scope(existScope.getType().toString())
                                .scopeId(existScope.getIdentifier())
                                .createdOn(Utilities.formatDate(existScope.getCreatedOn()))
                                .build())
                        .build())
                .switchIfEmpty(Mono.just(ScopeResponse.builder().status(Status.FAILURE).reason("Not found").build()));
    }

    public Mono<CredentialsResponse> issueCredentials(@NonNull CredentialRequest request) {
        return credentialsRepository.findAnyById(request.getClientId())
                .map(cred -> (CredentialsResponse) CredentialsResponse.builder().status(Status.FAILURE).reason("Record already exists with the supplied clientId").build())
                .switchIfEmpty(checkAndSaveNewCredentials(request))
                .onErrorReturn(CredentialsResponse.builder().status(Status.FAILURE).reason("Internal error").build());
    }

    public Mono<CredentialsResponse> updateCredentialStatus(@NonNull CredentialOpsRequest request, CredStatus credStatus) {
        return credentialsRepository.findById(request.getClientId())
                .flatMap(cred -> {
                    cred.setStatus(credStatus);
                    return credentialsRepository.save(cred)
                            .map(savedCred -> (CredentialsResponse) CredentialsResponse
                                    .builder()
                                    .status(Status.SUCCESS)
                                    .data(CredentialData.builder()
                                            .clientId(savedCred.getClientId())
                                            .expiry(Utilities.formatDate(savedCred.getExpiry()))
                                            .status(savedCred.getStatus().name())
                                            .build()
                                    )
                                    .build());
                })
                .switchIfEmpty(Mono.<CredentialsResponse>just(CredentialsResponse.builder().status(Status.FAILURE).reason("No credentials found with supplied clientId").build()));
    }

    public Mono<CredentialsResponse> getCredentialsSummary(@NonNull CredentialOpsRequest request) {
        return credentialsRepository.findById(request.getClientId())
                .map(existingCred -> (CredentialsResponse) CredentialsResponse
                        .builder()
                        .status(Status.SUCCESS)
                        .data(CredentialData.builder()
                                .clientId(existingCred.getClientId())
                                .expiry(Utilities.formatDate(existingCred.getExpiry()))
                                .status(existingCred.getStatus().name())
                                .build()
                        )
                        .build()
                )
                .switchIfEmpty(Mono.<CredentialsResponse>just(CredentialsResponse.builder().status(Status.FAILURE).reason("No credentials found with supplied clientId").build()));
    }

    public Mono<CredentialsResponse> rotateCredentials(@NonNull CredentialRotateRequest request) {
        return credentialsRepository.findById(request.getClientId())
                .flatMap(existingCred -> {
                            existingCred.setExpiry(LocalDateTime.now(ZoneId.of(Constant.IND_ZONE)).plusDays(request.getRotateAfter()));
                            existingCred.setStatus(request.isActivate() ? CredStatus.ACTIVE : CredStatus.INACTIVE);
                            existingCred.setLastRotatedOn(LocalDateTime.now(ZoneId.of(Constant.IND_ZONE)));
                            existingCred.setClientSecret(Utilities.generateRandomSecure(17));
                            return credentialsRepository.save(existingCred)
                                    .map(savedCred ->
                                            (CredentialsResponse) CredentialsResponse
                                                    .builder()
                                                    .status(Status.SUCCESS)
                                                    .data(CredentialData.builder()
                                                            .clientId(savedCred.getClientId())
                                                            .expiry(Utilities.formatDate(savedCred.getExpiry()))
                                                            .status(savedCred.getStatus().name())
                                                            .secret(savedCred.getClientSecret())
                                                            .build()
                                                    )
                                                    .build());
                        }
                )
                .switchIfEmpty(Mono.<CredentialsResponse>just(CredentialsResponse.builder().status(Status.FAILURE).reason("No credentials found with supplied clientId").build()));
    }

    public Mono<GenericResponse> updateAccessForCredential(@NonNull AccessConfigRequest request) {
        return credentialsRepository.findById(request.getClientId())
                .flatMap(existingCred ->
                        accessConfigRepository.findByCredIdAndScopeId(request.getClientId(), request.getTargetScopeId())
                                .flatMap(existingAccCnf -> {
                                    // expire this entry and create new
                                    return createAccessConfig(request)
                                            .log()
                                            .flatMap(_relayed -> {
                                                        existingAccCnf.setStatus(AccessConfigStatus.EXPIRED);
                                                        existingAccCnf.setNewEntry(false);
                                                        return accessConfigRepository.save(existingAccCnf)
                                                                .map(_ignored -> _relayed)
                                                                .log();
                                                    }
                                            )
                                            .onErrorReturn(GenericResponse.builder().status(Status.FAILURE).reason("Internal error").build());
                                })
                                .switchIfEmpty(createAccessConfig(request))
                                .onErrorReturn(GenericResponse.builder().status(Status.FAILURE).reason("Internal error").build())
                )
                .switchIfEmpty(Mono.<GenericResponse>just(GenericResponse.builder().status(Status.FAILURE).reason("No credentials found with supplied clientId").build()));
    }

    public Mono<AccessConfigResponse> accessConfigSummary(@NonNull CredentialOpsRequest request) {
        return credentialsRepository.findById(request.getClientId())
                .flatMap(existingCred ->
                        accessConfigRepository.findByCredId(request.getClientId())
                                .log()
                                .map(existingAccCnf ->
                                        (AccessConfigData) AccessConfigData
                                                .builder()
                                                .targetScopeId(existingAccCnf.getScopeId())
                                                .access(AccessResponse.builder()
                                                        .operation(existingAccCnf.getAccessMode().name())
                                                        .apiTags(existingAccCnf.getAccessApiList())
                                                        .build())
                                                .build()
                                )
                                .collectList()
                                .map(aCnfData -> (AccessConfigResponse) AccessConfigResponse
                                        .builder()
                                        .data(aCnfData)
                                        .status(Status.SUCCESS)
                                        .clientId(request.getClientId())
                                        .build()
                                )
                                .log()
                )
                .switchIfEmpty(Mono.<AccessConfigResponse>just(AccessConfigResponse.builder().status(Status.FAILURE).reason("No credentials found with supplied clientId").build()));
    }

    private Mono<GenericResponse> createAccessConfig(AccessConfigRequest request) {
        AccessConfig accessConfig = new AccessConfig();
        accessConfig.initIdentifier();
        accessConfig.setCredId(request.getClientId());
        accessConfig.setScopeId(request.getTargetScopeId());
        accessConfig.setAccessMode(AccessMode.valueOf(request.getAccess().getOperation()));
        accessConfig.setAccessApiList(request.getAccess().getApiTags());

        return accessConfigRepository.save(accessConfig)
                .map(savedAccCnf -> (GenericResponse) GenericResponse.builder().status(Status.SUCCESS).build())
                .doOnError(throwable ->
                        log.error("Error occurred while saving access config for clientId: [{}] and targetScope: [{}]. Reason: [{}]", request.getClientId(), request.getTargetScopeId(), throwable.getMessage()));
    }


    private Mono<CredentialsResponse> checkAndSaveNewCredentials(CredentialRequest request) {
        return scopeRepository.findByTypeAndIdentifier(ScopeType.valueOf(request.getScope()), request.getScopeId())
                .log()
                .flatMap(exisingScope -> {
                    final Credentials credentials = new Credentials();
                    credentials.setClientId(request.getClientId());
                    credentials.setNewEntry(true);
                    credentials.setExpiry(LocalDateTime.now(ZoneId.of(Constant.IND_ZONE)).plusDays(request.getRotateAfter()));
                    credentials.setClientSecret(Utilities.generateRandomSecure(17));
                    credentials.setStatus(request.isActivate() ? CredStatus.ACTIVE : CredStatus.INACTIVE);
                    credentials.setContextData(request.getContextData());
                    credentials.setScopeId(exisingScope.getId());
                    return credentialsRepository.save(credentials)
                            .map(savedCred -> (CredentialsResponse) CredentialsResponse
                                    .builder()
                                    .status(Status.SUCCESS)
                                    .data(CredentialData.builder()
                                            .clientId(savedCred.getClientId())
                                            .secret(savedCred.getClientSecret())
                                            .expiry(Utilities.formatDate(savedCred.getExpiry()))
                                            .status(savedCred.getStatus().name())
                                            .build()
                                    )
                                    .build()
                            )
                            ;
                })
                .switchIfEmpty(Mono.<CredentialsResponse>just(CredentialsResponse.builder().status(Status.FAILURE).reason("No scope exists with supplied scope and scopeId. Please register a scope first.").build()));
    }

    private Mono<ScopeResponse> saveNewScope(ScopeRequest request) {
        Scope scope = new Scope();
        scope.initIdentifier();
        scope.setIdentifier(request.getScopeId());
        scope.setType(ScopeType.valueOf(request.getScope()));
        scope.setNewEntry(true);
        return scopeRepository.save(scope)
                .map(
                        savedScope -> ScopeResponse.builder()
                                .status(Status.SUCCESS)
                                .data(ScopeData.builder()
                                        .scope(savedScope.getType().toString())
                                        .scopeId(savedScope.getIdentifier())
                                        .createdOn(Utilities.formatDate(savedScope.getCreatedOn()))
                                        .build())
                                .build());
    }
}
