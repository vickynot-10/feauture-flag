package com.feauture.app.services;

import java.time.Instant;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.feauture.app.dto.ApiRequest;
import com.feauture.app.dto.ApiResponse;
import com.feauture.app.entity.AppApiKeys;
import com.feauture.app.repository.ApiRepository;
import com.feauture.app.repository.ApplicationRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ApiService {

    final ApplicationRepository applicationRepository;
    final ApiRepository apiRepository;

    public ApiResponse<Void> saveApi(ApiRequest.SaveApi request) {

        boolean isExists = applicationRepository.existsByIDAndStatus(request.app_id());

        if (!isExists) {
            throw this.ThrowError(HttpStatus.NOT_FOUND, "Invalid App ID");
        }

        AppApiKeys app = new AppApiKeys();
        app.setName(request.name());
        app.setUpdated_at(Instant.now());

        apiRepository.save(app);
        return ApiResponse.success("API Key Saved Successfully !", null);

    }

    @Transactional
    public ApiResponse<Void> updateApi(ApiRequest.EditApi request) {

        boolean isExists = applicationRepository.existsByIDAndStatus(request.id());

        if (!isExists) {
            throw this.ThrowError(HttpStatus.NOT_FOUND, "Invalid App ID");
        }
        int response = apiRepository.updateApp(request.id(), request.name(), request.app_id(), Instant.now());

        if (response <= 0) {
            throw this.ThrowError(HttpStatus.NOT_FOUND, "No API key is found !");
        }

        return ApiResponse.success("API Key Updated Successfully !", null);

    }

    @Transactional
    public ApiResponse<Void> updateStatusApi(ApiRequest.StatusUpdate request) {
        int response = apiRepository.updateStatusApi(request.id(), Instant.now());

        if (response <= 0) {
            throw this.ThrowError(HttpStatus.NOT_FOUND, "No API key is found !");
        }

        return ApiResponse.success("API Key Status Updated Successfully !", null);
    }

    @Transactional
    public ApiResponse<Void> deleteApi(Long id) {
        final int response = apiRepository.deleteApi(id);

        if (response <= 0) {
            throw this.ThrowError(HttpStatus.NOT_FOUND, "No API key is found !");
        }
        return ApiResponse.success("API Deleted Successfully !", null);
    }

    private ResponseStatusException ThrowError(HttpStatus status, String msg) {
        return new ResponseStatusException(status, msg);

    }

}
