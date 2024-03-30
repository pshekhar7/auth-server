package co.pshekhar.authserver.model.response;

import co.pshekhar.authserver.model.response.helper.AccessConfigData;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
@SuperBuilder
@JsonInclude(JsonInclude.Include.NON_NULL)
@ToString
public class AccessConfigResponse extends GenericResponse {
    private String clientId;
    private List<AccessConfigData> data;
}

