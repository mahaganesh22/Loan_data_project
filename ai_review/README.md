# Loan Data AI Review (Python + LangChain)

Advisory copilot used by Spring Boot. It never writes loan records. Suggestions stay in the exception until a reviewer submits a human correction.

This service always calls the OpenAI model. There is no local heuristic fallback.

## API key (required)

1. Copy `.env.example` to `.env` in this folder.
2. Replace `sk-your-openai-api-key-here` with your real OpenAI API key.
3. Restart uvicorn after changing `.env`.

`.env` is gitignored. Do not put the key in source code, the frontend, or Spring properties.

## Run

```bash
cd ai_review
python -m venv .venv
.\.venv\Scripts\activate
pip install -r requirements.txt
copy .env.example .env
uvicorn app:app --host 127.0.0.1 --port 8001
```

Health check: `http://localhost:8001/health` — `openaiConfigured` must be `true`.

Spring proxies reviews through:

- `POST /api/exceptions/{id}/ai-review`
- `PATCH /api/exceptions/{id}/ai-decision`
- `GET /api/ai/summary`
- `POST /api/ai/rules`
