package studying.service.impl;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import studying.withsolid.exception.ApplicationErrorCode;
import studying.withsolid.exception.ApplicationException;
import studying.withsolid.model.Report;
import studying.withsolid.service.impl.JsonReportSender;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JsonReportSenderTest {
    @Test
    @DisplayName("Отправляет отчёт в формате JSON на указанный эндпоинт")
    void sendPrintsJsonPayload() {
        var message = captureSend(new JsonReportSender("https://example.test/reports"),
                createReport(), "student@hse.ru");

        assertTrue(message.contains("POST https://example.test/reports"));
        assertTrue(message.contains("Content-Type: application/json"));
        assertTrue(message.contains("\"recipient\": \"student@hse.ru\""));
        assertTrue(message.contains("\"title\": \"Продажи\""));
        assertTrue(message.contains("\"date\": \"2026-09-11\""));
        assertTrue(message.contains("\"time\": \"12:00:00\""));
        assertTrue(message.contains("\"carsSold\": 100"));
        assertTrue(message.contains("\"motorcyclesSold\": 50"));
    }

    @Test
    @DisplayName("Экранирует кавычки в JSON-представлении отчёта")
    void sendEscapesQuotesInJson() {
        var report = createReportBuilder().title("Отчёт \"Продажи\"").build();

        var message = captureSend(new JsonReportSender(), report, "student@hse.ru");

        assertTrue(message.contains("\"title\": \"Отчёт \\\"Продажи\\\"\""));
    }

    @Test
    @DisplayName("Выбрасывает прикладную ошибку, если email получателя не задан")
    void sendRejectsMissingEmail() {
        var exception = assertThrows(ApplicationException.class,
                () -> new JsonReportSender().send(createReport(), " "));

        assertEquals(ApplicationErrorCode.VALIDATION_ERROR, exception.getCode());
    }

    @Test
    @DisplayName("Выбрасывает прикладную ошибку, если отчёт не задан")
    void sendRejectsMissingReport() {
        var exception = assertThrows(ApplicationException.class,
                () -> new JsonReportSender().send(null, "student@hse.ru"));

        assertEquals(ApplicationErrorCode.VALIDATION_ERROR, exception.getCode());
    }

    @Test
    @DisplayName("Выбрасывает прикладную ошибку, если адрес эндпоинта не задан")
    void constructorRejectsMissingEndpoint() {
        var exception = assertThrows(ApplicationException.class, () -> new JsonReportSender(null));

        assertEquals(ApplicationErrorCode.VALIDATION_ERROR, exception.getCode());
    }

    private String captureSend(JsonReportSender sender, Report report, String email) {
        var originalOutput = System.out;
        var output = new ByteArrayOutputStream();

        try (var interceptedOutput = new PrintStream(output, true, StandardCharsets.UTF_8)) {
            System.setOut(interceptedOutput);
            sender.send(report, email);
        } finally {
            System.setOut(originalOutput);
        }

        return output.toString(StandardCharsets.UTF_8);
    }

    private Report createReport() {
        return createReportBuilder().build();
    }

    private Report.ReportBuilder createReportBuilder() {
        return Report.builder()
                .title("Продажи")
                .date(LocalDate.of(2026, 9, 11))
                .time(LocalTime.NOON)
                .carsSold(100)
                .motorcyclesSold(50);
    }
}
