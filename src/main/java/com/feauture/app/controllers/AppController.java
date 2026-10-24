package com.feauture.app.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.feauture.app.dto.ApiResponse;
import com.feauture.app.dto.AppRequest;
import com.feauture.app.entity.AppApplications;
import com.feauture.app.services.FeautureApp;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/app")
public class AppController {
    private final FeautureApp feautureApp;

    @GetMapping
    public ApiResponse<Map<String, Object>> getApps(@Valid AppRequest.PageQuery query) {
        return feautureApp.getApps(query);
    }

    @GetMapping("/get-apps")
    public ApiResponse<List<AppApplications>> getAppsList() {
        return feautureApp.getAppList();
    }

    @PostMapping
    public ApiResponse<Void> saveApp(@Valid @RequestBody AppRequest.SaveApp request) {

        return feautureApp.saveApp(request);
    }

    @PutMapping
    public ApiResponse<Void> editApp(@Valid @RequestBody AppRequest.EditApp request) {

        return feautureApp.editApp(request);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteApp(@PathVariable @Positive Long id) {

        return feautureApp.deleteApp(id);
    }

    @PatchMapping
    public ApiResponse<Void> updateStatusApp(@Valid @RequestBody AppRequest.StatusUpdateApp request) {

        return feautureApp.updateStatusApp(request);
    }

}
