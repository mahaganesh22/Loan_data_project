from fastapi import FastAPI, HTTPException
from chains import (
    MissingOpenAiKeyError,
    build_review_prompt,
    model_name,
    aimodel_configured,
    review_runnable,
    rule_runnable,
    summary_runnable,
)
from models import (
    ReviewRequest,
    ReviewResponse,
    RuleRequest,
    RuleResponse,
    SummaryRequest,
    SummaryResponse,
    utc_now,
)

app = FastAPI(title="Loan Data AI Review", version="1.0.0")

@app.get("/health")
def health():
    configured = aimodel_configured()
    return {
        "status": "ok" if configured else "missing_api_key",
        "model": model_name(),
        "openaiConfigured": configured,
    }

@app.post("/review", response_model=ReviewResponse)
def review(request: ReviewRequest):
    
    try:
        result = review_runnable.invoke({"exception": request.exception, "loan": request.loan})
    except MissingOpenAiKeyError as exc:
        raise HTTPException(status_code=503, detail=str(exc)) from exc
    except Exception as exc:
        print("AI REVIEW ERROR:", repr(exc))
        raise HTTPException(
            status_code=502,
            detail=f"AI review failed: {exc}"
        ) from exc
    # except Exception as exc:
    #     raise HTTPException(status_code=502, detail=f"AI review failed: {exc}") from exc


    if result is None:
        raise HTTPException(
            status_code=502,
            detail="AI model returned no structured review."
        )
    
    prompt = build_review_prompt(request.exception)

    return ReviewResponse(
        model=model_name(),
        generatedAt=utc_now(),
        prompt=prompt,
        explanation=result.explanation,
        messageContext=request.exception.message,
        suggestedCorrection=result.suggestedCorrection or "",
        reviewerNote=result.reviewerNote,
        severityRationale=result.severityRationale,
        classifiedSeverity=result.classifiedSeverity,
        conflictComparison=result.conflictComparison,
    )

@app.post("/summarize", response_model=SummaryResponse)
def summarize(request: SummaryRequest):
    try:
        result = summary_runnable.invoke(request.exceptions)
    except MissingOpenAiKeyError as exc:
        raise HTTPException(status_code=503, detail=str(exc)) from exc
    except Exception as exc:  # noqa: BLE001
        raise HTTPException(status_code=502, detail=f"AI summary failed: {exc}") from exc
    prompt = f"Summarize {len(request.exceptions)} loan validation exceptions for the reviewer queue."
    return SummaryResponse(
        model=model_name(),
        generatedAt=utc_now(),
        prompt=prompt,
        summary=result.summary,
        classifiedSeverity=result.classifiedSeverity,
        openCount=len(request.exceptions),
    )

@app.post("/rules", response_model=RuleResponse)
def rules(request: RuleRequest):
    if not request.instruction.strip():
        raise HTTPException(status_code=400, detail="instruction is required")
    try:
        result = rule_runnable.invoke(request.instruction)
    except MissingOpenAiKeyError as exc:
        raise HTTPException(status_code=503, detail=str(exc)) from exc
    except Exception as exc:  # noqa: BLE001
        raise HTTPException(status_code=502, detail=f"AI rule generation failed: {exc}") from exc

    prompt = "Generate a validation rule and a unit-test idea from natural language."
    return RuleResponse(
        model=model_name(),
        generatedAt=utc_now(),
        prompt=prompt,
        suggestedRule=result.suggestedRule,
        testIdea=result.testIdea,
    )