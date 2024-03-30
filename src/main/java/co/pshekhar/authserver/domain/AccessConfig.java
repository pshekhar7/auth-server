package co.pshekhar.authserver.domain;

import co.pshekhar.authserver.domain.enums.AccessConfigStatus;
import co.pshekhar.authserver.domain.enums.AccessMode;
import co.pshekhar.authserver.util.Generator;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;

import java.time.LocalDateTime;
import java.util.Set;

@Data
public class AccessConfig extends IdGenerator implements Persistable<String> {
    public AccessConfig() {
        this.status = AccessConfigStatus.ACTIVE;
        this.isNewEntry = true;
    }

    @Id
    @Setter(AccessLevel.NONE)
    private String id;

    private String credId; // FK to Credentials entity; source

    private String scopeId; // FK to Scope entity; target service

    private AccessMode accessMode;

    private Set<String> accessApiList;

    private AccessConfigStatus status;

    @CreatedDate
    private LocalDateTime createdOn;

    @LastModifiedDate
    private LocalDateTime lastUpdatedOn;

    @Transient
    private boolean isNewEntry;

    @Override
    public boolean isNew() {
        return isNewEntry;
    }

    @Override
    public void initIdentifier() {
        this.id = Generator.getRandomString("acsconf_", null, 13);
    }
}
