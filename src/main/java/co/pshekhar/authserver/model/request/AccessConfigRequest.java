package co.pshekhar.authserver.model.request;

import co.pshekhar.authserver.model.request.helper.AccessRequest;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class AccessConfigRequest {
    @NotNull(message = "clientId must not be null")
    private String clientId;
    @NotNull(message = "targetScopeId must not be null")
    private String targetScopeId;
    @NotNull(message = "access must not be null")
    private AccessRequest access;
}
