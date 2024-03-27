package co.pshekhar.authserver.domain;

import co.pshekhar.authserver.domain.enums.LogOperation;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.ZonedDateTime;

//@Data
@Data
public class Logs {
    @Id
    @Setter(AccessLevel.NONE)
    private Long id;

    private String credId; // FK to Credentials entity; source

    private String targetScope; // FK to Scope entity; target service

    private Boolean success;

    private String accessConfigId;

    private String tenant;

    private String correlationId;

    private LogOperation operation;

    @CreatedDate
    private ZonedDateTime createdOn;

    @LastModifiedDate
    private ZonedDateTime lastUpdatedOn;

}
