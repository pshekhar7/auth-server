package co.pshekhar.authserver.controller;

import co.pshekhar.authserver.domain.enums.CredStatus;
import co.pshekhar.authserver.model.request.CredentialOpsRequest;
import co.pshekhar.authserver.model.request.CredentialRequest;
import co.pshekhar.authserver.model.request.CredentialRotateRequest;
import co.pshekhar.authserver.model.request.ScopeRequest;
import co.pshekhar.authserver.model.response.CredentialsResponse;
import co.pshekhar.authserver.model.response.ScopeResponse;
import co.pshekhar.authserver.model.response.Status;
import co.pshekhar.authserver.service.AdminService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/admin")
public class AdminController {
    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @PostMapping(value = "/scope", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    Mono<ResponseEntity<ScopeResponse>> createScope(@Valid @RequestBody ScopeRequest request) {
        return adminService.createScope(request)
                .map(response -> {
                    HttpStatus httpStatus = response.getStatus() == Status.SUCCESS ? HttpStatus.OK : HttpStatus.CONFLICT;
                    return ResponseEntity.status(httpStatus).body(response);
                });
    }

    @PostMapping(value = "/scope/details", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    Mono<ResponseEntity<ScopeResponse>> getScope(@Valid @RequestBody ScopeRequest request) {
        return adminService.getScope(request)
                .map(response -> {
                    HttpStatus httpStatus = response.getStatus() == Status.SUCCESS ? HttpStatus.OK : HttpStatus.NOT_FOUND;
                    return ResponseEntity.status(httpStatus).body(response);
                });
    }

    @PostMapping(value = "/cred/issue", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    Mono<ResponseEntity<CredentialsResponse>> issueCredentials(@Valid @RequestBody CredentialRequest request) {
        return adminService.issueCredentials(request)
                .map(response -> {
                    HttpStatus httpStatus = response.getStatus() == Status.SUCCESS ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
                    return ResponseEntity.status(httpStatus).body(response);
                });
    }

    @PostMapping(value = "/cred/activate", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    Mono<ResponseEntity<CredentialsResponse>> activateCredentials(@Valid @RequestBody CredentialOpsRequest request) {
        return adminService.updateCredentialStatus(request, CredStatus.ACTIVE)
                .map(response -> {
                    HttpStatus httpStatus = response.getStatus() == Status.SUCCESS ? HttpStatus.OK : HttpStatus.NOT_FOUND;
                    return ResponseEntity.status(httpStatus).body(response);
                });
    }

    @PostMapping(value = "/cred/expire", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    Mono<ResponseEntity<CredentialsResponse>> expireCredentials(@Valid @RequestBody CredentialOpsRequest request) {
        return adminService.updateCredentialStatus(request, CredStatus.EXPIRED)
                .map(response -> {
                    HttpStatus httpStatus = response.getStatus() == Status.SUCCESS ? HttpStatus.OK : HttpStatus.NOT_FOUND;
                    return ResponseEntity.status(httpStatus).body(response);
                });
    }

    @PostMapping(value = "/cred/summary", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    Mono<ResponseEntity<CredentialsResponse>> credentialsSummary(@Valid @RequestBody CredentialOpsRequest request) {
        return adminService.getCredentialsSummary(request)
                .map(response -> {
                    HttpStatus httpStatus = response.getStatus() == Status.SUCCESS ? HttpStatus.OK : HttpStatus.NOT_FOUND;
                    return ResponseEntity.status(httpStatus).body(response);
                });
    }

    @PostMapping(value = "/cred/rotate", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    Mono<ResponseEntity<CredentialsResponse>> rotateCredentials(@Valid @RequestBody CredentialRotateRequest request) {
        return adminService.rotateCredentials(request)
                .map(response -> {
                    HttpStatus httpStatus = response.getStatus() == Status.SUCCESS ? HttpStatus.OK : HttpStatus.NOT_FOUND;
                    return ResponseEntity.status(httpStatus).body(response);
                });
    }
}
