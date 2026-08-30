# Loan Data Verification Copilot — Frontend

React console for the Spring Boot loan verification API.

## Run locally

Start the backend on `http://localhost:8080`, then:

```bash
cd frontend
npm install
npm run dev
```

The Vite dev server runs on `http://localhost:5173` and proxies `/api` to the backend.

For the reviewer AI panel, also start the LangChain service on port `8001` (`ai_review/README.md`).

## Roles

Sign in with users already stored in the backend `users` table:

- **DATA_OPERATOR** — CSV upload and import summary
- **REVIEWER** — exception queue, AI assistant panel, corrections, verify loan
- **DATA_CONSUMER** — verified records, audit trail, CSV export

## Environment

Optional: set `VITE_API_URL=http://localhost:8080` if you are not using the Vite proxy.
