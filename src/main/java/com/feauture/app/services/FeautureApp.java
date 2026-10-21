package com.feauture.app.services;

import java.util.Map;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

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

    @Transactional(readOnly = true)
    public ApiResponse<Map<String, Object>> getApps(AppRequest.PageQuery query) {
        Pageable pageable = PageRequest.of(query.page() - 1, query.limit(), Sort.by("id").descending());
        Specification<AppApplications> spec = null;
        if (query.search() != null) {
            spec = nameContains(query.search());
        }
        Page<AppApplications> result = applicationRepository.findAll(spec, pageable);
        Map<String, Object> data = Map.of(
                "total", result.getTotalElements(),
                "list", result.getContent());
        return ApiResponse.success("Applications fetched Successfully !", data);

    }

    private Specification<AppApplications> nameContains(String str) {
        return (root, cq, cb) -> cb.like(cb.lower(root.get("name")), "%" + str.toLowerCase() + "%");
    }

    @Transactional
    public ApiResponse<Void> editApp(AppRequest.EditApp request) {
        final int response = applicationRepository.updateApp(request.id(), request.name(), request.environment());
        if (response == 0) {
            throw ApiResponse.error(HttpStatus.NOT_FOUND, "App not found !");
        }
        return ApiResponse.success("Application updated Successfully !", null);
    }

    @Transactional
    public ApiResponse<Void> deleteApp(Long id) {
        final int response = applicationRepository.deleteApp(id);
        if (response == 0) {
            throw ApiResponse.error(HttpStatus.NOT_FOUND, "App not found !");
        }
        return ApiResponse.success("Application Deleted Successfully !", null);
    }

    @Transactional
    public ApiResponse<Void> updateStatusApp(AppRequest.StatusUpdateApp request) {
        final int response = applicationRepository.updateStatusApp(request.id(), request.status());
        if (response == 0) {
            throw ApiResponse.error(HttpStatus.NOT_FOUND, "App not found !");
        }

        return ApiResponse.success("Application Status updated Successfully !", null);
    }

}
