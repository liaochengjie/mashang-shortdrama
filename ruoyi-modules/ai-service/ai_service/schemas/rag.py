from __future__ import annotations

from typing import Literal
from pydantic import BaseModel, ConfigDict, Field, model_validator
from pydantic.alias_generators import to_camel


class Contract(BaseModel):
    model_config = ConfigDict(alias_generator=to_camel, populate_by_name=True, extra="forbid", allow_inf_nan=False)


class Media(Contract):
    identity: str = Field(min_length=1, max_length=512)
    url: str = Field(min_length=1)
    sha256: str = Field(pattern=r"^[a-f0-9]{64}$")


class Episode(Contract):
    episode_id: str = Field(pattern=r"^[0-9]+$")
    episode_number: int = Field(ge=1)
    description: str = ""
    title: str = ""
    cover: str = ""
    duration: float | None = Field(default=None, gt=0)
    media: Media
    subtitle: Media | None = None


class Snapshot(Contract):
    snapshot_id: str
    drama_id: str = Field(pattern=r"^[0-9]+$")
    source_version: int = Field(ge=1)
    source_hash: str = Field(pattern=r"^[a-f0-9]{64}$")
    title: str
    description: str = ""
    story_line: str = ""
    cover: str = ""
    actors: list[dict] = Field(default_factory=list)
    tags: list[str] = Field(default_factory=list)
    categories: list[str] = Field(default_factory=list)
    episodes: list[Episode] = Field(min_length=1)

    @model_validator(mode="after")
    def unique_episodes(self):
        if len({e.episode_id for e in self.episodes}) != len(self.episodes):
            raise ValueError("duplicate episodeId")
        if len({e.episode_number for e in self.episodes}) != len(self.episodes):
            raise ValueError("duplicate episodeNumber")
        return self


class Evidence(Contract):
    kind: Literal["observation", "dialogue", "background", "inference"]
    text: str = Field(min_length=1, max_length=4000)
    source_refs: list[str] = Field(min_length=1)
    start_time: float | None = Field(default=None, ge=0)
    end_time: float | None = Field(default=None, ge=0)


class Scene(Contract):
    summary: str = Field(min_length=1, max_length=4000)
    participants: list[str] = Field(default_factory=list)
    evidence: list[Evidence] = Field(min_length=1)
    uncertain: bool = False


class SceneOutput(Contract):
    scenes: list[Scene] = Field(min_length=1, max_length=30)


class JobRequest(Contract):
    snapshot_id: str = Field(min_length=1,max_length=36,pattern=r"^[A-Za-z0-9-]+$")
    drama_id: str = Field(pattern=r"^[0-9]+$")
    source_version: int = Field(ge=1)
    pipeline_version: str = Field(min_length=1,max_length=80)
    embedding_profile: str = Field(min_length=1,max_length=80)


class Candidate(Contract):
    drama_id: str
    source_version: int
    build_id: str
    embedding_profile: str
    episode_id: str | None = None


class SearchRequest(Contract):
    query: str = Field(min_length=1, max_length=500)
    page: int = Field(default=1, ge=1)
    page_size: int = Field(default=15, ge=1, le=30)
    session_id: str | None = None

    @model_validator(mode="after")
    def nonblank(self):
        self.query = self.query.strip()
        if not self.query:
            raise ValueError("query is blank")
        if self.page > 1 and not self.session_id:
            raise ValueError("sessionId is required after page 1")
        return self
