package co.pshekhar.authserver.model.response;

import co.pshekhar.authserver.model.response.helper.CredentialData;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@ToString
public class CredentialsResponse extends GenericResponse {
    private CredentialData data;
}

