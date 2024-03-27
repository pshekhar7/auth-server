package co.pshekhar.authserver.model.response;

import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@ToString
@Getter
public class ScopeData {
    private String scope;
    private String scopeId;
    private String createdOn;
}
