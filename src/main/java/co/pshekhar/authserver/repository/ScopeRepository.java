package co.pshekhar.authserver.repository;

import co.pshekhar.authserver.domain.Scope;
import co.pshekhar.authserver.domain.enums.ScopeType;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface ScopeRepository extends R2dbcRepository<Scope, String> {
    Mono<Scope> findByTypeAndIdentifier(ScopeType scope, String identifier);
}
