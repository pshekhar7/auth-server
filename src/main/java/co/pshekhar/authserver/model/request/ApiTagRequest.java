package co.pshekhar.authserver.model.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ApiTagRequest {
    @NotNull(message = "tag must not be null")
    private String tag;
    @NotNull(message = "method must not be null")
    private String method;
    @NotNull(message = "path must not be null")
    private String path;
    @NotNull(message = "serviceScopeId must not be null")
    private String serviceScopeId;
}
