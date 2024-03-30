package co.pshekhar.authserver.model.response.helper;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Set;

@Getter
@Setter
@Builder
@ToString
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class AccessResponse {
    @NotBlank(message = "operation must not be empty/blank")
    private String operation;
    @NotBlank(message = "apiTags must not be empty/blank")
    private Set<String> apiTags;
}
