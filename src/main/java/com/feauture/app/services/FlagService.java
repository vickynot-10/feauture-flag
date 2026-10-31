package com.feauture.app.services;

import java.time.Instant;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.feauture.app.dto.ApiResponse;
import com.feauture.app.dto.FlagDefinitionRequest;
import com.feauture.app.dto.FlagRequest;
import com.feauture.app.engine.FlagEvaluator;
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

    private final FlagEvaluator flagEvaluator;

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
    public ApiResponse<String> getFlagDefintion(FlagRequest.GetDefinition request) {

        String conditions = appFlagVersionRepository
                .findConditionsByFlagIdAndVersion(request.id(), request.version());

        return ApiResponse.success("Fetched Successfully !", conditions);
    }

    @Transactional(readOnly = true)
    public ApiResponse<AppFlagRepository.FlagDetails> getFlagByID(
            FlagRequest.getFlagByID query) {

        var res = appFlagRepository.findFlagById(query.id())
                .orElseThrow(() -> ApiResponse.error(
                        HttpStatus.NOT_FOUND, "Flag not found"));

        return ApiResponse.success("Fetched Successfully!", res);
    }

    @Transactional(readOnly = true)
    public List<AppFlagRepository.ListFlagDetails> getFlagLists() {

        return appFlagRepository.findAllFlagVersions();
    }

    @Transactional
    public ApiResponse<Boolean> playgroundEditor(FlagRequest.playgroundBody request) {
        AppFlagVersions flag = appFlagVersionRepository.findByFlag_IdAndVersion(request.flagId(), request.version())
                .orElseThrow(() -> new RuntimeException("Flag version not found"));

        var definition = flag.getDefinition();

        boolean response = flagEvaluator.Evaluate(flag.getFlag().getFlagKey(), definition.salt(), request.bucketValue(),
                definition.defaultOn(), definition.rules(), request.attributes());

        return ApiResponse.success("Fetcehd Successfully !", response);
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

        AppFlags flag = new AppFlags();
        flag.setEnabled(true);
        flag.setName(request.name());
        flag.setFlagKey(flagKey);
        flag.setCurrentVersion(1);
        flag.setApp(applicationRepository.getReferenceById(request.app()));
        flag.setCreatedBy(userRepository.getReferenceById(userId));
        flag.setUpdatedBy(userRepository.getReferenceById(userId));
        appFlagRepository.save(flag);

        insertVersion(flag, 1, defaultDefinition(), userId);
        return ApiResponse.success("Flag Created Successfully !", null);
    }

    private FlagDefinitionRequest.Definition defaultDefinition() {
        return new FlagDefinitionRequest.Definition(
                UUID.randomUUID().toString().substring(0, 8), "user_id", false, List.of());
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

        insertVersion(appFlagRepository.getReferenceById(request.id()), nextversion, request.definition(), userId);

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

    private void insertVersion(AppFlags flag, int version, FlagDefinitionRequest.Definition definition, Long userId) {

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
