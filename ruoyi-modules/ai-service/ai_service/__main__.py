"""Start the API by default, or execute an explicit management command."""
import sys
from ai_service.cli import main

if len(sys.argv) == 1:
    sys.argv.append("api")
raise SystemExit(main())
