package co.pshekhar.authserver.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.util.Map;

@Getter
@SuperBuilder
@JsonInclude(JsonInclude.Include.NON_NULL)
@ToString
public class InternalCredValidationResponse extends GenericResponse {
    private String scopeId;
    private Map<String, Object> contextData;
}

