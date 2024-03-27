package co.pshekhar.authserver.model.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ScopeRequest {
    @NotNull(message = "scope must not be null")
    private String scope;
    @NotNull(message = "scopeId must not be null")
    private String scopeId;
}
