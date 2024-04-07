package co.pshekhar.authserver.controller;

import co.pshekhar.authserver.service.AuthService;
import co.pshekhar.authserver.service.LogService;
import co.pshekhar.authserver.util.Constant;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.concurrent.ExecutorService;

@RestController
@RequestMapping("/")
public class AuthController {
    private final AuthService authService;
    private final ExecutorService executorService;
    private final LogService logService;

    public AuthController(AuthService authService,
                          @Qualifier("virtualThreadExecutor") ExecutorService executorService,
                          LogService logService) {
        this.authService = authService;
        this.executorService = executorService;
        this.logService = logService;
    }

    @PostMapping(value = "/authenticate", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    Mono<ResponseEntity<?>> authenticate(@RequestHeader HttpHeaders headers) {
        return authService.authenticate(headers)
                .flatMap(res -> {
                            executorService.execute(() -> logService.logLoginAttempt(headers, res));
                            return switch (res.getStatus()) {
                                case SUCCESS -> res.getJwt()
                                        .map(jwt -> ResponseEntity
                                                .status(200)
                                                .headers(h -> h.add(Constant.HEADER_AUTH_TOKEN_RESPONSE, jwt))
                                                .build()
                                        );

                                case FAILURE -> Mono.defer(() -> Mono.just(ResponseEntity.status(500).body(res)));
                                case FORBIDDEN -> Mono.defer(() -> Mono.just(ResponseEntity.status(403).body(res)));
                                case UNAUTHORIZED -> Mono.defer(() -> Mono.just(ResponseEntity.status(401).body(res)));
                            };
                        }
                );
    }
}
