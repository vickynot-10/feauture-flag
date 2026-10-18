package com.feauture.app.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.feauture.app.dto.ApiRequest;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/api-apps")
public class ApiController {

    @PostMapping
    public String saveApi(@RequestBody @Valid ApiRequest.SaveApi request) {
        return "ok";
    }

    @PutMapping
    public String editApi(@RequestBody @Valid ApiRequest.EditApi request) {
        return "ok";
    }

    @PatchMapping
    public String statusUpdateApi(@RequestBody @Valid ApiRequest.StatusUpdate request) {
        return "ok";
    }

    @DeleteMapping("/{id}")
    public String deleteApi(@PathVariable @Positive Long id) {
        return "ok";
    }

}
