package co.pshekhar.authserver.controller;

import co.pshekhar.authserver.domain.enums.CredStatus;
import co.pshekhar.authserver.domain.enums.LogOperation;
import co.pshekhar.authserver.model.request.AccessConfigRequest;
import co.pshekhar.authserver.model.request.ApiTagRequest;
import co.pshekhar.authserver.model.request.CredentialOpsRequest;
import co.pshekhar.authserver.model.request.CredentialRequest;
import co.pshekhar.authserver.model.request.CredentialRotateRequest;
import co.pshekhar.authserver.model.request.ScopeRequest;
import co.pshekhar.authserver.model.response.AccessConfigResponse;
import co.pshekhar.authserver.model.response.CredentialsResponse;
import co.pshekhar.authserver.model.response.GenericResponse;
import co.pshekhar.authserver.model.response.ScopeResponse;
import co.pshekhar.authserver.model.response.Status;
import co.pshekhar.authserver.service.AdminService;
import co.pshekhar.authserver.service.LogService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

import java.util.concurrent.ExecutorService;

@RestController
@RequestMapping("/admin")
public class AdminController {
    private final AdminService adminService;
    private final ExecutorService executorService;
    private final LogService logService;
    private final Scheduler scheduler;

    public AdminController(AdminService adminService,
                           @Qualifier("virtualThreadExecutor") ExecutorService executorService,
                           LogService logService,
                           @Qualifier("virtualThreadScheduler") Scheduler scheduler) {
        this.adminService = adminService;
        this.executorService = executorService;
        this.logService = logService;
        this.scheduler = scheduler;
    }

    @PostMapping(value = "/scope", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    Mono<ResponseEntity<ScopeResponse>> createScope(@Valid @RequestBody ScopeRequest request) {
        return adminService.createScope(request)
                .map(response -> {
                    executorService.execute(() -> logService.logScope(request, response));
                    HttpStatus httpStatus = response.getStatus() == Status.SUCCESS ? HttpStatus.OK : HttpStatus.CONFLICT;
                    return ResponseEntity.status(httpStatus).body(response);
                })
                .subscribeOn(scheduler);
    }

    @PostMapping(value = "/scope/details", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    Mono<ResponseEntity<ScopeResponse>> getScope(@Valid @RequestBody ScopeRequest request) {
        return adminService.getScope(request)
                .map(response -> {
                    HttpStatus httpStatus = response.getStatus() == Status.SUCCESS ? HttpStatus.OK : HttpStatus.NOT_FOUND;
                    return ResponseEntity.status(httpStatus).body(response);
                })
                .subscribeOn(scheduler);
    }

    @PostMapping(value = "/cred/issue", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    Mono<ResponseEntity<CredentialsResponse>> issueCredentials(@Valid @RequestBody CredentialRequest request) {
        return adminService.issueCredentials(request)
                .map(response -> {
                    executorService.execute(() -> logService.logCredentials(request.getClientId(), response, LogOperation.CRED_CREATE));
                    HttpStatus httpStatus = response.getStatus() == Status.SUCCESS ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
                    return ResponseEntity.status(httpStatus).body(response);
                })
                .subscribeOn(scheduler);
    }

    @PostMapping(value = "/cred/activate", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    Mono<ResponseEntity<CredentialsResponse>> activateCredentials(@Valid @RequestBody CredentialOpsRequest request) {
        return adminService.updateCredentialStatus(request, CredStatus.ACTIVE)
                .map(response -> {
                    executorService.execute(() -> logService.logCredentials(request.getClientId(), response, LogOperation.CRED_ACTIVATE));
                    HttpStatus httpStatus = response.getStatus() == Status.SUCCESS ? HttpStatus.OK : HttpStatus.NOT_FOUND;
                    return ResponseEntity.status(httpStatus).body(response);
                })
                .subscribeOn(scheduler);
    }

    @PostMapping(value = "/cred/expire", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    Mono<ResponseEntity<CredentialsResponse>> expireCredentials(@Valid @RequestBody CredentialOpsRequest request) {
        return adminService.updateCredentialStatus(request, CredStatus.EXPIRED)
                .map(response -> {
                    executorService.execute(() -> logService.logCredentials(request.getClientId(), response, LogOperation.CRED_EXPIRE));
                    HttpStatus httpStatus = response.getStatus() == Status.SUCCESS ? HttpStatus.OK : HttpStatus.NOT_FOUND;
                    return ResponseEntity.status(httpStatus).body(response);
                })
                .subscribeOn(scheduler);
    }

    @PostMapping(value = "/cred/summary", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    Mono<ResponseEntity<CredentialsResponse>> credentialsSummary(@Valid @RequestBody CredentialOpsRequest request) {
        return adminService.getCredentialsSummary(request)
                .map(response -> {
                    HttpStatus httpStatus = response.getStatus() == Status.SUCCESS ? HttpStatus.OK : HttpStatus.NOT_FOUND;
                    return ResponseEntity.status(httpStatus).body(response);
                })
                .subscribeOn(scheduler);
    }

    @PostMapping(value = "/cred/rotate", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    Mono<ResponseEntity<CredentialsResponse>> rotateCredentials(@Valid @RequestBody CredentialRotateRequest request) {
        return adminService.rotateCredentials(request)
                .map(response -> {
                    executorService.execute(() -> logService.logCredentials(request.getClientId(), response, LogOperation.CRED_ROTATE));
                    HttpStatus httpStatus = response.getStatus() == Status.SUCCESS ? HttpStatus.OK : HttpStatus.NOT_FOUND;
                    return ResponseEntity.status(httpStatus).body(response);
                })
                .subscribeOn(scheduler);
    }

    @PostMapping(value = "/cred/accessConfig", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    Mono<ResponseEntity<GenericResponse>> updateAccessConfig(@Valid @RequestBody AccessConfigRequest request) {
        return adminService.updateAccessForCredential(request)
                .map(response -> {
                    executorService.execute(() -> logService.logAccessConfig(request, response));
                    HttpStatus httpStatus = response.getStatus() == Status.SUCCESS ? HttpStatus.OK : HttpStatus.NOT_FOUND;
                    return ResponseEntity.status(httpStatus).body(response);
                })
                .subscribeOn(scheduler);
    }

    @PostMapping(value = "/cred/accessConfig/summary", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    Mono<ResponseEntity<AccessConfigResponse>> accessConfigSummary(@Valid @RequestBody CredentialOpsRequest request) {
        return adminService.accessConfigSummary(request)
                .map(response -> {
                    HttpStatus httpStatus = response.getStatus() == Status.SUCCESS ? HttpStatus.OK : HttpStatus.NOT_FOUND;
                    return ResponseEntity.status(httpStatus).body(response);
                })
                .subscribeOn(scheduler);
    }

    @PostMapping(value = "/config/apiTag", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    Mono<ResponseEntity<GenericResponse>> addApiTag(@Valid @RequestBody ApiTagRequest request) {
        return adminService.addApiTag(request)
                .map(response -> {
                    HttpStatus httpStatus = response.getStatus() == Status.SUCCESS ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
                    return ResponseEntity.status(httpStatus).body(response);
                })
                .subscribeOn(scheduler);
    }
}
