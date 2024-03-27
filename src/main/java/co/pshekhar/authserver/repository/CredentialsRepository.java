package co.pshekhar.authserver.repository;

import co.pshekhar.authserver.domain.Credentials;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CredentialsRepository extends R2dbcRepository<Credentials, String> {
}
