package studying.withsolid.service.impl;

import studying.withsolid.exception.ApplicationErrorCode;
import studying.withsolid.exception.ApplicationException;
import studying.withsolid.model.Report;
import studying.withsolid.service.ReportSender;

/**
 * Imitates the delivery of a report as a JSON payload sent to an HTTP endpoint.
 * Instead of a real HTTP call the request is printed to the console.
 */
public final class JsonReportSender implements ReportSender {
    private static final String DEFAULT_ENDPOINT = "https://reports.example/api/v1/reports";

    private final String endpoint;

    /** Creates a sender addressing the default demonstration endpoint. */
    public JsonReportSender() {
        this(DEFAULT_ENDPOINT);
    }

    /**
     * Creates a sender addressing the selected endpoint.
     *
     * @param endpoint URL that receives the JSON payload
     * @throws ApplicationException with {@link ApplicationErrorCode#VALIDATION_ERROR}
     *         when the endpoint is missing
     */
    public JsonReportSender(String endpoint) {
        if (endpoint == null || endpoint.isBlank()) {
            throw new ApplicationException(ApplicationErrorCode.VALIDATION_ERROR,
                    "Адрес JSON-эндпоинта не задан");
        }
        this.endpoint = endpoint;
    }

    /**
     * Reports the delivery of the report as a JSON request.
     *
     * @param report report to send
     * @param email recipient address placed into the payload
     * @throws ApplicationException with {@link ApplicationErrorCode#VALIDATION_ERROR}
     *         when the report or the email is missing
     */
    @Override
    public void send(Report report, String email) {
        if (report == null) {
            throw new ApplicationException(ApplicationErrorCode.VALIDATION_ERROR,
                    "Отчёт для отправки не задан");
        }
        if (email == null || email.isBlank()) {
            throw new ApplicationException(ApplicationErrorCode.VALIDATION_ERROR,
                    "Email получателя не задан");
        }

        System.out.printf("POST %s%nContent-Type: application/json%n%s%n", endpoint, toJson(report, email));
    }

    /**
     * Serializes the report and its recipient into a JSON object.
     *
     * @param report report to serialize
     * @param email recipient address
     * @return JSON representation of the report
     */
    private String toJson(Report report, String email) {
        return """
                {
                  "recipient": "%s",
                  "title": "%s",
                  "date": "%s",
                  "time": "%s",
                  "carsSold": %d,
                  "motorcyclesSold": %d
                }""".formatted(
                escape(email),
                escape(report.title()),
                report.date(),
                report.formattedTime(),
                report.carsSold(),
                report.motorcyclesSold());
    }

    /**
     * Escapes the characters that are not allowed inside a JSON string literal.
     *
     * @param value raw text
     * @return text safe to embed into JSON
     */
    private String escape(String value) {
        var escaped = new StringBuilder(value.length());

        for (var symbol : value.toCharArray()) {
            switch (symbol) {
                case '"' -> escaped.append("\\\"");
                case '\\' -> escaped.append("\\\\");
                case '\n' -> escaped.append("\\n");
                case '\r' -> escaped.append("\\r");
                case '\t' -> escaped.append("\\t");
                default -> {
                    if (symbol < 0x20) {
                        escaped.append("\\u%04x".formatted((int) symbol));
                    } else {
                        escaped.append(symbol);
                    }
                }
            }
        }

        return escaped.toString();
    }
}
