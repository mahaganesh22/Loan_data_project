from datetime import datetime, timezone
from typing import Any
from pydantic import BaseModel, Field

class ExceptionPayload(BaseModel):
    id: int | None = None
    loanId: str | None = None
    borrowerId: str | None = None
    exceptionType: str | None = None
    severity: str | None = None
    fieldName: str | None = None
    message: str | None = None
    originalValue: str | None = None
    status: str | None = None

class ReviewRequest(BaseModel):
    exception: ExceptionPayload
    loan: dict[str, Any] = Field(default_factory=dict)

class ReviewResponse(BaseModel):
    model: str
    generatedAt: str
    prompt: str
    explanation: str
    messageContext: str | None = None
    suggestedCorrection: str = ""
    reviewerNote: str
    severityRationale: str
    classifiedSeverity: str | None = None
    conflictComparison: str | None = None

class SummaryItem(BaseModel):
    id: int | None = None
    loanId: str | None = None
    exceptionType: str | None = None
    severity: str | None = None
    fieldName: str | None = None
    message: str | None = None

class SummaryRequest(BaseModel):
    exceptions: list[SummaryItem]

class SummaryResponse(BaseModel):
    model: str
    generatedAt: str
    prompt: str
    summary: str
    classifiedSeverity: str | None = None
    openCount: int = 0

class RuleRequest(BaseModel):
    instruction: str

class RuleResponse(BaseModel):
    model: str
    generatedAt: str
    prompt: str
    suggestedRule: str
    testIdea: str

class StructuredReview(BaseModel):
    explanation: str
    suggestedCorrection: str = ""
    reviewerNote: str
    severityRationale: str
    classifiedSeverity: str
    conflictComparison: str = ""

class StructuredSummary(BaseModel):
    summary: str
    classifiedSeverity: str

class StructuredRule(BaseModel):
    suggestedRule: str
    testIdea: str
    
def utc_now() -> str:
    return datetime.now(timezone.utc).isoformat()