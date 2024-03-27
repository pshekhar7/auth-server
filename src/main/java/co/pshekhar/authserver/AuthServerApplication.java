package co.pshekhar.authserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@SpringBootApplication
@RestController
@RequestMapping("/")
public class AuthServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(AuthServerApplication.class, args);
    }

    @GetMapping(value = "/health", produces = MediaType.TEXT_PLAIN_VALUE)
    public Mono<String> healthCheck() {
        return Mono.just("server is health");
//        Scope scope = new Scope();
//        scope.setType(ScopeType.CLIENT);
//        scope.setIdentifier("cl123");
//        scope.initIdentifier();
//        scope.setNewEntry(true);
//        return scopeRepository.save(scope)
//                .flatMap(s -> scopeRepository.findById(s.getId())
//                        .map(Object::toString)
//                        .defaultIfEmpty("Not found!") // Handle if not found
//                        .log());
    }

}
