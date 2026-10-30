package com.feauture.app.controllers;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.feauture.app.dto.ApiResponse;
import com.feauture.app.dto.FlagDefinitionRequest;
import com.feauture.app.dto.FlagRequest;
import com.feauture.app.entity.AppFlagVersions;
import com.feauture.app.repository.AppFlagRepository;
import com.feauture.app.services.FlagService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;

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

    @PostMapping("/editor")
    public ApiResponse<Void> saveflagEditor(@Valid @RequestBody FlagRequest.saveflagEditor request,
            @RequestAttribute("userId") Long userId) {

        return flagService.editFlag(request, userId);
    }

    @GetMapping("/get-flag-list")
    public List<AppFlagRepository.ListFlagDetails> getFlags() {
        return flagService.getFlagLists();
    }

    @GetMapping("/get-definition")
    public ApiResponse<FlagDefinitionRequest.Definition> getMethodName(@Valid FlagRequest.GetDefinition request) {
        return flagService.getFlagDefintion(request);
    }

    @PostMapping("/playground")
    public ApiResponse<AppFlagVersions> editorPlayGround(@Valid @RequestBody FlagRequest.playgroundBody request) {
        return flagService.playgroundEditor(request);
    }

    @GetMapping("/get-flag")
    public ApiResponse<AppFlagRepository.FlagDetails> getFlagValue(@Valid FlagRequest.getFlagByID request) {
        return flagService.getFlagByID(request);
    }

    @PatchMapping
    public ApiResponse<Void> updateStatusFlag(@Valid @RequestBody FlagRequest.idFlagRequest request,
            @RequestAttribute("userId") Long userId) {
        return flagService.statusToggleFlag(request.id(), userId);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteFlag(@PathVariable @Positive Long id) {

        return flagService.deleteFlag(id);
    }
}