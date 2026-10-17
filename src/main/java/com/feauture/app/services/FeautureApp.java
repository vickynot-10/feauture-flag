package com.feauture.app.services;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.feauture.app.constants.AppConstants;
import com.feauture.app.dto.ApiResponse;
import com.feauture.app.dto.AppRequest;
import com.feauture.app.entity.AppApplications;
import com.feauture.app.repository.ApplicationRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FeautureApp {

    private final ApplicationRepository applicationRepository;

    public ApiResponse<Void> saveApp(AppRequest.SaveApp request) {
        AppApplications app = new AppApplications();
        app.setName(request.name());
        app.setEnvironment(request.environment());
        app.setStatus(true);
        applicationRepository.save(app);
        return ApiResponse.success("Application Created Successfully !", null);
    }

    @Transactional
    public ApiResponse<Void> editApp(AppRequest.EditApp request) {
        final int response = applicationRepository.updateApp(request.id(), request.name(), request.environment());
        if (response == 0) {
            throw invalidException();
        }
        return ApiResponse.success("Application updated Successfully !", null);
    }

    public ApiResponse<Void> deleteApp(Long id) {
        final int response = applicationRepository.deleteApp(id);
        if (response == 0) {
            throw invalidException();
        }
        return ApiResponse.success("Application Deleted Successfully !", null);
    }

    @Transactional
    public ApiResponse<Void> updateStatusApp(AppRequest.StatusUpdateApp request) {
        final int response = applicationRepository.updateStatusApp(request.id(), request.status());
        if (response == 0) {
            throw invalidException();
        }

        return ApiResponse.success("Application Status updated Successfully !", null);
    }

    private ResponseStatusException invalidException() {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, "Application Not Found !");
    }
}
