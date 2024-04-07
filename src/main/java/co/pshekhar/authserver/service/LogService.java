package co.pshekhar.authserver.service;

import co.pshekhar.authserver.domain.Logs;
import co.pshekhar.authserver.domain.enums.LogOperation;
import co.pshekhar.authserver.model.request.AccessConfigRequest;
import co.pshekhar.authserver.model.request.ScopeRequest;
import co.pshekhar.authserver.model.response.CredentialsResponse;
import co.pshekhar.authserver.model.response.GenericResponse;
import co.pshekhar.authserver.model.response.InternalAuthResponse;
import co.pshekhar.authserver.model.response.ScopeResponse;
import co.pshekhar.authserver.model.response.Status;
import co.pshekhar.authserver.repository.LogsRepository;
import co.pshekhar.authserver.util.Constant;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import reactor.core.scheduler.Scheduler;

import java.util.HashMap;
import java.util.Map;

@Service
public class LogService {
    private final LogsRepository logsRepository;
    private final Scheduler scheduler;


    public LogService(LogsRepository logsRepository,
                      @Qualifier("virtualThreadScheduler") Scheduler scheduler) {
        this.logsRepository = logsRepository;
        this.scheduler = scheduler;
    }

    public void logScope(ScopeRequest request, ScopeResponse response) {
        Logs logs = new Logs();
        logs.setOperation(LogOperation.SCOPE_CREATION);
        logs.setSuccess(response.getStatus() == Status.SUCCESS);
        logs.setFailureReason(response.getReason());
        logs.setScopeId(request.getScopeId());
        logs.setScope(request.getScope());
        logsRepository.save(logs)
                .publishOn(scheduler)
                .subscribeOn(scheduler)
                .log()
                .subscribe();
    }

    public void logCredentials(String clientId, CredentialsResponse response, LogOperation operation) {
        Logs logs = new Logs();
        logs.setOperation(operation);
        logs.setSuccess(response.getStatus() == Status.SUCCESS);
        logs.setFailureReason(response.getReason());
        logs.setCredId(clientId);
        logsRepository.save(logs)
                .publishOn(scheduler)
                .subscribeOn(scheduler)
                .subscribe();
    }

    public void logAccessConfig(AccessConfigRequest request, GenericResponse response) {
        Logs logs = new Logs();
        logs.setOperation(LogOperation.ACCESS_UPDATE);
        logs.setSuccess(response.getStatus() == Status.SUCCESS);
        logs.setFailureReason(response.getReason());
        logs.setCredId(request.getClientId());
        logs.setTargetScope(request.getTargetScopeId());
        logsRepository.save(logs)
                .publishOn(scheduler)
                .subscribeOn(scheduler)
                .subscribe();
    }

    public void logLoginAttempt(HttpHeaders reqHeaders, InternalAuthResponse response) {
        Map<String, String> loginData = new HashMap<>();
        loginData.put(Constant.HEADER_TARGET_PATH, reqHeaders.getFirst(Constant.HEADER_TARGET_PATH));
        loginData.put(Constant.HEADER_TARGET_HTTP_METHOD, reqHeaders.getFirst(Constant.HEADER_TARGET_HTTP_METHOD));
        loginData.put(Constant.HEADER_SOURCE_IP, reqHeaders.getFirst(Constant.HEADER_SOURCE_IP));
        loginData.put(Constant.HEADER_DEVICE_ID, reqHeaders.getFirst(Constant.HEADER_DEVICE_ID));

        Logs logs = new Logs();
        logs.setOperation(LogOperation.LOGIN);
        logs.setSuccess(response.getStatus() == Status.SUCCESS);
        logs.setFailureReason(response.getReason());
        logs.setCredId(reqHeaders.getFirst(Constant.HEADER_CLIENT_ID));
        logs.setTargetScope(reqHeaders.getFirst(Constant.HEADER_TARGET_SERVICE));
        logs.setLoginData(loginData);
        logs.setTenant(reqHeaders.getFirst(Constant.HEADER_TENANT_ID));
        logs.setCorrelationId(reqHeaders.getFirst(Constant.HEADER_CORRELATION_ID));
        logsRepository.save(logs)
                .publishOn(scheduler)
                .subscribeOn(scheduler)
                .subscribe();
    }
}
