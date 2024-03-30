package co.pshekhar.authserver.repository;

import co.pshekhar.authserver.domain.AccessConfig;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface AccessConfigRepository extends R2dbcRepository<AccessConfig, String> {
    @Query(value = "select * from access_config where cred_id = :credId and scope_id = :scopeId and status = 'ACTIVE'")
    Mono<AccessConfig> findByCredIdAndScopeId(String credId, String scopeId);

    @Query(value = "select * from access_config where cred_id = :credId and status = 'ACTIVE'")
    Flux<AccessConfig> findByCredId(String credId);
}
