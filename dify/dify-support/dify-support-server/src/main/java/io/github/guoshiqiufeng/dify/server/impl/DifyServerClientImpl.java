/*
 * Copyright (c) 2025-2026, fubluesky (fubluesky@foxmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.guoshiqiufeng.dify.server.impl;

import io.github.guoshiqiufeng.dify.core.pojo.DifyPageResult;
import io.github.guoshiqiufeng.dify.core.utils.CollUtil;
import io.github.guoshiqiufeng.dify.core.utils.MapUtil;
import io.github.guoshiqiufeng.dify.dataset.client.DifyDatasetClient;
import io.github.guoshiqiufeng.dify.dataset.dto.response.DocumentIndexingStatusResponse;
import io.github.guoshiqiufeng.dify.dataset.dto.response.DocumentInfo;
import io.github.guoshiqiufeng.dify.dataset.dto.response.UploadFileInfoResponse;
import io.github.guoshiqiufeng.dify.server.utils.FilePreviewSigner;
import io.github.guoshiqiufeng.dify.server.DifyServer;
import io.github.guoshiqiufeng.dify.server.client.DifyServerClient;
import io.github.guoshiqiufeng.dify.server.dto.request.AppsRequest;
import io.github.guoshiqiufeng.dify.server.dto.request.ChatConversationsRequest;
import io.github.guoshiqiufeng.dify.server.dto.request.DocumentRetryRequest;
import io.github.guoshiqiufeng.dify.server.dto.response.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * @author yanghq
 * @version 0.8.0
 * @since 2025/4/9 10:42
 */
@Slf4j
public class DifyServerClientImpl implements DifyServer {

    private final DifyServerClient difyServerClient;
    private final DifyDatasetClient difyDatasetClient;
    private final FilePreviewSigner filePreviewSigner;

    public DifyServerClientImpl(DifyServerClient difyServerClient) {
        this(difyServerClient, null, null);
    }

    public DifyServerClientImpl(DifyServerClient difyServerClient,
                                DifyDatasetClient difyDatasetClient,
                                FilePreviewSigner filePreviewSigner) {
        this.difyServerClient = difyServerClient;
        this.difyDatasetClient = difyDatasetClient;
        this.filePreviewSigner = filePreviewSigner;
    }

    @Override
    public List<AppsResponse> apps(String mode, String name) {
        return difyServerClient.apps(mode, name);
    }

    @Override
    public AppsResponseResult apps(AppsRequest appsRequest) {
        return difyServerClient.apps(appsRequest);
    }

    @Override
    public AppsResponse app(String appId) {
        return difyServerClient.app(appId);
    }

    @Override
    public List<ApiKeyResponse> getAppApiKey(String appId) {
        return difyServerClient.getAppApiKey(appId);
    }

    @Override
    public List<ApiKeyResponse> initAppApiKey(String appId) {
        return difyServerClient.initAppApiKey(appId);
    }

    @Override
    public void deleteAppApiKey(String appId, String apiKeyId) {
        difyServerClient.deleteAppApiKey(appId, apiKeyId);
    }

    @Override
    public List<DatasetApiKeyResponse> getDatasetApiKey() {
        return difyServerClient.getDatasetApiKey();
    }

    @Override
    public List<DatasetApiKeyResponse> initDatasetApiKey() {
        return difyServerClient.initDatasetApiKey();
    }

    @Override
    public void deleteDatasetApiKey(String apiKeyId) {
        difyServerClient.deleteDatasetApiKey(apiKeyId);
    }

    @Override
    public UploadFileInfoResponse getUploadFileInfoByDocument(String datasetId, String documentId, String apiKey) {
        Assert.notNull(difyDatasetClient, "DifyDatasetClient is not configured");
        DocumentInfo documentInfo = difyDatasetClient.getDocument(datasetId, documentId, apiKey);
        if (documentInfo == null) {
            return null;
        }

        Map<String, Object> dataSourceInfo = documentInfo.getDataSourceInfo();
        if (CollUtil.isEmpty(dataSourceInfo)) {
            return null;
        }

        String dataSourceType = documentInfo.getDataSourceType();
        if (dataSourceType == null || dataSourceType.trim().isEmpty()) {
            return null;
        }

        UploadFileInfoResponse response = new UploadFileInfoResponse();

        Object detailObj = dataSourceInfo.get(dataSourceType);
        if (!(detailObj instanceof Map)) {
            return null;
        }
        Map<?, ?> detailMap = (Map<?, ?>) detailObj;

        response.setId(MapUtil.getStr(detailMap, "id"));
        response.setName(MapUtil.getStr(detailMap, "name"));
        response.setSize(MapUtil.getInt(detailMap, "size"));
        response.setExtension(MapUtil.getStr(detailMap, "extension"));
        response.setMimeType(MapUtil.getStr(detailMap, "mime_type"));
        response.setCreatedBy(MapUtil.getStr(detailMap, "created_by"));
        BigDecimal createdAt = MapUtil.getBigDecimal(detailMap, "created_at");
        response.setCreatedAt(createdAt == null ? null : createdAt.longValue());

        if (response.getId() == null) {
            return null;
        }

        Assert.notNull(filePreviewSigner, "FilePreviewSigner is not configured");
        response.setUrl(filePreviewSigner.buildSignedFileUrl(response.getId(), false));
        response.setDownloadUrl(filePreviewSigner.buildSignedFileUrl(response.getId(), true));
        return response;
    }

    @Override
    public DifyPageResult<ChatConversationResponse> chatConversations(ChatConversationsRequest request) {
        return difyServerClient.chatConversations(request);
    }

    @Override
    public List<DailyConversationsResponse> dailyConversations(String appId, LocalDateTime start, LocalDateTime end) {
        return difyServerClient.dailyConversations(appId, start, end);
    }

    @Override
    public List<DailyWorkflowConversationsResponse> dailyWorkflowConversations(String appId, LocalDateTime start, LocalDateTime end) {
        return difyServerClient.dailyWorkflowConversations(appId, start, end);
    }

    @Override
    public List<DailyEndUsersResponse> dailyEndUsers(String appId, LocalDateTime start, LocalDateTime end) {
        return difyServerClient.dailyEndUsers(appId, start, end);
    }

    @Override
    public List<AverageSessionInteractionsResponse> averageSessionInteractions(String appId, LocalDateTime start, LocalDateTime end) {
        return difyServerClient.averageSessionInteractions(appId, start, end);
    }

    @Override
    public List<TokensPerSecondResponse> tokensPerSecond(String appId, LocalDateTime start, LocalDateTime end) {
        return difyServerClient.tokensPerSecond(appId, start, end);
    }

    @Override
    public List<UserSatisfactionRateResponse> userSatisfactionRate(String appId, LocalDateTime start, LocalDateTime end) {
        return difyServerClient.userSatisfactionRate(appId, start, end);
    }

    @Override
    public List<TokenCostsResponse> tokenCosts(String appId, LocalDateTime start, LocalDateTime end) {
        return difyServerClient.tokenCosts(appId, start, end);
    }

    @Override
    public List<DailyMessagesResponse> dailyMessages(String appId, LocalDateTime start, LocalDateTime end) {
        return difyServerClient.dailyMessages(appId, start, end);
    }

    @Override
    public DocumentIndexingStatusResponse getDatasetIndexingStatus(String datasetId) {
        return difyServerClient.getDatasetIndexingStatus(datasetId);
    }

    @Override
    public DocumentIndexingStatusResponse.ProcessingStatus getDocumentIndexingStatus(String datasetId, String documentId) {
        return difyServerClient.getDocumentIndexingStatus(datasetId, documentId);
    }

    @Override
    public DatasetErrorDocumentsResponse getDatasetErrorDocuments(String datasetId) {
        return difyServerClient.getDatasetErrorDocuments(datasetId);
    }

    @Override
    public void retryDocumentIndexing(DocumentRetryRequest request) {
        difyServerClient.retryDocumentIndexing(request);

    }
}
