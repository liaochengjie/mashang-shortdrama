# AI Service

Kcat 的独立 Python AI 模块，使用 FastAPI 提供 HTTP 服务，预置 LangChain 和 LangGraph 依赖。要求 Python 3.10 或更高版本，默认监听 `127.0.0.1:7777`。

## 本地启动（PowerShell）

在项目根目录执行：

```powershell
cd ruoyi-modules/ai-service
python -m venv .venv
.\.venv\Scripts\python.exe -m pip install -e .
.\.venv\Scripts\python.exe -m ai_service
```

健康检查：<http://127.0.0.1:7777/health>

接口文档：<http://127.0.0.1:7777/docs>

开发时需要自动重载，可使用：

```powershell
.\.venv\Scripts\python.exe -m uvicorn ai_service.main:app --host 127.0.0.1 --port 7777 --reload
```

Linux / macOS 将上述虚拟环境中的 Python 路径替换为 `.venv/bin/python`。

## 基础分层

这些是本模块采用的分层约定，并非 Python 强制要求的目录。每个分类目录都包含 `__init__.py`，可以作为 Python 包导入。

```text
ai-service/
├── ai_service/
│   ├── __init__.py
│   ├── __main__.py          # 服务启动入口
│   ├── main.py              # 创建 FastAPI 应用、注册路由
│   ├── config/
│   │   ├── __init__.py
│   │   └── settings.py      # 应用配置，默认端口 7777
│   ├── controllers/
│   │   ├── __init__.py
│   │   └── health.py        # 健康检查接口
│   ├── services/            # 业务逻辑
│   ├── schemas/             # 请求、响应的数据校验模型
│   ├── models/              # 领域实体、后续持久化模型
│   ├── repositories/        # 数据访问
│   └── utils/               # 公共工具
├── .gitignore
├── pyproject.toml           # Python 包及依赖声明
└── README.md
```

| Python 包 | 对应 Java 分层 | 职责 |
| --- | --- | --- |
| `config` | Config | 应用配置及基础设施初始化配置 |
| `controllers` | Controller | 定义 HTTP 路由、接收请求、调用服务 |
| `services` | Service | 实现业务逻辑、组织业务流程 |
| `schemas` | DTO / VO | 定义请求和响应的数据结构，按需使用 Pydantic 校验 |
| `models` | Entity / Domain | 定义领域实体及后续持久化模型 |
| `repositories` | Mapper / DAO / Repository | 封装数据库等数据源访问 |
| `utils` | Utils | 与具体业务无关的公共工具 |

后续新增接口时，在 `controllers` 中定义 `APIRouter`，在 `main.py` 中通过 `app.include_router(...)` 注册；业务逻辑放在 `services`，需要持久化时再添加 `repositories` 和 `models` 的实现。

目前只实现配置、应用入口和健康检查，其余分类包为空骨架，没有调用模型，不需要 API Key。模型集成和业务代码后续按需添加。

该模块独立安装和运行，不加入 Maven 构建；当前未接入网关、Nacos、数据库或鉴权。`/health` 仅检查服务进程是否可用。
