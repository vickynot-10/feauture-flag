package com.feauture.app.services;

import java.time.Instant;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.feauture.app.dto.ApiResponse;
import com.feauture.app.dto.FlagRequest;
import com.feauture.app.entity.AppApplications;
import com.feauture.app.entity.AppFlagVersions;
import com.feauture.app.entity.AppFlags;
import com.feauture.app.repository.AppFlagRepository;
import com.feauture.app.repository.AppFlagVersionRepository;
import com.feauture.app.repository.ApplicationRepository;
import com.feauture.app.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FlagService {

    private final AppFlagRepository appFlagRepository;
    private final UserRepository userRepository;
    private final ApplicationRepository applicationRepository;
    private final AppFlagVersionRepository appFlagVersionRepository;

    @Transactional
    public ApiResponse<Void> saveFlag(FlagRequest.saveFlag request, Long userId) {
        if (appFlagRepository.existsByApp_IdAndFlagKey(request.app(), request.flagKey())) {
            throw ThrowError(HttpStatus.CONFLICT, "A Flag with similar Key and App Already Exists !");
        }
        AppFlags app = new AppFlags();
        app.setEnabled(true);
        app.setFlagKey(request.flagKey());
        app.setApp(applicationRepository.getReferenceById(request.app()));
        appFlagRepository.save(app);
        insertVersion(app, 1, request.definition(), userId);
        return ApiResponse.success("Flag Created Successfully !", null);
    }

    @Transactional
    public ApiResponse<Void> editFlag(FlagRequest.editFlag request, Long userId) {

        int nextversion = request.expectedVersion() + 1;
        int updated = appFlagRepository.insertVersion(request.id(), request.expectedVersion(), nextversion,
                Instant.now());

        if (updated == 0) {
            if (!appFlagRepository.existsById(request.id())) {
                throw ThrowError(HttpStatus.NOT_FOUND, "Flag is Not Found !");
            }
            throw ThrowError(HttpStatus.CONFLICT, "Flag was changed by someone else, reload and retry !");
        }
        insertVersion(appFlagRepository.getReferenceById(request.id()), nextversion, request.definition(), userId);

        return ApiResponse.success("Flag updated Successfully !", null);
    }

    private void insertVersion(AppFlags flag, int version, Map<String, Object> definition, Long userId) {

        AppFlagVersions appVersion = new AppFlagVersions();
        appVersion.setVersion(version);
        appVersion.setFlag(flag);
        appVersion.setDefinition(definition);
        appVersion.setCreatedBy(userRepository.getReferenceById(userId));
        appFlagVersionRepository.save(appVersion);

    }

    private ResponseStatusException ThrowError(HttpStatus status, String msg) {
        return new ResponseStatusException(status, msg);

    }

}
