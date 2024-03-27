package co.pshekhar.authserver.model.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CredentialRequest {
    @NotNull(message = "scope must not be null")
    private String clientId;
    @NotNull(message = "scope must not be null")
    private String scope;
    @NotNull(message = "scopeId must not be null")
    private String scopeId;
    @NotNull(message = "initStatus must not be null")
    private String initStatus;
    @NotNull(message = "rotateAfter must not be null")
    private Integer rotateAfter;

}
