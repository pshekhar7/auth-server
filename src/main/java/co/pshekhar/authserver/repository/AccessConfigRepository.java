package co.pshekhar.authserver.repository;

import co.pshekhar.authserver.domain.AccessConfig;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccessConfigRepository extends R2dbcRepository<AccessConfig, String> {
}
