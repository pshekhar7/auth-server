package co.pshekhar.authserver.domain;

import co.pshekhar.authserver.domain.enums.LogOperation;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;
import java.util.Map;

//@Data
@Data
public class Logs {
    @Id
    @Setter(AccessLevel.NONE)
    private Long id;

    private String credId; // FK to Credentials entity; source; for login/credential related logging

    private String targetScope; // FK to Scope entity; target service; for login related logging

    private Boolean success;

    private String accessConfigId; // for accessConfig related logging

    private String scopeId; // for scope related logging

    private String scope; // for scope related logging

    private String tenant; // for login related logging

    private String correlationId; // for login related logging

    private String traceId;

    private LogOperation operation;

    private String failureReason;

    private Map<String, String> loginData;

    @CreatedDate
    private LocalDateTime createdOn;

    @LastModifiedDate
    private LocalDateTime lastUpdatedOn;

}
