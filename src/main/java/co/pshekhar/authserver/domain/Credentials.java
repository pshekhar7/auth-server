package co.pshekhar.authserver.domain;

import co.pshekhar.authserver.domain.enums.CredStatus;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;

import java.time.LocalDateTime;
import java.util.Map;

@Data
public class Credentials implements Persistable<String> {
    @Id
    private String clientId;

    private String clientSecret;

    private LocalDateTime expiry;

    private LocalDateTime lastRotatedOn;

    String scopeId; // FK to Scope entity

    private CredStatus status;

    @CreatedDate
    private LocalDateTime createdOn;

    @LastModifiedDate
    private LocalDateTime lastUpdatedOn;

    @Transient
    private boolean isNewEntry = false;

    private Map<String, Object> contextData;

    @Override
    public String getId() {
        return this.getClientId();
    }

    @Override
    public boolean isNew() {
        return isNewEntry;
    }

}
