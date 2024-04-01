package co.pshekhar.authserver.controller;

import co.pshekhar.authserver.service.AuthService;
import co.pshekhar.authserver.util.Constant;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping(value = "/authenticate", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    Mono<ResponseEntity<Object>> authenticate(@RequestHeader HttpHeaders headers) {
        return authService.authenticate(headers)
                .flatMap(res ->
                        switch (res.getStatus()) {
                            case SUCCESS -> res.getJwt()
                                    .map(jwt -> ResponseEntity
                                            .status(200)
                                            .headers(h -> h.add(Constant.HEADER_AUTH_TOKEN_RESPONSE, jwt))
                                            .build()
                                    );

                            case FAILURE -> Mono.defer(() -> Mono.just(ResponseEntity.status(500).body(res)));
                            case FORBIDDEN -> Mono.defer(() -> Mono.just(ResponseEntity.status(403).body(res)));
                            case UNAUTHORIZED -> Mono.defer(() -> Mono.just(ResponseEntity.status(401).body(res)));
                        }
                );
    }
}
