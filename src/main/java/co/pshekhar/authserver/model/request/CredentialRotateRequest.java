package co.pshekhar.authserver.model.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class CredentialRotateRequest {
    public CredentialRotateRequest() {
        this.activate = false;
        this.rotateAfter = 180;
    }

    @NotNull(message = "scope must not be null")
    private String clientId;
    private boolean activate;
    private Integer rotateAfter;
}
