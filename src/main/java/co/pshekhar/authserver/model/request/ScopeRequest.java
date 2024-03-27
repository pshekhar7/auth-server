package co.pshekhar.authserver.model.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ScopeRequest {
    @NotNull(message = "scope must not be null")
    private String scope;
    @NotNull(message = "scopeId must not be null")
    private String scopeId;
}
