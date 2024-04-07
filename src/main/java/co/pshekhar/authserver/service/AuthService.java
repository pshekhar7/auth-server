package co.pshekhar.authserver.service;

import co.pshekhar.authserver.domain.ApiTagConfig;
import co.pshekhar.authserver.domain.enums.AccessMode;
import co.pshekhar.authserver.model.response.InternalAuthResponse;
import co.pshekhar.authserver.model.response.InternalCredValidationResponse;
import co.pshekhar.authserver.model.response.Status;
import co.pshekhar.authserver.repository.AccessConfigRepository;
import co.pshekhar.authserver.repository.ApiTagConfigRepository;
import co.pshekhar.authserver.repository.CredentialsRepository;
import co.pshekhar.authserver.repository.ScopeRepository;
import co.pshekhar.authserver.util.Constant;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class AuthService {
    private final CredentialsRepository credentialsRepository;
    private final ScopeRepository scopeRepository;
    private final AccessConfigRepository accessConfigRepository;
    private final ApiTagConfigRepository apiTagConfigRepository;
    private final long EXPIRATION_TIME_MINUTES;
    private final SecretKey secretKey;

    public AuthService(CredentialsRepository credentialsRepository,
                       ScopeRepository scopeRepository,
                       AccessConfigRepository accessConfigRepository,
                       ApiTagConfigRepository apiTagConfigRepository,
                       @Value("${auth.config.jwt.expiry-in-min}") long expirationTimeMinutes,
                       @Qualifier("jwt-secret-key") SecretKey secretKey,
                       @Qualifier("virtualThreadScheduler") Scheduler scheduler) {
        this.credentialsRepository = credentialsRepository;
        this.scopeRepository = scopeRepository;
        this.accessConfigRepository = accessConfigRepository;
        this.apiTagConfigRepository = apiTagConfigRepository;
        this.EXPIRATION_TIME_MINUTES = expirationTimeMinutes;
        this.secretKey = secretKey;
    }

    /**
     * <b>Authentication steps:</b>
     * <ul>1. validate credentials</ul>
     * <ul>2. check API access</ul>
     * <ul>3. generate JWT</ul>
     */
    public Mono<InternalAuthResponse> authenticate(HttpHeaders headers) {
        return validateCredentials(headers)
                .flatMap(result -> {
                    if (Status.FAILURE == result.getStatus()) {
                        return Mono.defer(() -> Mono.just(InternalAuthResponse
                                .builder()
                                .status(Status.UNAUTHORIZED)
                                .reason(result.getReason())
                                .build()));
                    } else {
                        return validateAPIAccess(headers)
                                .filter(Boolean.TRUE::equals)
                                .map(_ignored -> (InternalAuthResponse) InternalAuthResponse
                                        .builder()
                                        .status(result.getStatus())
                                        .jwt(createJWT(result))
                                        .build()
                                )
                                .switchIfEmpty(Mono.defer(() -> Mono.just(InternalAuthResponse
                                        .builder()
                                        .status(Status.FORBIDDEN)
                                        .reason("Resource access denied")
                                        .build())));
                    }
                })
                .log()
                .onErrorReturn(InternalAuthResponse
                        .builder()
                        .status(Status.FAILURE)
                        .reason("Internal error")
                        .build()
                );
    }

    private Mono<String> createJWT(InternalCredValidationResponse request) {
        Instant now = Instant.now();
        Date issuedAt = Date.from(now);
        Date expiration = Date.from(now.plus(EXPIRATION_TIME_MINUTES, ChronoUnit.MINUTES));

        return scopeRepository.findById(request.getScopeId())
                .map(scope -> {
                    final Map<String, Object> claims = new HashMap<>(request.getContextData());
                    claims.put("scope", scope.getType().name());
                    claims.put("scopeId", scope.getIdentifier());
                    return Jwts.builder()
                            .issuedAt(issuedAt)
                            .expiration(expiration)
                            .claims(claims)
                            .signWith(secretKey, Jwts.SIG.HS256)
                            .compact();
                });
    }

    private Mono<InternalCredValidationResponse> validateCredentials(HttpHeaders headers) {
        final String clientId = headers.getFirst(Constant.HEADER_CLIENT_ID);
        final String secret = headers.getFirst(Constant.HEADER_CLIENT_SECRET);
        return credentialsRepository.findByIdAndSecret(clientId, secret)
                .map(existingCred -> (InternalCredValidationResponse) InternalCredValidationResponse
                        .builder()
                        .scopeId(existingCred.getScopeId())
                        .contextData(existingCred.getContextData())
                        .status(Status.SUCCESS)
                        .build()
                )
                .switchIfEmpty(Mono.defer(() -> Mono.just(InternalCredValidationResponse.builder().status(Status.FAILURE).reason("Invalid credentials").build())));
    }

    private Mono<Boolean> validateAPIAccess(HttpHeaders headers) {
        final String clientId = headers.getFirst(Constant.HEADER_CLIENT_ID);
        final String serviceScopeId = headers.getFirst(Constant.HEADER_TARGET_SERVICE);
        final String method = headers.getFirst(Constant.HEADER_TARGET_HTTP_METHOD);
        final String path = headers.getFirst(Constant.HEADER_TARGET_PATH);
        return accessConfigRepository.findByCredIdAndScopeId(clientId, serviceScopeId)
                .log()
                .flatMap(existingAccCnf -> deriveApiTag(method, path, serviceScopeId)
                        .filter(tag -> existingAccCnf.getAccessApiList().contains(tag))
                        .hasElement()
                        .map(hasTag -> {
                            if (AccessMode.ALLOW == existingAccCnf.getAccessMode()) {
                                return hasTag;
                            } else {
                                return !hasTag;
                            }
                        })
                );
    }

    private Mono<String> deriveApiTag(String method, String path, String serviceScopeId) {
        final String[] pathTokens = path.split("/");
        return apiTagConfigRepository.findByServiceScopeIdAndMethod(serviceScopeId, method)
                .filter(apiTagConfig -> {
                    String[] matcherPath = apiTagConfig.getPathRegex().split("/");
                    return pathTokens.length == matcherPath.length
                            && matchAPIPaths(pathTokens, matcherPath);
                })
                .log()
                .singleOrEmpty()
                .map(ApiTagConfig::getTag);
    }

    private boolean matchAPIPaths(String[] input, String[] target) {
        int len = input.length;
        for (int i = 0; i < len; i++) {
            if (!target[i].equals("@")
                    && !target[i].equals(input[i])) {
                return false;
            }
        }
        return true;
    }
}
