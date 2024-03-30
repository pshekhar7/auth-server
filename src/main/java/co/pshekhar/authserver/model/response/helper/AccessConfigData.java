package co.pshekhar.authserver.model.response.helper;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@ToString
@Getter
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class AccessConfigData {
    private String targetScopeId;
    private AccessResponse access;
}
