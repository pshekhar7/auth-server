package co.pshekhar.authserver.model.request.helper;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class AccessRequest {
    @NotBlank(message = "operation must not be empty/blank")
    private String operation;
    @NotBlank(message = "apiTags must not be empty/blank")
    private Set<String> apiTags;
}
