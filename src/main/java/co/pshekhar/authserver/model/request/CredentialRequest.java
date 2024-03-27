package co.pshekhar.authserver.model.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class CredentialRequest {
    public CredentialRequest() {
        this.activate = false;
    }

    @NotNull(message = "scope must not be null")
    private String clientId;
    @NotNull(message = "scope must not be null")
    private String scope;
    @NotNull(message = "scopeId must not be null")
    private String scopeId;
    private boolean activate;
    @NotNull(message = "rotateAfter must not be null")
    private Integer rotateAfter;
    private Map<String, Object> contextData;
}
