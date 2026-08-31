import json
import os

from dotenv import load_dotenv
from langchain_core.prompts import ChatPromptTemplate
from langchain_core.runnables import RunnableLambda
from langchain_openai import ChatOpenAI
from langchain_google_genai import ChatGoogleGenerativeAI

from models import (
    ExceptionPayload,
    StructuredReview,
    StructuredRule,
    StructuredSummary,
    SummaryItem,
)

load_dotenv()


class MissingOpenAiKeyError(RuntimeError):
    """Raised when the OpenAI API key is not configured."""


REVIEW_PROMPT = ChatPromptTemplate.from_messages(
    [
        (
            "system",
            "You are a loan-tape data-quality copilot. Explain validation exceptions, "
            "suggest a likely correction, classify severity (LOW, MEDIUM, HIGH, CRITICAL), "
            "compare conflicting fields, and draft a reviewer note. "
            "Never invent borrower identity data. If unsure, leave suggestedCorrection empty. "
            "AI must not silently change data; recommendations are advisory only.",
        ),
        (
            "human",
            "Prompt: {prompt}\n\nException JSON:\n{exception_json}\n\nLoan snapshot JSON:\n{loan_json}",
        ),
    ]
)


SUMMARY_PROMPT = ChatPromptTemplate.from_messages(
    [
        (
            "system",
            "Summarize a batch of loan validation exceptions for a human reviewer. "
            "Call out volume by severity and the riskiest types. Do not recommend auto-fixes.",
        ),
        ("human", "Prompt: {prompt}\n\nExceptions JSON:\n{exceptions_json}"),
    ]
)


RULE_PROMPT = ChatPromptTemplate.from_messages(
    [
        (
            "system",
            "Turn a natural-language data-quality instruction into a validation rule and a test idea "
            "for a loan tape verification engine. Keep it implementable in Java.",
        ),
        ("human", "Prompt: {prompt}\n\nInstruction:\n{instruction}"),
    ]
)


def openai_api_key() -> str:
    api_key = os.getenv("GEMINI_API_KEY", "").strip()
    if not api_key:
        raise MissingOpenAiKeyError(
            "AI MODEL API_KEY is not set. Copy ai_review/.env.example to ai_review/.env "
            "and paste your OpenAI API key there, then restart the AI service."
        )
    return api_key


def openai_configured() -> bool:
    return bool(os.getenv("GEMINI_API_KEY", "").strip())


def model_name() -> str:
    return os.getenv("GEMINI_MODEL", "gemini-3-flash-preview")


def _llm() -> ChatGoogleGenerativeAI:
    return ChatGoogleGenerativeAI(
        model=model_name(),
        temperature=0,
        api_key=openai_api_key(),
    )


def build_review_prompt(exception: ExceptionPayload) -> str:
    return (
        f"Explain exception {exception.id} of type {exception.exceptionType} "
        f"on field {exception.fieldName}."
    )


def review_exception(exception: ExceptionPayload, loan: dict) -> StructuredReview:

    chain = REVIEW_PROMPT | _llm().with_structured_output(StructuredReview)
    return chain.invoke(
        {
            "prompt": build_review_prompt(exception),
            "exception_json": exception.model_dump_json(),
            "loan_json": json.dumps(loan, default=str),
        }
    )


def summarize_exceptions(items: list[SummaryItem]) -> StructuredSummary:
    
    chain = SUMMARY_PROMPT | _llm().with_structured_output(StructuredSummary)
    return chain.invoke(
        {
            "prompt": f"Summarize {len(items)} loan validation exceptions for the reviewer queue.",
            "exceptions_json": json.dumps([item.model_dump() for item in items], default=str),
        }
    )


def generate_rule(instruction: str) -> StructuredRule:
    
    chain = RULE_PROMPT | _llm().with_structured_output(StructuredRule)
    return chain.invoke(
        {
            "prompt": "Generate a validation rule and a unit-test idea from natural language.",
            "instruction": instruction,
        }
    )


review_runnable = RunnableLambda(lambda payload: review_exception(payload["exception"], payload["loan"]))
summary_runnable = RunnableLambda(lambda items: summarize_exceptions(items))
rule_runnable = RunnableLambda(generate_rule)