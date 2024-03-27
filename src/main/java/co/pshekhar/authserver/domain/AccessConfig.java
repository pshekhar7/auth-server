package co.pshekhar.authserver.domain;

import co.pshekhar.authserver.domain.enums.AccessConfigStatus;
import co.pshekhar.authserver.domain.enums.AccessMode;
import co.pshekhar.authserver.util.Generator;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;

import java.time.ZonedDateTime;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Data
public class AccessConfig extends IdGenerator implements Persistable<String> {
    @Id
    @Setter(AccessLevel.NONE)
    private String id;

    private String credId; // FK to Credentials entity; source

    private String scopeId; // FK to Scope entity; target service

    private AccessMode accessMode;

    private Set<String> accessApiList;

    private AccessConfigStatus status;

    @CreatedDate
    private ZonedDateTime createdOn;

    @LastModifiedDate
    private ZonedDateTime lastUpdatedOn;

    @Transient
    private boolean isNewEntry = false;

    @Override
    public boolean isNew() {
        return isNewEntry;
    }

    @Override
    public void initIdentifier() {
        this.id = Generator.getRandomString("scp_", null, 13);
    }
}
