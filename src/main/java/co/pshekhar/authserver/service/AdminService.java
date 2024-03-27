package co.pshekhar.authserver.service;

import co.pshekhar.authserver.domain.Credentials;
import co.pshekhar.authserver.domain.Scope;
import co.pshekhar.authserver.domain.enums.CredStatus;
import co.pshekhar.authserver.domain.enums.ScopeType;
import co.pshekhar.authserver.model.request.CredentialRequest;
import co.pshekhar.authserver.model.request.ScopeRequest;
import co.pshekhar.authserver.model.response.CredentialsResponse;
import co.pshekhar.authserver.model.response.ScopeResponse;
import co.pshekhar.authserver.model.response.Status;
import co.pshekhar.authserver.model.response.helper.CredentialData;
import co.pshekhar.authserver.model.response.helper.ScopeData;
import co.pshekhar.authserver.repository.CredentialsRepository;
import co.pshekhar.authserver.repository.ScopeRepository;
import co.pshekhar.authserver.util.Constant;
import co.pshekhar.authserver.util.Utilities;
import lombok.NonNull;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
public class AdminService {

    private final ScopeRepository scopeRepository;
    private final CredentialsRepository credentialsRepository;

    public AdminService(ScopeRepository scopeRepository, CredentialsRepository credentialsRepository) {
        this.scopeRepository = scopeRepository;
        this.credentialsRepository = credentialsRepository;
    }

    public Mono<ScopeResponse> createScope(@NonNull ScopeRequest request) {
        // check existing record based on scope and scopeId; if exists, return error else return new data
        return scopeRepository.findByTypeAndIdentifier(ScopeType.valueOf(request.getScope()), request.getScopeId())
                .log()
                .map(existScope -> (ScopeResponse) ScopeResponse.builder().status(Status.FAILURE).reason("Scope with supplied data already exists!").build())
                .switchIfEmpty(saveNewScope(request));
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
        return credentialsRepository.findById(request.getClientId())
                .map(cred -> (CredentialsResponse) CredentialsResponse.builder().status(Status.FAILURE).reason("Record already exists with the supplied clientId").build())
                .switchIfEmpty(checkAndSaveNewCredentials(request));
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
                            );
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
