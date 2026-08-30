package com.loan.loan_data_project.global_exception.dto;

import java.io.IOException;
import java.time.format.DateTimeFormatter;

import org.springframework.http.MediaType;

import jakarta.servlet.http.HttpServletResponse;

public final class ErrorResponseWriter {

    private ErrorResponseWriter() {
    }

    public static void write(HttpServletResponse response, ErrorResponse error) throws IOException {
        response.setStatus(error.getStatus());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        String timestamp = error.getTimestamp() == null
                ? ""
                : error.getTimestamp().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        String json = "{"
                + "\"timestamp\":" + quote(timestamp)
                + ",\"status\":" + error.getStatus()
                + ",\"error\":" + quote(error.getError())
                + ",\"code\":" + quote(error.getCode())
                + ",\"message\":" + quote(error.getMessage())
                + ",\"path\":" + quote(error.getPath())
                + "}";
        response.getWriter().write(json);
        response.getWriter().flush();
    }

    private static String quote(String value) {
        if (value == null) {
            return "null";
        }
        return "\"" + value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t")
                + "\"";
    }
}
