package com.feauture.app.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.feauture.app.dto.ApiRequest;
import com.feauture.app.dto.ApiResponse;

import com.feauture.app.services.ApiService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;

import java.util.Map;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/keys")

public class AppApiController {

    private final ApiService apiService;

    @GetMapping
    public ApiResponse<Map<String, Object>> getApps(@Valid ApiRequest.PageQuery query) {
        return apiService.getApis(query);
    }

    @PostMapping
    public ApiResponse<String> saveApi(@RequestBody @Valid ApiRequest.SaveApi request) {
        return apiService.saveApi(request);
    }

    @PutMapping
    public ApiResponse<Void> editApi(@RequestBody @Valid ApiRequest.EditApi request) {
        return apiService.updateApi(request);
    }

    @PatchMapping
    public ApiResponse<Void> statusUpdateApi(@RequestBody @Valid ApiRequest.StatusUpdate request) {
        return apiService.updateStatusApi(request);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteApi(@PathVariable @Positive Long id) {
        return apiService.deleteApi(id);
    }
}
