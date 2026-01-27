---
lang: zh-cn
title: Server API
description: 
---

# Server API

## 接口概述

服务器 API 提供了全面的功能，用于与 Dify 平台交互，包括管理应用程序、检索和初始化应用程序及数据集的 API 密钥。所有接口都需要有效的
API 密钥进行身份验证。
使用 `DifyServer` 接口实例。
> 默认检测当前环境包含 redis 则使用 redis持久化 token，若不包含 redis 则使用默认实现保存 token(重启服务会丢失)

## 1. 应用管理

### 1.1 获取所有应用 (非分页)

#### 方法

```java
List<AppsResponseVO> apps(String mode, String name);
```

#### 请求参数

| 参数名  | 类型     | 是否必须 | 描述                                                   |
|------|--------|------|------------------------------------------------------|
| mode | String | 否    | 模式 chat\agent-chat\completion\advanced-chat\workflow |
| name | String | 否    | 应用名称，用于过滤应用列表（可选，传入空字符串时表示不过滤）                       |

#### 响应参数

AppsResponseVO

| 参数名                 | 类型             | 描述           |
|---------------------|----------------|--------------|
| id                  | String         | 应用ID         |
| name                | String         | 应用名称         |
| maxActiveRequests   | Integer        | 最大活跃请求数      |
| description         | String         | 应用描述         |
| mode                | String         | 应用模式         |
| iconType            | String         | 图标类型         |
| icon                | String         | 图标           |
| iconBackground      | String         | 图标背景         |
| iconUrl             | String         | 图标URL        |
| modelConfig         | ModelConfig    | 模型配置         |
| workflow            | Object         | 工作流信息        |
| useIconAsAnswerIcon | Boolean        | 是否使用图标作为答案图标 |
| createdBy           | String         | 创建者ID        |
| createdAt           | Long           | 创建时间（时间戳）    |
| updatedBy           | String         | 更新者ID        |
| updatedAt           | Long           | 更新时间（时间戳）    |
| tags                | `List<String>` | 应用标签         |

ModelConfig

| 参数名       | 类型     | 描述        |
|-----------|--------|-----------|
| model     | Model  | 模型信息      |
| prePrompt | String | 预提示文本     |
| createdBy | String | 创建者ID     |
| createdAt | Long   | 创建时间（时间戳） |
| updatedBy | String | 更新者ID     |
| updatedAt | Long   | 更新时间（时间戳） |

Model

| 参数名              | 类型               | 描述    |
|------------------|------------------|-------|
| provider         | String           | 模型提供商 |
| name             | String           | 模型名称  |
| mode             | String           | 模型模式  |
| completionParams | CompletionParams | 完成参数  |

CompletionParams

| 参数名  | 类型             | 描述   |
|------|----------------|------|
| stop | `List<String>` | 停止序列 |

#### 请求示例

```java

@Resource
private DifyServer difyServer;

@Test
public void testGetApps() {
    // 获取所有应用
    List<AppsResponseVO> apps = difyServer.apps("");

    // 获取带名称过滤的应用
    List<AppsResponseVO> filteredApps = difyServer.apps("myApp");
}
```

### 1.2 分页获取应用

#### 方法

```java
AppsResponseResult apps(AppsRequest appsRequest);
```

#### 请求参数

AppsRequest

| 参数名           | 类型      | 是否必须 | 描述                                                       |
|---------------|---------|------|----------------------------------------------------------|
| page          | Integer | 否    | 页码（默认：1）                                                 |
| limit         | Integer | 否    | 每页数量（默认：20，范围：1-100）                                     |
| mode          | String  | 否    | 应用模式过滤：chat\agent-chat\completion\advanced-chat\workflow |
| name          | String  | 否    | 应用名称过滤                                                   |
| isCreatedByMe | Boolean | 否    | 是否为当前用户创建的应用                                             |

#### 响应参数

AppsResponseResult

| 参数名     | 类型                   | 描述      |
|---------|----------------------|---------|
| data    | `List<AppsResponse>` | 当前页数据列表 |
| hasMore | Boolean              | 是否有更多页  |
| limit   | Integer              | 每页数量    |
| page    | Integer              | 当前页码    |
| total   | Integer              | 总数据数量   |

AppsResponse 的结构与 1.1 节中定义的相同。

#### 请求示例

```java

@Resource
private DifyServer difyServer;

@Test
public void testGetAppsPaginated() {
    // 创建分页请求
    AppsRequest request = new AppsRequest();
    request.setPage(1);
    request.setLimit(10);
    request.setMode("chat");
    request.setName("myApp");
    request.setIsCreatedByMe(true);

    // 获取分页应用列表
    AppsResponseResult result = difyServer.apps(request);

    System.out.println("当前页: " + result.getPage());
    System.out.println("每页数量: " + result.getLimit());
    System.out.println("总数: " + result.getTotal());
    System.out.println("是否有更多: " + result.getHasMore());
    System.out.println("数据大小: " + result.getData().size());
}
```

### 1.3 根据ID获取应用

#### 方法

```java
AppsResponseVO app(String appId);
```

#### 请求参数

| 参数名   | 类型     | 是否必须 | 描述    |
|-------|--------|------|-------|
| appId | String | 是    | 应用 ID |

#### 响应参数

与上面定义的 AppsResponseVO 结构相同。

#### 请求示例

```java

@Resource
private DifyServer difyServer;

@Test
public void testGetApp() {
    AppsResponseVO app = difyServer.app("app-123456789");
}
```

### 1.4 获取应用API密钥

#### 方法

```java
List<ApiKeyResponseVO> getAppApiKey(String id);
```

#### 请求参数

| 参数名 | 类型     | 是否必须 | 描述    |
|-----|--------|------|-------|
| id  | String | 是    | 应用 ID |

#### 响应参数

ApiKeyResponseVO

| 参数名   | 类型     | 描述        |
|-------|--------|-----------|
| id    | String | API 密钥 ID |
| token | String | API 密钥令牌值 |

#### 请求示例

```java

@Resource
private DifyServer difyServer;

@Test
public void testGetAppApiKeys() {
    List<ApiKeyResponseVO> apiKeys = difyServer.getAppApiKey("app-123456789");
}
```

### 1.5 初始化应用API密钥

#### 方法

```java
List<ApiKeyResponseVO> initAppApiKey(String id);
```

#### 请求参数

| 参数名 | 类型     | 是否必须 | 描述    |
|-----|--------|------|-------|
| id  | String | 是    | 应用 ID |

#### 响应参数

与上面定义的 ApiKeyResponseVO 结构相同。

#### 请求示例

```java

@Resource
private DifyServer difyServer;

@Test
public void testInitAppApiKey() {
    List<ApiKeyResponseVO> apiKeys = difyServer.initAppApiKey("app-123456789");
}
```

### 1.6 删除应用API密钥

#### 方法

```java
void deleteAppApiKey(String appId, String apiKeyId);
```

#### 请求参数

| 参数名     | 类型     | 是否必须 | 描述      |
|---------|--------|------|---------|
| appId   | String | 是    | 应用 ID   |
| apiKeyId | String | 是    | API密钥ID |

#### 响应参数

该方法不返回值，成功时返回204 No Content。

#### 请求示例

```java

@Resource
private DifyServer difyServer;

@Test
public void testDeleteAppApiKey() {
    // 删除指定应用的API密钥
    difyServer.deleteAppApiKey("app-123456789", "key-789012345");
}
```

## 2. 知识库管理

### 2.1 获取知识库API密钥

#### 方法

```java
List<DatasetApiKeyResponseVO> getDatasetApiKey();
```

#### 响应参数

DatasetApiKeyResponseVO

| 参数名        | 类型     | 描述          |
|------------|--------|-------------|
| id         | String | API 密钥 ID   |
| type       | String | API 密钥类型    |
| token      | String | API 密钥令牌值   |
| lastUsedAt | Long   | 上次使用时间（时间戳） |
| createdAt  | Long   | 创建时间（时间戳）   |

#### 请求示例

```java

@Resource
private DifyServer difyServer;

@Test
public void testGetDatasetApiKeys() {
    List<DatasetApiKeyResponseVO> datasetApiKeys = difyServer.getDatasetApiKey();
}
```

### 2.2 初始化知识库API密钥

#### 方法

```java
List<DatasetApiKeyResponseVO> initDatasetApiKey();
```

#### 响应参数

与上面定义的 DatasetApiKeyResponseVO 结构相同。

#### 请求示例

```java

@Resource
private DifyServer difyServer;

@Test
public void testInitDatasetApiKey() {
    List<DatasetApiKeyResponseVO> datasetApiKeys = difyServer.initDatasetApiKey();
}
```

### 2.3 删除知识库API密钥

#### 方法

```java
void deleteDatasetApiKey(String apiKeyId);
```

#### 请求参数

| 参数名     | 类型     | 是否必须 | 描述      |
|---------|--------|------|---------|
| apiKeyId | String | 是    | API密钥ID |

#### 响应参数

该方法不返回值，成功时返回204 No Content。

#### 请求示例

```java

@Resource
private DifyServer difyServer;

@Test
public void testDeleteDatasetApiKey() {
    // 删除指定的知识库API密钥
    difyServer.deleteDatasetApiKey("89f04b59-6906-4d32-a630-d2911d3b5fd8");
}
```

### 2.4 获取知识库索引状态

#### 方法

```java
DocumentIndexingStatusResponse getDatasetIndexingStatus(String datasetId);
```

#### 请求参数

| 参数名       | 类型     | 是否必须 | 描述     |
|-----------|--------|------|--------|
| datasetId | String | 是    | 知识库ID |

#### 响应参数

DocumentIndexingStatusResponse

| 参数名  | 类型                          | 描述         |
|------|-----------------------------|-----------|
| data | `List<ProcessingStatus>`    | 文档索引状态列表  |

ProcessingStatus

| 参数名             | 类型      | 描述                                                    |
|-----------------|---------|-------------------------------------------------------|
| id              | String  | 文档ID                                                  |
| indexingStatus  | String  | 索引状态：waiting、parsing、cleaning、splitting、indexing、completed、error、paused |
| processingStartedAt | Long | 处理开始时间（时间戳）                                           |
| parsingCompletedAt | Long | 解析完成时间（时间戳）                                           |
| cleaningCompletedAt | Long | 清理完成时间（时间戳）                                           |
| splittingCompletedAt | Long | 分割完成时间（时间戳）                                           |
| completedAt     | Long    | 完成时间（时间戳）                                             |
| pausedAt        | Long    | 暂停时间（时间戳）                                             |
| error           | String  | 错误信息                                                  |
| stoppedAt       | Long    | 停止时间（时间戳）                                             |
| completedSegments | Integer | 已完成的分段数                                               |
| totalSegments   | Integer | 总分段数                                                  |

#### 请求示例

```java

@Resource
private DifyServer difyServer;

@Test
public void testGetDatasetIndexingStatus() {
    String datasetId = "dataset-123456789";

    // 获取知识库索引状态
    DocumentIndexingStatusResponse indexingStatus = difyServer.getDatasetIndexingStatus(datasetId);

    if (indexingStatus.getData() != null && !indexingStatus.getData().isEmpty()) {
        for (DocumentIndexingStatusResponse.ProcessingStatus doc : indexingStatus.getData()) {
            System.out.println("文档ID: " + doc.getId());
            System.out.println("索引状态: " + doc.getIndexingStatus());
            System.out.println("已完成分段: " + doc.getCompletedSegments() + "/" + doc.getTotalSegments());
        }
    }
}
```

### 2.5 获取文档索引状态

#### 方法

```java
DocumentIndexingStatusResponse.ProcessingStatus getDocumentIndexingStatus(String datasetId, String documentId);
```

#### 请求参数

| 参数名        | 类型     | 是否必须 | 描述     |
|------------|--------|------|--------|
| datasetId  | String | 是    | 知识库ID |
| documentId | String | 是    | 文档ID  |

#### 响应参数

ProcessingStatus（结构与 2.4 节中定义的相同）

#### 请求示例

```java

@Resource
private DifyServer difyServer;

@Test
public void testGetDocumentIndexingStatus() {
    String datasetId = "dataset-123456789";
    String documentId = "doc-987654321";

    // 获取文档索引状态
    DocumentIndexingStatusResponse.ProcessingStatus documentStatus =
        difyServer.getDocumentIndexingStatus(datasetId, documentId);

    System.out.println("文档ID: " + documentStatus.getId());
    System.out.println("索引状态: " + documentStatus.getIndexingStatus());
    System.out.println("已完成分段: " + documentStatus.getCompletedSegments() + "/" + documentStatus.getTotalSegments());

    if (documentStatus.getError() != null) {
        System.out.println("错误信息: " + documentStatus.getError());
    }
}
```

### 2.6 获取知识库错误文档

#### 方法

```java
DatasetErrorDocumentsResponse getDatasetErrorDocuments(String datasetId);
```

#### 请求参数

| 参数名       | 类型     | 是否必须 | 描述     |
|-----------|--------|------|--------|
| datasetId | String | 是    | 知识库ID |

#### 响应参数

DatasetErrorDocumentsResponse

| 参数名   | 类型                      | 描述       |
|-------|-------------------------|----------|
| data  | `List<ErrorDocument>`   | 错误文档列表   |
| total | Integer                 | 错误文档总数   |

ErrorDocument

| 参数名             | 类型     | 描述                |
|-----------------|--------|-------------------|
| id              | String | 文档ID              |
| name            | String | 文档名称              |
| error           | String | 错误信息              |
| indexingStatus  | String | 索引状态（通常为 error）  |
| createdAt       | Long   | 创建时间（时间戳）         |
| updatedAt       | Long   | 更新时间（时间戳）         |

#### 请求示例

```java

@Resource
private DifyServer difyServer;

@Test
public void testGetDatasetErrorDocuments() {
    String datasetId = "dataset-123456789";

    // 获取知识库错误文档
    DatasetErrorDocumentsResponse errorDocuments = difyServer.getDatasetErrorDocuments(datasetId);

    System.out.println("错误文档总数: " + errorDocuments.getTotal());

    if (errorDocuments.getData() != null && !errorDocuments.getData().isEmpty()) {
        for (DatasetErrorDocumentsResponse.ErrorDocument doc : errorDocuments.getData()) {
            System.out.println("文档ID: " + doc.getId());
            System.out.println("文档名称: " + doc.getName());
            System.out.println("错误信息: " + doc.getError());
        }
    } else {
        System.out.println("没有错误文档");
    }
}
```

### 2.7 重试文档索引

#### 方法

```java
void retryDocumentIndexing(DocumentRetryRequest request);
```

#### 请求参数

DocumentRetryRequest

| 参数名         | 类型             | 是否必须 | 描述           |
|-------------|----------------|------|--------------|
| datasetId   | String         | 是    | 知识库ID       |
| documentIds | `List<String>` | 是    | 需要重试的文档ID列表 |

#### 响应参数

该方法不返回值，成功时返回204 No Content。

#### 请求示例

```java

@Resource
private DifyServer difyServer;

@Test
public void testRetryDocumentIndexing() {
    String datasetId = "dataset-123456789";

    // 先获取错误文档
    DatasetErrorDocumentsResponse errorDocuments = difyServer.getDatasetErrorDocuments(datasetId);

    if (errorDocuments.getData() != null && !errorDocuments.getData().isEmpty()) {
        // 提取错误文档ID
        List<String> errorDocIds = errorDocuments.getData().stream()
            .map(DatasetErrorDocumentsResponse.ErrorDocument::getId)
            .collect(Collectors.toList());

        // 创建重试请求
        DocumentRetryRequest request = new DocumentRetryRequest();
        request.setDatasetId(datasetId);
        request.setDocumentIds(errorDocIds);

        // 重试文档索引
        difyServer.retryDocumentIndexing(request);
        System.out.println("已触发 " + errorDocIds.size() + " 个文档的索引重试");
    } else {
        System.out.println("没有需要重试的错误文档");
    }
}
```

### 2.8 获取文档上传文件信息

#### 方法

```java
UploadFileInfoResponse getUploadFileInfoByDocument(String datasetId, String documentId);
UploadFileInfoResponse getUploadFileInfoByDocument(String datasetId, String documentId, String apiKey);
```

#### 请求参数

| 参数名        | 类型     | 是否必须 | 描述                                                                 |
|------------|--------|------|---------------------------------------------------------------------|
| datasetId  | String | 是    | 知识库ID                                                              |
| documentId | String | 是    | 文档ID                                                               |
| apiKey     | String | 否    | API密钥（可选）。如果不传或传 null，则使用配置文件中的 `dify.dataset.api-key`；如果传入具体值，则使用传入的 API Key 覆盖默认配置 |

#### 响应参数

UploadFileInfoResponse

| 参数名         | 类型      | 描述                      |
|-------------|---------|-------------------------|
| id          | String  | 文件ID                    |
| name        | String  | 文件名称                    |
| size        | Integer | 文件大小（字节）                |
| extension   | String  | 文件扩展名                   |
| url         | String  | 文件预览URL（带签名，有效期5分钟）    |
| downloadUrl | String  | 文件下载URL（带签名，有效期5分钟）    |
| mimeType    | String  | 文件MIME类型                |
| createdBy   | String  | 创建者ID                   |
| createdAt   | Long    | 创建时间（时间戳）               |

#### 功能说明

此方法用于获取知识库中已上传文档的文件信息，并自动生成带签名的预览和下载链接。主要特点：

1. **自动签名**：返回的 `url` 和 `downloadUrl` 已经包含了安全签名，可以直接使用
2. **有效期限制**：生成的 URL 有效期为 5 分钟（由服务端 `FILES_ACCESS_TIMEOUT` 配置决定）
3. **支持预览和下载**：提供两种 URL，分别用于在线预览和文件下载

#### 请求示例

```java

@Resource
private DifyServer difyServer;

@Test
public void testGetUploadFileInfo() {
    String datasetId = "dataset-123456789";
    String documentId = "doc-987654321";

    // 方法1：使用默认API密钥
    UploadFileInfoResponse fileInfo = difyServer.getUploadFileInfoByDocument(datasetId, documentId);

    if (fileInfo != null) {
        System.out.println("文件ID: " + fileInfo.getId());
        System.out.println("文件名称: " + fileInfo.getName());
        System.out.println("文件大小: " + fileInfo.getSize() + " 字节");
        System.out.println("文件扩展名: " + fileInfo.getExtension());
        System.out.println("MIME类型: " + fileInfo.getMimeType());
        System.out.println("创建者: " + fileInfo.getCreatedBy());
        System.out.println("创建时间: " + fileInfo.getCreatedAt());

        // 使用预览URL（在浏览器中打开可直接预览）
        System.out.println("预览URL: " + fileInfo.getUrl());

        // 使用下载URL（在浏览器中打开会触发下载）
        System.out.println("下载URL: " + fileInfo.getDownloadUrl());
    }
}

@Test
public void testGetUploadFileInfoWithApiKey() {
    String datasetId = "dataset-123456789";
    String documentId = "doc-987654321";
    String apiKey = "dataset-xxx";

    // 方法2：指定API密钥
    UploadFileInfoResponse fileInfo =
        difyServer.getUploadFileInfoByDocument(datasetId, documentId, apiKey);

    if (fileInfo != null) {
        System.out.println("文件信息: " + fileInfo);
    }
}
```

#### 使用场景

1. **文件预览**：在应用中展示知识库文档的预览链接
2. **文件下载**：提供文档下载功能
3. **文件信息展示**：显示文档的详细信息（名称、大小、类型等）
4. **临时访问**：生成临时的安全访问链接，避免直接暴露文件存储路径

#### 注意事项

1. **URL 有效期**：返回的 URL 有效期为 5 分钟，超时后需要重新调用此方法获取新的 URL
2. **文档类型限制**：仅支持通过文件上传方式创建的文档，不支持通过其他方式（如网页抓取）创建的文档
3. **权限验证**：需要确保使用的 API 密钥有权限访问指定的知识库和文档
4. **配置要求**：需要在配置文件中正确配置文件服务地址和签名密钥

#### 配置说明

文件服务地址配置优先级：
1. **优先使用** `dify.file.url` - 专门用于文件服务的地址
2. **兜底使用** `dify.url` - 如果未配置 `dify.file.url`，则使用通用的 Dify 服务地址

签名密钥配置：
- 必须配置 `dify.signature.secret-key`，用于生成文件访问签名

#### 配置示例

**方式1：使用专用文件服务地址（推荐）**

```yaml
dify:
  # Dify 服务地址
  url: http://your-dify-server.com
  # 文件服务地址（如果文件服务与主服务分离）
  file:
    url: http://your-file-server.com
  # 签名密钥
  signature:
    secret-key: your-secret-key
```

**方式2：使用统一服务地址（简化配置）**

```yaml
dify:
  # Dify 服务地址（同时用于文件服务）
  url: http://your-dify-server.com
  # 签名密钥
  signature:
    secret-key: your-secret-key
```

## 3. 聊天会话管理

### 3.1 获取应用的聊天会话列表

#### 方法

```java
DifyPageResult<ChatConversationResponse> chatConversations(ChatConversationsRequest request);
```

#### 请求参数

ChatConversationsRequest

| 参数名              | 类型      | 是否必须 | 描述                               |
|------------------|---------|------|----------------------------------|
| appId            | String  | 是    | 应用ID                             |
| page             | Integer | 否    | 页码（默认：1）                         |
| limit            | Integer | 否    | 每页数量（默认：10，范围：1-100）             |
| start            | String  | 否    | 开始时间，格式：yyyy-MM-dd HH:mm         |
| end              | String  | 否    | 结束时间，格式：yyyy-MM-dd HH:mm         |
| sortBy           | String  | 否    | 排序字段，例如：-created_at（按创建时间倒序）     |
| annotationStatus | String  | 否    | 注释状态：all、not_annotated、annotated |

#### 响应参数

`DifyPageResult<ChatConversationResponse>`

| 参数名     | 类型                               | 描述      |
|---------|----------------------------------|---------|
| data    | `List<ChatConversationResponse>` | 当前页数据列表 |
| hasMore | Boolean                          | 是否有更多页  |
| limit   | Integer                          | 每页数量    |
| page    | Integer                          | 当前页码    |
| total   | Integer                          | 总数据数量   |

ChatConversationResponse

| 参数名                  | 类型                    | 描述       |
|----------------------|-----------------------|----------|
| id                   | String                | 会话ID     |
| status               | String                | 会话状态     |
| fromSource           | String                | 来源       |
| fromEndUserId        | String                | 终端用户ID   |
| fromEndUserSessionId | String                | 终端用户会话ID |
| fromAccountId        | String                | 账户ID     |
| fromAccountName      | String                | 账户名称     |
| name                 | String                | 会话名称     |
| summary              | String                | 会话摘要     |
| readAt               | Long                  | 阅读时间戳    |
| createdAt            | Long                  | 创建时间戳    |
| updatedAt            | Long                  | 更新时间戳    |
| annotated            | Boolean               | 是否已标注    |
| modelConfig          | `Map<String, Object>` | 模型配置     |
| messageCount         | Integer               | 消息数量     |
| userFeedbackStats    | FeedbackStats         | 用户反馈统计   |
| adminFeedbackStats   | FeedbackStats         | 管理员反馈统计  |
| statusCount          | StatusCount           | 状态计数     |

FeedbackStats

| 参数名     | 类型      | 描述  |
|---------|---------|-----|
| like    | Integer | 点赞数 |
| dislike | Integer | 点踩数 |

StatusCount

| 参数名            | 类型      | 描述     |
|----------------|---------|--------|
| success        | Integer | 成功数量   |
| failed         | Integer | 失败数量   |
| partialSuccess | Integer | 部分成功数量 |

#### 请求示例

```java

@Resource
private DifyServer difyServer;

@Test
public void testGetChatConversations() {
    // 创建请求对象
    ChatConversationsRequest request = new ChatConversationsRequest();
    request.setAppId("app-123456789");
    request.setPage(1);
    request.setLimit(10);
    request.setStart("2025-10-23 00:00");
    request.setEnd("2025-10-30 23:59");
    request.setAnnotationStatus("all");
    request.setSortBy("-created_at");

    // 获取聊天会话列表
    DifyPageResult<ChatConversationResponse> result = difyServer.chatConversations(request);

    System.out.println("当前页: " + result.getPage());
    System.out.println("每页数量: " + result.getLimit());
    System.out.println("总数: " + result.getTotal());
    System.out.println("是否有更多: " + result.getHasMore());
    System.out.println("数据大小: " + result.getData().size());

    for (ChatConversationResponse conversation : result.getData()) {
        System.out.println("会话ID: " + conversation.getId());
        System.out.println("会话名称: " + conversation.getName());
        System.out.println("是否标注: " + conversation.isAnnotated());
    }
}
```

## 4. 应用统计

### 4.1 获取应用的每日对话统计(工作流除外)

#### 方法

```java
List<DailyConversationsResponse> dailyConversations(String appId, LocalDateTime start, LocalDateTime end);
```

#### 请求参数

| 参数名   | 类型            | 是否必须 | 描述                       |
|-------|---------------|------|--------------------------|
| appId | String        | 是    | 应用 ID                    |
| start | LocalDateTime | 是    | 开始时间，格式：yyyy-MM-dd HH:mm |
| end   | LocalDateTime | 是    | 结束时间，格式：yyyy-MM-dd HH:mm |

#### 响应参数

DailyConversationsResponse

| 参数名               | 类型      | 描述               |
|-------------------|---------|------------------|
| date              | String  | 日期，格式：yyyy-MM-dd |
| conversationCount | Integer | 当日对话数量           |

#### 请求示例

```java

@Resource
private DifyServer difyServer;

@Test
public void testGetDailyConversations() {
    String appId = "08534c1a-4316-4cd3-806d-bbbca03f58aa";
    LocalDateTime start = LocalDateTime.of(2025, 10, 23, 0, 0);
    LocalDateTime end = LocalDateTime.of(2025, 10, 30, 23, 59);

    // 获取每日对话统计
    List<DailyConversationsResponse> dailyStats = difyServer.dailyConversations(appId, start, end);

    if (dailyStats != null) {
        for (DailyConversationsResponse dailyStat : dailyStats) {
            System.out.println("日期: " + dailyStat.getDate());
            System.out.println("对话数量: " + dailyStat.getConversationCount());
        }
    }
}
```

### 4.2 获取应用的每日工作流对话统计

#### 方法

```java
List<DailyWorkflowConversationsResponse> dailyWorkflowConversations(String appId, LocalDateTime start, LocalDateTime end);
```

#### 请求参数

| 参数名   | 类型            | 是否必须 | 描述                       |
|-------|---------------|------|--------------------------|
| appId | String        | 是    | 应用 ID                    |
| start | LocalDateTime | 是    | 开始时间，格式：yyyy-MM-dd HH:mm |
| end   | LocalDateTime | 是    | 结束时间，格式：yyyy-MM-dd HH:mm |

#### 响应参数

DailyWorkflowConversationsResponse

| 参数名 | 类型      | 描述               |
|-----|---------|------------------|
| date | String  | 日期，格式：yyyy-MM-dd |
| runs | Integer | 当日工作流运行次数       |

#### 请求示例

```java

@Resource
private DifyServer difyServer;

@Test
public void testGetDailyWorkflowConversations() {
    String appId = "08534c1a-4316-4cd3-806d-bbbca03f58aa";
    LocalDateTime start = LocalDateTime.of(2025, 10, 23, 0, 0);
    LocalDateTime end = LocalDateTime.of(2025, 10, 30, 23, 59);

    // 获取每日工作流对话统计
    List<DailyWorkflowConversationsResponse> dailyWorkflowStats = difyServer.dailyWorkflowConversations(appId, start, end);

    if (dailyWorkflowStats != null) {
        for (DailyWorkflowConversationsResponse dailyStat : dailyWorkflowStats) {
            System.out.println("日期: " + dailyStat.getDate());
            System.out.println("工作流运行次数: " + dailyStat.getRuns());
        }
    }
}
```

### 4.3 获取应用的每日终端用户统计

#### 方法

```java
List<DailyEndUsersResponse> dailyEndUsers(String appId, LocalDateTime start, LocalDateTime end);
```

#### 请求参数

| 参数名   | 类型            | 是否必须 | 描述                       |
|-------|---------------|------|--------------------------|
| appId | String        | 是    | 应用 ID                    |
| start | LocalDateTime | 是    | 开始时间，格式：yyyy-MM-dd HH:mm |
| end   | LocalDateTime | 是    | 结束时间，格式：yyyy-MM-dd HH:mm |

#### 响应参数

DailyEndUsersResponse

| 参数名            | 类型      | 描述               |
|----------------|---------|------------------|
| date           | String  | 日期，格式：yyyy-MM-dd |
| terminalCount  | Integer | 当日终端用户数量        |

#### 请求示例

```java

@Resource
private DifyServer difyServer;

@Test
public void testGetDailyEndUsers() {
    String appId = "08534c1a-4316-4cd3-806d-bbbca03f58aa";
    LocalDateTime start = LocalDateTime.of(2025, 10, 23, 0, 0);
    LocalDateTime end = LocalDateTime.of(2025, 10, 30, 23, 59);

    // 获取每日终端用户统计
    List<DailyEndUsersResponse> dailyEndUsersStats = difyServer.dailyEndUsers(appId, start, end);

    if (dailyEndUsersStats != null) {
        for (DailyEndUsersResponse dailyStat : dailyEndUsersStats) {
            System.out.println("日期: " + dailyStat.getDate());
            System.out.println("终端用户数量: " + dailyStat.getTerminalCount());
        }
    }
}
```

### 4.4 获取应用的平均会话交互统计

#### 方法

```java
List<AverageSessionInteractionsResponse> averageSessionInteractions(String appId, LocalDateTime start, LocalDateTime end);
```

#### 请求参数

| 参数名   | 类型            | 是否必须 | 描述                       |
|-------|---------------|------|--------------------------|
| appId | String        | 是    | 应用 ID                    |
| start | LocalDateTime | 是    | 开始时间，格式：yyyy-MM-dd HH:mm |
| end   | LocalDateTime | 是    | 结束时间，格式：yyyy-MM-dd HH:mm |

#### 响应参数

AverageSessionInteractionsResponse

| 参数名        | 类型   | 描述               |
|-------------|------|------------------|
| date        | String  | 日期，格式：yyyy-MM-dd |
| interactions| Double | 当日平均会话交互数      |

#### 请求示例

```java

@Resource
private DifyServer difyServer;

@Test
public void testGetAverageSessionInteractions() {
    String appId = "08534c1a-4316-4cd3-806d-bbbca03f58aa";
    LocalDateTime start = LocalDateTime.of(2025, 10, 23, 0, 0);
    LocalDateTime end = LocalDateTime.of(2025, 10, 30, 23, 59);

    // 获取平均会话交互统计
    List<AverageSessionInteractionsResponse> averageSessionInteractionsStats = difyServer.averageSessionInteractions(appId, start, end);

    if (averageSessionInteractionsStats != null) {
        for (AverageSessionInteractionsResponse dailyStat : averageSessionInteractionsStats) {
            System.out.println("日期: " + dailyStat.getDate());
            System.out.println("平均会话交互数: " + dailyStat.getInteractions());
        }
    }
}
```

### 4.5 获取应用的每秒令牌统计

#### 方法

```java
List<TokensPerSecondResponse> tokensPerSecond(String appId, LocalDateTime start, LocalDateTime end);
```

#### 请求参数

| 参数名   | 类型            | 是否必须 | 描述                       |
|-------|---------------|------|--------------------------|
| appId | String        | 是    | 应用 ID                    |
| start | LocalDateTime | 是    | 开始时间，格式：yyyy-MM-dd HH:mm |
| end   | LocalDateTime | 是    | 结束时间，格式：yyyy-MM-dd HH:mm |

#### 响应参数

TokensPerSecondResponse

| 参数名        | 类型   | 描述               |
|-------------|------|------------------|
| date        | String  | 日期，格式：yyyy-MM-dd |
| tps         | Double | 当日每秒令牌数        |

#### 请求示例

```java

@Resource
private DifyServer difyServer;

@Test
public void testGetTokensPerSecond() {
    String appId = "08534c1a-4316-4cd3-806d-bbbca03f58aa";
    LocalDateTime start = LocalDateTime.of(2025, 10, 23, 0, 0);
    LocalDateTime end = LocalDateTime.of(2025, 10, 30, 23, 59);

    // 获取每秒令牌统计
    List<TokensPerSecondResponse> tokensPerSecondStats = difyServer.tokensPerSecond(appId, start, end);

    if (tokensPerSecondStats != null) {
        for (TokensPerSecondResponse dailyStat : tokensPerSecondStats) {
            System.out.println("日期: " + dailyStat.getDate());
            System.out.println("每秒令牌数: " + dailyStat.getTps());
        }
    }
}
```

### 4.6 获取应用的用户满意度率统计

#### 方法

```java
List<UserSatisfactionRateResponse> userSatisfactionRate(String appId, LocalDateTime start, LocalDateTime end);
```

#### 请求参数

| 参数名   | 类型            | 是否必须 | 描述                       |
|-------|---------------|------|--------------------------|
| appId | String        | 是    | 应用 ID                    |
| start | LocalDateTime | 是    | 开始时间，格式：yyyy-MM-dd HH:mm |
| end   | LocalDateTime | 是    | 结束时间，格式：yyyy-MM-dd HH:mm |

#### 响应参数

UserSatisfactionRateResponse

| 参数名        | 类型   | 描述               |
|-------------|------|------------------|
| date        | String  | 日期，格式：yyyy-MM-dd |
| rate        | Double | 当日用户满意度率      |

#### 请求示例

```java

@Resource
private DifyServer difyServer;

@Test
public void testGetUserSatisfactionRate() {
    String appId = "08534c1a-4316-4cd3-806d-bbbca03f58aa";
    LocalDateTime start = LocalDateTime.of(2025, 10, 23, 0, 0);
    LocalDateTime end = LocalDateTime.of(2025, 10, 30, 23, 59);

    // 获取用户满意度率统计
    List<UserSatisfactionRateResponse> userSatisfactionRateStats = difyServer.userSatisfactionRate(appId, start, end);

    if (userSatisfactionRateStats != null) {
        for (UserSatisfactionRateResponse dailyStat : userSatisfactionRateStats) {
            System.out.println("日期: " + dailyStat.getDate());
            System.out.println("用户满意度率: " + dailyStat.getRate());
        }
    }
}
```

### 4.7 获取应用的令牌费用统计

#### 方法

```java
List<TokenCostsResponse> tokenCosts(String appId, LocalDateTime start, LocalDateTime end);
```

#### 请求参数

| 参数名   | 类型            | 是否必须 | 描述                       |
|-------|---------------|------|--------------------------|
| appId | String        | 是    | 应用 ID                    |
| start | LocalDateTime | 是    | 开始时间，格式：yyyy-MM-dd HH:mm |
| end   | LocalDateTime | 是    | 结束时间，格式：yyyy-MM-dd HH:mm |

#### 响应参数

TokenCostsResponse

| 参数名        | 类型   | 描述               |
|-------------|------|------------------|
| date        | String  | 日期，格式：yyyy-MM-dd |
| token_count | Integer | 当日令牌数量        |
| total_price | String  | 当日总费用          |
| currency    | String  | 货币类型           |

#### 请求示例

```java

@Resource
private DifyServer difyServer;

@Test
public void testGetTokenCosts() {
    String appId = "08534c1a-4316-4cd3-806d-bbbca03f58aa";
    LocalDateTime start = LocalDateTime.of(2025, 10, 23, 0, 0);
    LocalDateTime end = LocalDateTime.of(2025, 10, 30, 23, 59);

    // 获取令牌费用统计
    List<TokenCostsResponse> tokenCostsStats = difyServer.tokenCosts(appId, start, end);

    if (tokenCostsStats != null) {
        for (TokenCostsResponse dailyStat : tokenCostsStats) {
            System.out.println("日期: " + dailyStat.getDate());
            System.out.println("令牌数量: " + dailyStat.getTokenCount());
            System.out.println("总费用: " + dailyStat.getTotalPrice());
            System.out.println("货币类型: " + dailyStat.getCurrency());
        }
    }
}
```

### 4.8 获取应用的每日消息统计

#### 方法

```java
List<DailyMessagesResponse> dailyMessages(String appId, LocalDateTime start, LocalDateTime end);
```

#### 请求参数

| 参数名   | 类型            | 是否必须 | 描述                       |
|-------|---------------|------|--------------------------|
| appId | String        | 是    | 应用 ID                    |
| start | LocalDateTime | 是    | 开始时间，格式：yyyy-MM-dd HH:mm |
| end   | LocalDateTime | 是    | 结束时间，格式：yyyy-MM-dd HH:mm |

#### 响应参数

DailyMessagesResponse

| 参数名        | 类型   | 描述               |
|-------------|------|------------------|
| date        | String  | 日期，格式：yyyy-MM-dd |
| message_count | Integer | 当日消息数量        |

#### 请求示例

```java

@Resource
private DifyServer difyServer;

@Test
public void testGetDailyMessages() {
    String appId = "08534c1a-4316-4cd3-806d-bbbca03f58aa";
    LocalDateTime start = LocalDateTime.of(2025, 10, 23, 0, 0);
    LocalDateTime end = LocalDateTime.of(2025, 10, 30, 23, 59);

    // 获取每日消息统计
    List<DailyMessagesResponse> dailyMessagesStats = difyServer.dailyMessages(appId, start, end);

    if (dailyMessagesStats != null) {
        for (DailyMessagesResponse dailyStat : dailyMessagesStats) {
            System.out.println("日期: " + dailyStat.getDate());
            System.out.println("消息数量: " + dailyStat.getMessageCount());
        }
    }
}
```
