package co.pshekhar.authserver.domain;

import co.pshekhar.authserver.util.Generator;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
@Getter
@Setter
public class ApiTagConfig extends IdGenerator implements Persistable<String> {
    @Id
    @Setter(AccessLevel.NONE)
    private String id;

    private String tag;
    private String method;
    private String pathRegex;
    private String serviceScopeId; // scope identifier for service scope type

    @CreatedDate
    private LocalDateTime createdOn;

    @LastModifiedDate
    private LocalDateTime lastUpdatedOn;

    @Transient
    private boolean isNewEntry = false;

    @Override
    public boolean isNew() {
        return isNewEntry;
    }

    @Override
    public void initIdentifier() {
        this.id = Generator.getRandomString(null, "apitag_", 15);
    }
}
