package co.pshekhar.authserver.repository;

import co.pshekhar.authserver.domain.ApiTagConfig;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface ApiTagConfigRepository extends R2dbcRepository<ApiTagConfig, String> {
    @Query(value = "select * from api_tag_config where service_scope_id = :serviceScopeId and method = :method")
    Flux<ApiTagConfig> findByServiceScopeIdAndMethod(String serviceScopeId, String method);
    Mono<ApiTagConfig> findByServiceScopeIdAndTag(String serviceScopeId, String tag);
}
