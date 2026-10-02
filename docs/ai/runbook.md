# AI Service 本地运行与联调

更新：2026-10-02。链路和 Docker 在本机运行。首次审核通过的内容在转码和索引验收结束后自动上架，所有人都能搜索。本仓库提供运行源码和公开配置样例，测试、评测工具及独立演示模块仅在本地开发目录保留。

## 当前实现及验证边界

| 项目 | 验证状态 |
| --- | --- |
| 独立 Python API/worker、持久化任务、租约、阶段复用、证据、混合检索、分页 | 已实现；35 项离线单元/Mock 测试通过 |
| Java 发布快照、审批 outbox、VOD 结果查询、发布 tuple、App 聚合和内部身份 | 已实现；三个服务编译通过 |
| 发布门槛、旧版本、HTTP 冲突契约、BPMN 合流/拒绝/取消 | 9 项 Java 测试通过；流程测试使用真实 Camunda 7.23 引擎和 H2、模拟服务 delegate |
| App 情节展示、去抖、旧请求隔离、按集 ID 和可信时间跳转 | 前端构建通过，2 项导航测试通过 |
| Python 包安装与临时本地 API | 可编辑安装成功；live=200，缺依赖 ready=503；测试进程已关闭 |
| MySQL 迁移、真实 Milvus/BM25 和服务部署 | Compose 配置校验通过，容器未启动；没有执行业务库迁移 |
| 百炼四种模型、真实视频/音频、真实 VOD、多服务完整流程 | 待联调；本轮没有发送真实模型请求 |
| 50～100 条人工标签、Recall@5、误报、延迟和费用 | 评测工具保留在本地开发目录；未标注，不能报告准确率 |

Python 的结构和启动入口见 [AI Service](../../ruoyi-modules/ai-service/README.md)，接口见 [契约文档](contracts.md)。测试和运行报告按需生成，不放入交付源码。

## 联调前要补的配置

1. **百炼 WorkspaceId 或业务空间调用域名**：用户要求先保留，`doctor`、worker 启动和最小模型验证都会提醒。配置项 `BAILIAN_WORKSPACE_ID`；若使用不同地域或自定义域名，同时设置 `BAILIAN_BASE_URL` 和 `BAILIAN_RERANK_URL`。现有 API Key 不需再次发送。
2. 分别生成本地 `RAG_INTERNAL_TOKEN`、`CAMUNDA_INTERNAL_TOKEN`、`CAMUNDA_WORKER_TOKEN`。content/user/Python 共享第一项，content/Camunda 共享第二项，worker/Camunda 共享第三项。不要把这些令牌交给 App。
3. 在现有本机 MySQL 中创建独立逻辑库 `kcat_rag` 和独立账号，配置 `RAG_DATABASE_URL`。账号只访问独立库；Python 不读内容业务表。URL 中的密码特殊字符须 URL 编码。
4. 安装 FFmpeg 与 ffprobe，并使 Python worker 的 PATH 可访问。当前机器 PATH 尚未找到这两个工具；没有自动安装。
5. 检查本机 MinIO 素材地址与允许主机、腾讯 VOD 的既有凭据和 procedure、原流程 AI 审核所需 Ollama，以及 Nacos/Redis/网关等项目原有依赖。

首次部署时将 `ai-service/.env.example` 复制为 `.env` 并填写配置；已有 `.env` 时按样例合并，勿覆盖原文件。`.env` 被 Git 忽略。Java 不自动读取 Python `.env`。

## 迁移与首次启动顺序

以下是后续操作说明，本轮没有执行这些数据库或 Docker 命令。

1. 导出现有内容业务库、Camunda 引擎库备份；保持 `RAG_ENABLED=false`，先应用新增表。`script/sql/update/kcat_rag_v1.sql` 在 content-service 当前逻辑库执行，只新增五张 `kcat_rag_*` 表；`camunda_rag_v1.sql` 在 Camunda 实际 datasource 指向的逻辑库执行，只新增流程启动去重表。两份 Java SQL 为一次性增量脚本，不重复执行。
2. 在独立 `kcat_rag` 库执行 `script/sql/update/ai_rag_v1.sql`，或配置好连接后运行 `python -m ai_service.cli init-db`。七张表使用 InnoDB。服务不会启动时自行建表。
3. 启动隔离 Milvus Compose。固定版本为 Milvus/PyMilvus 2.5.16、etcd 3.5.18、MinIO 2024-05-28；这些组合已通过客户端 Schema 构造校验，容器兼容性仍待本机实测。建议为独立组件准备至少 4 CPU、8 GB 内存及足够磁盘，并结合实际视频量调整，不作为性能保证。

```powershell
# 从 kcat 目录，先在本地设置 RAG_MINIO_USER/RAG_MINIO_PASSWORD
docker compose -f script/docker/docker-compose.rag.yml config --quiet
docker compose -f script/docker/docker-compose.rag.yml up -d
docker compose -f script/docker/docker-compose.rag.yml ps
```

只绑定 loopback：Milvus 19530、Milvus 健康端口 19091、专用 MinIO 19000/19001。容器内 MinIO 仍为 9000，三个命名卷 `rag-etcd/rag-minio/rag-milvus` 与既有素材存储隔离。不要使用 `down -v` 删除已有索引。

4. 确认 Milvus 健康接口可访问、连接配置正确。真实数据写入、中文 BM25 检索及重启持久化的验证在本地联调目录进行；相关验证脚本不随本仓库提交。
5. 执行 `doctor`，确认 profile；默认 `RAG_EMBEDDING_PROFILE=p_e5e37f16c3c03cba4e62`。Java content 和 Python 必须使用相同 pipeline/profile。Java content/user 用 `--spring.profiles.active=dev,rag`（结合当前部署 profile），环境中设置 `RAG_ENABLED=true`；Camunda 同样加载 rag profile。把两处 gateway YAML 的新增 `/api/**` 路由和两个精确匿名入口同步到实际 Nacos dataId，不开放 `/internal/**`。
6. 运行 `init-index`，启动 Python API 和独立 worker，启动三个 Java 服务。真实服务启动还需项目原有基础设施，本轮只验证编译和模拟流程。
7. 配好百炼业务空间后，在本地联调目录验证真实视频和音频的模型调用。素材须为真实短片段；视频切片不带音轨，音频独立送入模型。模型名和端点由当前账户实测确认。
8. 上传新素材并发布，查看 `/dramas/rag-draft/{dramaId}`。管理端人工审核请求必须提交固定 snapshotId/processId；审批写入后由 outbox 推进 Camunda。独立管理后台表单不在本次 App 前端改动内，须接入这两个字段。
9. 核实 VOD 每集真实 FINISH/SUCCESS、Python 全部必需集覆盖、可见性和 dense/BM25 查询。两者就绪后流程 finish 原子切换发布 tuple，首次自动上架，随后用 App 搜索和点击播放。

## 旧数据与旧流程

- 不直接把已有 `audit_status=1` 的业务行当作新 RAG 发布版本。新模式首页和播放只读取正式发布快照，旧数据须经过回填与审核；启用前准备好回填批次，避免旧列表暂时为空。
- 历史视频和字幕先核实实际文件 SHA-256，以不可变文件名或对象版本作为 identity，调用 `/internal/rag/assets`。已有新上传会在 MinIO 上传流中计算摘要并登记。不要填随意摘要绕过验证。
- 对历史剧调用 `/internal/rag/dramas/{id}/rebuild` 生成版本化快照和新流程，再按固定事实审核。force rebuild 会重新审核；审核失败期间旧的新式发布版本仍在线。
- 原 `DramaAuthProcess` BPMN 和实例保留，新实例使用 `DramaAuthProcessV2`。旧实例涉及已纳入新模式的剧时，旧审核回写/VOD 入口返回 `LEGACY_PROCESS_REQUIRES_MANUAL_MAPPING`，由运维核实后取消或映射旧实例，不让旧回调覆盖新版本。
- 业务 CRUD、关系服务变更已挂接版本刷新；直接 SQL 或直接 mapper 绕过服务层的修改不自动生成快照，运维必须显式重建。删除最后一集的管理修改会因完整性校验回滚，应删除整剧或先补齐必需剧集。

## 故障与重试

先确认 snapshotId、sourceVersion、buildId、profile、外部 taskId 和对应 Camunda 实例，禁止按 dramaId 猜当前任务。

| 故障 | 处理 |
| --- | --- |
| outbox FAILED | 修复 Camunda/令牌/当前人工任务，再 POST `/internal/rag/outbox/{eventId}/retry`；事件唯一键避免重复启动/决定 |
| Python FAILED | 修复素材、工具、模型、Milvus或登记错误，POST Python `/internal/index-jobs/{jobId}/retry`；复用原 buildId 与阶段缓存 |
| External Task retries=0 | 修复后先恢复 Python 作业，再用 Camunda 运维 token POST `/internal/rag/external-tasks/{taskId}/retry`；不能无限自动重试 |
| VOD FAILED | 修复账号/procedure/素材，POST content `/internal/rag/snapshots/{id}/media/retry`；再恢复 Camunda PollMedia 的 incident |
| 普通异步 job incident | 通过 Camunda 管理工具核实流程/活动并恢复有限次数重试；技术失败不能改成 READY |
| SUPERSEDED | 当前版本已替代，结束旧工作；不能用重试将旧结果发布 |
| 搜索 session 410/409 | 重新发第一页搜索，不混用 query 或 pageSize |
| 下架/删除 | 内容权威校验立即排除，新页和播放再次校验；不等待 Milvus 删除完成 |

HTTP 401/403 和结构不合法不反复重试；429/超时/5xx 有上限及退避。Camunda worker 默认三次任务尝试，outbox 八次，媒体失败三次。VOD 查询超过四小时转入失败处理，READY 必须由真实结果产生。SDK 上传结果在持久化前丢失仍可能重复上传，已有 fileId/taskId 则复用；没有声称云上传 exactly-once。

## 对账、清理与 profile 迁移

```powershell
# 从仓库根目录执行
Set-Location ruoyi-modules/ai-service
& '.venv/Scripts/python.exe' -m ai_service.cli reconcile
# 审阅对账结果后显式应用
& '.venv/Scripts/python.exe' -m ai_service.cli reconcile --apply
& '.venv/Scripts/python.exe' -m ai_service.cli delete-build --build-id 'BUILD_UUID'
& '.venv/Scripts/python.exe' -m ai_service.cli usage --build-id 'BUILD_UUID'
```

清理只允许权威服务确认不再是 published、也不是 currentDraft 的构建；活跃租约禁止清理。先把本地构建退休并阻止重新领取，再删除 Milvus；删除失败时重复对账继续清理。下架仍保留发布构建，便于重新上架；删除整剧后当前实现也保守保留其发布构建，物理清除须另外核实保留策略。

未登记的半成品不会自动物理删除。先检查是否仍有活动作业，确认没有引用后人工清理 probe 或孤立文件；不按模糊目录匹配删除。阶段缓存和原始片段有成本/保留需求，暂不自动清除。

首版只服务一个已发布 embedding profile；检测到上架剧的 profile 不一致会明确报错，避免混用向量或静默遗漏。模型/维度迁移需建立新 collection，用新 profile 逐剧重建，在维护窗口完成审批和发布切换，再切换在线服务并验收；没有实现同时查询多个 profile。提示词、融合模型、预处理或切割参数变化也须递增 pipelineVersion，不能只覆盖旧缓存。

## 回滚

停止新 worker/outbox、暂停新 V2 实例推进，导出七张独立库表、五张内容表及引擎去重表。将 RAG_ENABLED 关闭、撤销新增 App gateway 路由并恢复旧消费者，核实新上架剧在旧接口中的表现；新转码地址保存在版本媒体表，旧接口不自动具备这些地址，回滚前需做好对应回填或保持下架。

不自动删除新表和卷，不修改/取消旧实例。确需删除，先核实没有活动引用并保留备份，再按 SQL 内的回滚说明移除专用新增表。原业务表和原 BPMN 不在删除范围内。

## 构建与运行检查

Python：安装依赖后执行 `python -m ai_service.cli doctor`，再启动 API，检查 `/health` 和 `/health/ready`。前端是独立工程，在 `kcat-app` 执行 `npm run build`。

Java 21，在后端仓库根目录构建四个业务服务及其依赖：

```powershell
# JAVA_HOME 使用本机 JDK 21
mvn -q -pl ruoyi-modules/content-service,ruoyi-modules/camunda-service,ruoyi-modules/user-service,ruoyi-modules/interaction-service -am -DskipTests package
```

测试源码、评测脚本和模拟数据被 Git 忽略，不是构建或启动的前提。测试继续在本地开发目录执行。

## 协议资料与待实测边界

本轮核对了 [百炼 Omni](https://help.aliyun.com/zh/model-studio/qwen-omni)、[文本向量](https://help.aliyun.com/zh/model-studio/text-embedding-synchronous-api)、[文本重排](https://help.aliyun.com/zh/model-studio/text-rerank-api)、Milvus 2.5 的 [BM25](https://milvus.io/docs/v2.5.x/full-text-search.md)、[中文 analyzer](https://milvus.io/docs/v2.5.x/chinese-analyzer.md)、[nullable](https://milvus.io/docs/v2.5.x/nullable-and-default.md)，以及 [Camunda 7.23 External Tasks](https://docs.camunda.org/manual/7.23/user-guide/process-engine/external-tasks/)。文档核对和模拟通过不等于当前账号/本机容器验证。

待实测重点：业务空间访问权限和模型配额、本地视频/音频协议及可辨语音、Milvus真实可见性/中文分词/重启持久化、MySQL事务和 Camunda 共享 datasource/事务管理器、完整 Spring 依赖启动、VOD 结果与时轴不变、管理端审批字段、真实网关匿名入口、缓存失效和播放加载事件，以及人工标注后的检索质量。

## 目录整理约定

作者：liaochengjie。AI 实现统一放在 `ruoyi-modules/ai-service/ai_service`，沿用既有 config/controllers/services/schemas/models/repositories/utils 分层。Java 的接入类放回原有 controller、biz/impl、business/impl、config/Config 和 job；审核、媒体完成和发布归原业务服务负责。服务默认端口为 7777，Java 的 `RAG_PYTHON_URL` 必须与实际端口一致。

测试源码、评测材料、独立 `ruoyi-example` 模块及用户服务调试接口仅在本地保留，不随本仓库提交。请假流程、Camunda 原有流程和框架任务处理器继续保留。SQL 统一在 `script/sql/update/`；OpenAPI/Schema 用 `python -m ai_service.cli schemas` 按需导出到忽略的 `.generated/contracts/`。
