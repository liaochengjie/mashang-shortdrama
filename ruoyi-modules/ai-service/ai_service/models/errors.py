class RagError(Exception):
    def __init__(self, code: str, retryable: bool = False, status: int = 503):
        super().__init__(code)
        self.code, self.retryable, self.status = code, retryable, status


class LostLease(RagError):
    def __init__(self):
        super().__init__("LOST_LEASE", status=409)


class Superseded(RagError):
    def __init__(self):
        super().__init__("SUPERSEDED", status=409)
