# Kcat AI Service

作者：liaochengjie。沿用原有 `ai-service` 的 Python 分层，集中实现多模态证据索引与情节检索，不加入 Maven reactor。Python 3.12，默认监听 `127.0.0.1:7777`；API 与索引 worker 使用同一份代码，分别运行。

## 目录与职责

```text
ai-service/
├── ai_service/
│   ├── __main__.py          # python -m ai_service 启动入口
│   ├── main.py              # FastAPI 应用及路由注册
│   ├── cli.py               # worker、配置检查和运维命令
│   ├── config/              # settings.py，配置与原有服务元信息
│   ├── controllers/         # health.py、rag.py，HTTP 接口
│   ├── services/            # 模型、媒体处理、索引流程、搜索及服务组合
│   ├── schemas/             # 请求、快照和场景校验模型
│   ├── models/              # 领域错误
│   ├── repositories/        # 任务持久化、Milvus 访问
│   └── utils/               # HTTP 传输、本地配置检查
├── .env.example             # 配置样例
├── pyproject.toml           # 包信息、作者和固定直接依赖
└── requirements.lock        # 安装依赖锁定
```

AI 逻辑集中在本模块；内容审核、VOD 完成确认和最终上架由既有 Java 服务负责。Java 接入类沿用各服务原有 controller、biz/impl、business/impl、config/Config 和 job 分层。只有同一内容版本的审核、媒体处理和索引验收全部完成后才自动上架，搜索入口对所有人开放。

## 本地运行

在仓库的 `ruoyi-modules/ai-service` 中执行。首次运行需先按下方步骤安装，已有 `.venv` 时可直接启动：

```powershell
& '.venv/Scripts/python.exe' -m ai_service
# 或指定端口
& '.venv/Scripts/python.exe' -m ai_service.cli api --port 7777
```

健康接口为 <http://127.0.0.1:7777/health>，接口文档为 <http://127.0.0.1:7777/docs>。`/health/ready` 检查实际依赖，缺配置或依赖时返回 503。启动 API 不会建表、启动 Docker 或调用模型。

```powershell
& '.venv/Scripts/python.exe' -m ai_service.cli doctor
```

测试和评测工具仅在本地开发目录保留，不随本仓库提交，API、worker 和运维 CLI 均不依赖它们。契约文件用 `python -m ai_service.cli schemas` 按需导出到忽略的 `.generated/contracts/`。

`doctor` 仅检查本地配置，不访问外部服务或输出密钥，缺配置时退出码为 1。后续联调前需要补 `BAILIAN_WORKSPACE_ID`，或同时填写业务空间的 `BAILIAN_BASE_URL` 与 `BAILIAN_RERANK_URL`。新机器复制 `.env.example` 为 `.env` 并填写配置；已有 `.env` 时勿覆盖。Java 配置需另行通过环境或 Nacos 注入，`RAG_PYTHON_URL` 默认使用 7777。

## 安装与后续联调

新机器安装：

```powershell
py -3.12 -m venv .venv
& '.venv/Scripts/python.exe' -m pip install -r requirements.lock
& '.venv/Scripts/python.exe' -m pip install --no-deps --no-build-isolation -e .
```

SQL 统一放在项目 `script/sql/update/`，其中 `ai_rag_v1.sql` 仅用于独立 AI 数据库。Docker、数据库迁移、真实视频与模型测试留待后续联调；完成依赖准备后再运行 `init-db`、`init-index` 和独立 `worker`。

详细操作见 [运行手册](../../docs/ai/runbook.md)；接口、分页和证据字段见 [接口契约](../../docs/ai/contracts.md)。
