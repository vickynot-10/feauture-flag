package com.feauture.app.controllers;

import java.util.Map;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.feauture.app.dto.ApiResponse;
import com.feauture.app.dto.FlagRequest;
import com.feauture.app.services.FlagService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestParam;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/flags")

public class FlagController {

    private final FlagService flagService;

    @GetMapping
    public ApiResponse<Map<String, Object>> getFlags(@Valid FlagRequest.PageQuery request) {
        return flagService.getFlags(request);
    }

    @PostMapping
    public ApiResponse<Void> saveFlag(@Valid @RequestBody FlagRequest.saveFlag request,
            @RequestAttribute("userId") Long userId) {
        return flagService.saveFlag(request, userId);
    }

    @PutMapping
    public ApiResponse<Void> updateFlags(@Valid @RequestBody FlagRequest.editFlag request,
            @RequestAttribute("userId") Long userId) {
        return flagService.editFlag(request, userId);
    }

    @PatchMapping
    public ApiResponse<Void> updateStatusFlag(@Valid @RequestBody FlagRequest.idFlagRequest request) {
        return flagService.statusToggleFlag(request.id());
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteFlag(@PathVariable @Positive Long id) {
        return flagService.deleteFlag(id);
    }
}