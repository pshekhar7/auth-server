package co.pshekhar.authserver.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import reactor.core.publisher.Mono;

@Getter
@SuperBuilder
@JsonInclude(JsonInclude.Include.NON_NULL)
@ToString
public class InternalAuthResponse extends GenericResponse {
    private Mono<String> jwt;
}

