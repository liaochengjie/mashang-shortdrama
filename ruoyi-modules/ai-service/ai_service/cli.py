import argparse
import json
import logging
from pathlib import Path

from ai_service.config.settings import HOST, PORT, Settings
from ai_service.schemas.rag import JobRequest, Snapshot, SceneOutput, SearchRequest
from ai_service.repositories.task_repository import Store


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("command", choices=["api", "worker", "init-db", "init-index", "schemas", "delete-build", "doctor", "export-sql", "reconcile", "usage"])
    parser.add_argument("--standalone", action="store_true")
    parser.add_argument("--build-id")
    parser.add_argument("--port", type=int, default=PORT)
    parser.add_argument("--host", default=HOST)
    parser.add_argument("--apply", action="store_true")
    parser.add_argument("--output", type=Path)
    args = parser.parse_args()
    settings = Settings.load()
    store = Store(settings.database_url)
    logging.basicConfig(level=logging.INFO, format="%(asctime)s %(levelname)s %(message)s")
    if args.command == "worker":
        from ai_service.utils.doctor import check
        report=check(settings)
        if not report["readyForIntegration"]:
            print(json.dumps(report,ensure_ascii=False,indent=2))
            return 1
    if args.command == "api":
        import uvicorn
        uvicorn.run("ai_service.main:app", host=args.host, port=args.port)
    elif args.command == "init-db":
        store.initialize()
    elif args.command == "schemas":
        out = args.output or Path(".generated/contracts")
        out.mkdir(parents=True, exist_ok=True)
        for model in (JobRequest, Snapshot, SceneOutput, SearchRequest):
            (out / (model.__name__ + ".schema.json")).write_text(json.dumps(model.model_json_schema(by_alias=True), ensure_ascii=False, indent=2), encoding="utf-8")
        from ai_service.main import app
        (out / "openapi.json").write_text(json.dumps(app.openapi(),ensure_ascii=False,indent=2),encoding="utf-8")
    elif args.command == "doctor":
        from ai_service.utils.doctor import check
        report=check(settings)
        print(json.dumps(report,ensure_ascii=False,indent=2))
        return 0 if report["readyForIntegration"] else 1
    elif args.command == "export-sql":
        from ai_service.repositories.task_repository import SCHEMA
        out = args.output or Path(__file__).resolve().parents[3] / "script/sql/update/ai_rag_v1.sql"
        out.parent.mkdir(parents=True, exist_ok=True)
        out.write_text("-- Run explicitly in the dedicated kcat_rag database. No business table access.\n"+";\n".join(s+" ENGINE=InnoDB DEFAULT CHARSET=utf8mb4" for s in SCHEMA)+";\n",encoding="utf-8")
    elif args.command == "usage":
        if not args.build_id:parser.error("--build-id required")
        print(json.dumps(store.usage(args.build_id),ensure_ascii=False,indent=2))
    else:
        from ai_service.repositories.vector_repository import MilvusIndex
        index = MilvusIndex(settings)
        if args.command == "init-index":
            index.initialize()
        elif args.command == "delete-build":
            if not args.build_id:
                parser.error("--build-id required (only retire a non-published build)")
            from ai_service.services.content_client import ContentClient
            if not ContentClient(settings).build_status(args.build_id).get("retirable"):
                parser.error("Build is published, current draft, or unregistered; retirement refused")
            store.retire(args.build_id)
            index.delete_build(args.build_id)
        elif args.command == "reconcile":
            from ai_service.services.content_client import ContentClient
            content=ContentClient(settings)
            for build in store.builds(settings.profile):
                status=content.build_status(build["build_id"])
                report={"buildId":build["build_id"],"localState":build["state"],"authority":status,"retired":False}
                if args.apply and status.get("retirable"):
                    store.retire(build["build_id"])
                    index.delete_build(build["build_id"])
                    report["retired"]=True
                print(json.dumps(report,ensure_ascii=False))
        else:
            from ai_service.services.content_client import ContentClient
            from ai_service.services.bailian import Bailian
            from ai_service.services.pipeline import Pipeline
            from ai_service.services.worker import Worker
            Worker(settings, store, Pipeline(settings, store, Bailian(settings), index, ContentClient(settings))).run(args.standalone)


if __name__ == "__main__":
    raise SystemExit(main())
