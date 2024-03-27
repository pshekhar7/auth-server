package co.pshekhar.authserver.service;

import co.pshekhar.authserver.domain.Scope;
import co.pshekhar.authserver.domain.enums.ScopeType;
import co.pshekhar.authserver.model.request.ScopeRequest;
import co.pshekhar.authserver.model.response.ScopeData;
import co.pshekhar.authserver.model.response.ScopeResponse;
import co.pshekhar.authserver.model.response.Status;
import co.pshekhar.authserver.repository.ScopeRepository;
import co.pshekhar.authserver.util.Utilities;
import lombok.NonNull;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class AdminService {

    private final ScopeRepository scopeRepository;

    public AdminService(ScopeRepository scopeRepository) {
        this.scopeRepository = scopeRepository;
    }

    public Mono<ScopeResponse> createScope(@NonNull ScopeRequest request) {
        // check existing record based on scope and scopeId; if exists, return error else return new data
        return scopeRepository.findByTypeAndIdentifier(ScopeType.valueOf(request.getScope()), request.getScopeId())
                .log()
                .map(existScope -> (ScopeResponse) ScopeResponse.builder().status(Status.FAILURE).reason("Scope with supplied data already exists!").build())
                .switchIfEmpty(saveNewScope(request));
    }

    public Mono<ScopeResponse> getScope(@NonNull ScopeRequest request) {
        // check existing record based on scope and scopeId; if exists, return details else return error
        return scopeRepository.findByTypeAndIdentifier(ScopeType.valueOf(request.getScope()), request.getScopeId())
                .log()
                .map(existScope -> (ScopeResponse) ScopeResponse.builder()
                        .status(Status.SUCCESS)
                        .data(ScopeData.builder()
                                .scope(existScope.getType().toString())
                                .scopeId(existScope.getIdentifier())
                                .createdOn(Utilities.formatDate(existScope.getCreatedOn()))
                                .build())
                        .build())
                .switchIfEmpty(Mono.just(ScopeResponse.builder().status(Status.FAILURE).reason("Not found").build()));
    }

    private Mono<ScopeResponse> saveNewScope(ScopeRequest request) {
        Scope scope = new Scope();
        scope.initIdentifier();
        scope.setIdentifier(request.getScopeId());
        scope.setType(ScopeType.valueOf(request.getScope()));
        scope.setNewEntry(true);
        return scopeRepository.save(scope)
                .map(
                        savedScope -> ScopeResponse.builder()
                                .status(Status.SUCCESS)
                                .data(ScopeData.builder()
                                        .scope(savedScope.getType().toString())
                                        .scopeId(savedScope.getIdentifier())
                                        .createdOn(Utilities.formatDate(savedScope.getCreatedOn()))
                                        .build())
                                .build());
    }
}
