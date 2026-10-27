package com.feauture.app.services;

import java.time.Instant;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.feauture.app.dto.ApiResponse;
import com.feauture.app.dto.FlagRequest;
import com.feauture.app.entity.AppFlagVersions;
import com.feauture.app.entity.AppFlags;
import com.feauture.app.repository.AppFlagRepository;
import com.feauture.app.repository.AppFlagRepository.FlagItem;
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

    @Transactional(readOnly = true)
    public ApiResponse<Map<String, Object>> getFlags(FlagRequest.PageQuery query) {
        Pageable page = PageRequest.of(query.page() - 1, query.limit(), Sort.by("updatedAt").descending());

        String search = query.search() == null ? "" : query.search();
        Page<FlagItem> result = appFlagRepository.findByNameContainingIgnoreCase(search, page);

        Map<String, Object> res = Map.of(
                "total", result.getTotalElements(),
                "list", result.getContent());

        return ApiResponse.success("Fetched Successfully !", res);

    }

    @Transactional(readOnly = true)
    public ApiResponse<AppFlags> getFlagByID(
            FlagRequest.getFlagByID query) {

        AppFlags res = appFlagRepository.findById(query.id())
                .orElseThrow(() -> ApiResponse.error(
                        HttpStatus.NOT_FOUND, "Flag not found"));

        return ApiResponse.success("Fetched Successfully!", res);
    }

    @Transactional
    public ApiResponse<Void> saveFlag(FlagRequest.saveFlag request, Long userId) {

        String flagKey = toFlagKey(request.flagKey());
        if (flagKey.isEmpty()) {
            throw ApiResponse.error(HttpStatus.BAD_REQUEST, "Invalid flag key");
        }
        if (appFlagRepository.existsByApp_IdAndFlagKey(request.app(), flagKey)) {
            throw ApiResponse.error(HttpStatus.CONFLICT, "A Flag with similar Key and App Already Exists !");
        }

        AppFlags app = new AppFlags();
        app.setEnabled(true);
        app.setName(request.name());
        app.setFlagKey(flagKey);
        app.setApp(applicationRepository.getReferenceById(request.app()));

        app.setCreatedBy(userRepository.getReferenceById(userId));
        app.setUpdatedBy(userRepository.getReferenceById(userId));

        appFlagRepository.save(app);

        Map<String, Object> definition = Map.of(
                "test", true,
                "message", "Hello World",
                "limit", 100,
                "enabled", true);

        insertVersion(app, 1, definition, userId);
        return ApiResponse.success("Flag Created Successfully !", null);
    }

    @Transactional
    public ApiResponse<Void> editFlag(FlagRequest.saveflagEditor request, Long userId) {

        int nextversion = request.expectedVersion() + 1;
        int updated = appFlagRepository.insertVersion(request.id(), request.expectedVersion(), nextversion,
                Instant.now(), userRepository.getReferenceById(userId));

        if (updated == 0) {
            if (!appFlagRepository.existsById(request.id())) {
                throw ApiResponse.error(HttpStatus.NOT_FOUND, "Flag is Not Found !");
            }
            throw ApiResponse.error(HttpStatus.CONFLICT, "Flag was changed by someone else, reload and retry !");
        }

        insertVersion(appFlagRepository.getReferenceById(request.id()), nextversion, request.defintion(), userId);

        return ApiResponse.success("Flag updated Successfully !", null);
    }

    @Transactional
    public ApiResponse<Void> statusToggleFlag(Long id, Long userId) {
        final int response = appFlagRepository.statusToggle(id, userRepository.getReferenceById(userId));
        if (response == 0) {
            throw ApiResponse.error(HttpStatus.CONFLICT, "Flag failed to update or May not found !");
        }

        return ApiResponse.success("Flag Status updated Successfully !", null);

    }

    @Transactional
    public ApiResponse<Void> deleteFlag(Long id) {
        if (appFlagRepository.deleteFlag(id) == 0) {
            throw ApiResponse.error(HttpStatus.NOT_FOUND, "Flag is Not Found !");
        }
        return ApiResponse.success("Flag Deleted Successfully !", null);
    }

    private void insertVersion(AppFlags flag, int version, Map<String, Object> definition, Long userId) {

        AppFlagVersions appVersion = new AppFlagVersions();
        appVersion.setVersion(version);
        appVersion.setFlag(flag);
        appVersion.setDefinition(definition);

        appVersion.setCreatedBy(userRepository.getReferenceById(userId));
        appFlagVersionRepository.save(appVersion);

    }

    public static String toFlagKey(String value) {
        final Pattern VALID = Pattern.compile("^[a-z0-9_-]+$");
        final Pattern INVALID_RUN = Pattern.compile("[^a-z0-9]+");
        final Pattern EDGE_UNDERSCORES = Pattern.compile("^_+|_+$");

        if (value == null) {
            return "";
        }
        String lowered = value.trim().toLowerCase(Locale.ROOT);
        if (VALID.matcher(lowered).matches()) {
            return cut(lowered);
        }
        String cleaned = INVALID_RUN.matcher(lowered).replaceAll("_");
        cleaned = EDGE_UNDERSCORES.matcher(cleaned).replaceAll("");
        return cut(cleaned);
    }

    private static String cut(String value) {
        final int MAX_LENGTH = 50;
        return value.length() > MAX_LENGTH ? value.substring(0, MAX_LENGTH) : value;
    }

}
