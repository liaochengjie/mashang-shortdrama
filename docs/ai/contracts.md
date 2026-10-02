# RAG 接口契约

更新：2026-10-02。内部接口直接返回 JSON；App 接口沿用 `R.ok` 的 `code/data` 包装。版本冲突使用真实 HTTP 409，依赖不可用使用 503。新增专用异常 advice 避免旧通用处理器把错误变成 HTTP 200。

## 身份与数据类型

Python、content-service 内部接口：`Authorization: Bearer <RAG_INTERNAL_TOKEN>`。Camunda 内部流程接口使用 `CAMUNDA_INTERNAL_TOKEN`，External Task REST 使用独立 `CAMUNDA_WORKER_TOKEN`，仅允许 RAG 主题的 fetch/extend/complete/failure。内部接口不加入网关匿名白名单。

所有业务 ID 按字符串传输；sourceVersion 为整数（客户端兼容数字字符串），真实媒体身份和 SHA-256 固定在快照。快照 URL 可以带签名，但不参与媒体内容哈希。服务端当前实现不自动刷新已过期的签名 URL，联调使用可供本机 worker 访问的稳定地址，或延长签名有效期。

## content-service，默认 10001

| 方法和路径 | 行为 |
| --- | --- |
| GET `/internal/rag/health` | 检查新增表可访问 |
| GET `/internal/rag/snapshots/{snapshotId}` | 读取指定、当前且已批准的固定快照；旧版本 409 SUPERSEDED |
| POST `/internal/rag/content/validate` | `{"candidates":[...]}` → `{"items":[...valid/title/cover/actors]}`，最多 240 个候选 |
| PUT `/internal/rag/index-results/{buildId}` | 指定版本 READY、profile、pipeline、episodeIds、documentCount 登记；不能登记旧版本或替换同版本 build |
| GET `/internal/rag/published-builds?profile=...&after=0` | 当前上架且可检索的构建，500 条/页，`next` 为下一页 dramaId |
| GET `/internal/rag/builds/{buildId}` | 构建是否 published/currentDraft/retirable；未登记构建不自动清理 |
| GET `/internal/rag/published/{dramaId}/episodes` | 当前发布快照和转码产物；不读取新草稿事实；下架/删除 410 |
| POST `/internal/rag/snapshots/{id}/media` | 持久化启动媒体状态，启动不等于完成 |
| GET `/internal/rag/snapshots/{id}/media` | PENDING/PROCESSING/READY/FAILED |
| POST `/internal/rag/snapshots/{id}/media/retry` | 修复后恢复失败媒体任务，保留已上传 fileId，清空失败 taskId |
| POST `/internal/rag/snapshots/{id}/finish` | `{"approve":true}`，审核+媒体+索引同时就绪才原子切换并自动上架；保留人工下架意图 |
| POST `/internal/rag/assets` | 为历史素材登记 url/identity/sha256/sizeBytes；调用方必须实际核实文件摘要 |
| POST `/internal/rag/dramas/{id}/rebuild` | 强制新 sourceVersion、新快照及新审核，旧发布继续有效 |
| POST `/internal/rag/outbox/{eventId}/retry` | 修复后恢复失败启动/审核决定事件 |

管理员经现有 content 路由访问 `GET /dramas/rag-draft/{dramaId}`，取得 snapshotId/processId/sourceVersion、固定审核事实与处理状态；需要 `content:dramas:query` 权限。审核调用现有 `POST /dramas/authcheck`，新增必须提交 snapshotId 和 processId，需要 `content:dramas:edit` 权限。先持久化审批决定再推进流程，旧版本决定返回 409。

```json
{"dramaId":"9223372036854775806","snapshotId":"SNAPSHOT_UUID","processId":"PROCESS_ID","auditStatus":"1","auditReason":"人工核实通过"}
```

此接口为管理端接入提供数据；本次未改独立管理后台的审核表单，已有表单须按上述字段提交。

## AI Service，默认 7777

| 方法和路径 | 行为 |
| --- | --- |
| GET `/health`、`/health/live` | 进程状态，无模型调用 |
| GET `/health/ready` | 配置及 DB/Milvus/content 依赖，未就绪 503 |
| POST `/internal/index-jobs` | 持久化、幂等接受作业；202 和 jobId 不代表索引完成 |
| GET `/internal/index-jobs/{jobId}` | buildId/state/progress/error/attempts |
| POST `/internal/index-jobs/{jobId}/retry` | 恢复 FAILED 作业；Camunda incident 的重试次数另外恢复 |
| POST `/internal/search/dramas` | 稳定候选会话的情节检索 |

```json
{"snapshotId":"SNAPSHOT_UUID","dramaId":"9223372036854775806","sourceVersion":1,"pipelineVersion":"evidence-v1","embeddingProfile":"p_e5e37f16c3c03cba4e62"}
```

```json
{"query":"女主在婚礼摘下戒指离开","page":1,"pageSize":15}
```

后续页须携带相同 query/pageSize/sessionId。会话窗口最多 100 剧（实际结果受召回和重排预算影响）、TTL 600 秒；TTL 不因翻页续期。没有精确全库 total，使用 `windowSize/hasMore`；`ttlSeconds` 表示配置的会话寿命。每页保留固定排名位置并重新校验，已下架条目被移除，页面可能不足 pageSize。过期 410，query/页大小不一致 409，超过窗口 400。

## App，经网关访问

`GET /api/search/dramas?keyword=...&page=1&pageSize=15`，后续增加 sessionId。无需登录或 VIP。响应示例（合成数据，不代表真实命中）：

```json
{"code":200,"data":{"list":[{"id":"1","dramaId":"1","title":"示例剧","cover":"","actors":[],"matchedEpisodeId":"11","episodeNumber":1,"matchText":"我已经有妻子了","parentSummary":"婚礼上发生冲突","evidenceType":"dialogue","startTime":12.5,"endTime":14.0,"canSeek":true,"sourceVersion":1,"buildId":"BUILD_UUID","embeddingProfile":"p_e5e37f16c3c03cba4e62"}],"sessionId":"SESSION_UUID","windowSize":1,"hasMore":false,"ttlSeconds":600,"degraded":[]}}
```

点击后调用 `GET /api/rag/dramas/{id}/playback?sourceVersion=...&buildId=...&embeddingProfile=...&episodeId=...`。重新核实完整发布 tuple，版本更新 409，下架 410，内容服务无法校验 503。前端按 episodeId 找真实数组位置；只有 ID/媒体身份/版本和 loadedmetadata 匹配时才定位时间。普通播放仍兼容原有入口。

Embedding 故障可降级 `BM25_ONLY`，重排故障可降级 `RERANK_UNAVAILABLE`，均重新校验内容；Milvus 或内容权威校验失败则报错。分数仅用于排序，暂定阈值需人工样本校准。

## Camunda，默认 8088

`POST /internal/rag/processes`：稳定业务键 `drama:{dramaId}:{sourceVersion}`，专用去重表和启动事务，响应丢失后可重复请求。`POST /internal/rag/decisions`：固定 snapshotId/processId，审批决定幂等且不能翻转。

`POST /internal/rag/external-tasks/{taskId}/retry`：仅运维 token 可恢复 RAG 主题失败任务的三次重试。External REST workerId 采用 `rag-<UUID>`，fetch 的唯一主题为 `drama-rag-index`，其余写入还核实任务主题、流程 key 和锁持有者。

新流程 key 为 `DramaAuthProcessV2`，旧 `DramaAuthProcess` 文件与存量实例未迁移。新 key 中模型审核异步执行，人工决定落库后并行处理原始媒体的 RAG 和真实 VOD 转码，timer 查询真实媒体结果，合流后调用 finish。旧版本的 409 SUPERSEDED 中断流程；不可把普通 409 作为版本替代。
