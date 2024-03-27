package co.pshekhar.authserver.model.response.helper;

import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@ToString
@Getter
public class CredentialData {
    private String clientId;
    private String secret;
    private String expiry;
    private String status;
}
