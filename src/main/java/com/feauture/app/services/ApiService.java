package com.feauture.app.services;

import java.time.Instant;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.feauture.app.dto.ApiRequest;
import com.feauture.app.dto.ApiResponse;
import com.feauture.app.entity.AppApiKeys;
import com.feauture.app.repository.ApiRepository;
import com.feauture.app.repository.ApplicationRepository;
import com.feauture.app.util.ApiKeyUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ApiService {

    private final ApplicationRepository applicationRepository;
    private final ApiRepository apiRepository;

    public ApiResponse<Map<String, Object>> getApis(ApiRequest.PageQuery query) {
        Pageable page = PageRequest.of(query.page() - 1, query.limit(), Sort.by("updated_at").descending());
        Specification<AppApiKeys> specs = null;
        if (query.search() != null) {
            specs = (root, cq, cb) -> cb.like(cb.lower(root.get("name")), "%" + query.search().toLowerCase() + "%");
        }

        Page<AppApiKeys> result = apiRepository.findAll(specs, page);
        Map<String, Object> data = Map.of(
                "list", result.getContent(),
                "total", result.getTotalElements());
        return ApiResponse.success("Fetcted Successfully !", data);
    }

    public ApiResponse<Void> saveApi(ApiRequest.SaveApi request) {

        boolean isExists = applicationRepository.existsByIDAndStatus(request.app_id());

        if (!isExists) {
            throw this.ThrowError(HttpStatus.NOT_FOUND, "Invalid App ID");
        }

        String rawKey = ApiKeyUtil.generateKey();

        AppApiKeys app = new AppApiKeys();
        app.setName(request.name());
        app.setKey(ApiKeyUtil.hashKey(rawKey));
        app.setUpdated_at(Instant.now());
        app.setApp(applicationRepository.getReferenceById(request.app_id()));
        app.setStatus(true);
        apiRepository.save(app);
        return ApiResponse.success("API Key Saved Successfully !", null);

    }

    @Transactional
    public ApiResponse<Void> updateApi(ApiRequest.EditApi request) {

        boolean isExists = applicationRepository.existsByIDAndStatus(request.app_id());

        if (!isExists) {
            throw this.ThrowError(HttpStatus.NOT_FOUND, "Invalid App ID");
        }
        int response = apiRepository.updateApp(request.id(), request.name(),
                applicationRepository.getReferenceById(request.app_id()), Instant.now());

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
