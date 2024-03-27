package co.pshekhar.authserver.repository;

import co.pshekhar.authserver.domain.Logs;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LogsRepository extends R2dbcRepository<Logs, Long> {
}
