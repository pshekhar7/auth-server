package co.pshekhar.authserver.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.experimental.SuperBuilder;


@SuperBuilder
@Getter
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class GenericResponse {
    private Status status;
    private String reason;
}
