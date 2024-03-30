package co.pshekhar.authserver.repository;

import co.pshekhar.authserver.domain.Credentials;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface CredentialsRepository extends R2dbcRepository<Credentials, String> {
    @Query(value = "select * from credentials where client_id = :id and status != 'EXPIRED'")
    @NotNull
    Mono<Credentials> findById(@NotNull String id);

    @Query(value = "select * from credentials where client_id = :id")
    @NotNull
    Mono<Credentials> findAnyById(@NotNull String id);
}
