package co.pshekhar.authserver.model.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class CredentialOpsRequest {
    @NotNull(message = "scope must not be null")
    private String clientId;
}
