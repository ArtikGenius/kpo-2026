package studying.service.impl;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import studying.withsolid.exception.ApplicationErrorCode;
import studying.withsolid.exception.ApplicationException;
import studying.withsolid.model.Report;
import studying.withsolid.service.impl.SoapReportSender;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalTime;

import javax.xml.parsers.DocumentBuilderFactory;

import org.xml.sax.InputSource;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SoapReportSenderTest {
    @Test
    @DisplayName("Отправляет отчёт SOAP-запросом с XML-конвертом")
    void sendPrintsSoapEnvelope() {
        var message = captureSend(new SoapReportSender("https://example.test/soap"),
                createReport(), "student@hse.ru");

        assertTrue(message.contains("POST https://example.test/soap"));
        assertTrue(message.contains("Content-Type: text/xml; charset=utf-8"));
        assertTrue(message.contains("SOAPAction: \"urn:reports:SendReport\""));
        assertTrue(message.contains("<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">"));
        assertTrue(message.contains("<rep:Recipient>student@hse.ru</rep:Recipient>"));
        assertTrue(message.contains("<rep:Title>Продажи</rep:Title>"));
        assertTrue(message.contains("<rep:Date>2026-09-11</rep:Date>"));
        assertTrue(message.contains("<rep:Time>12:00:00</rep:Time>"));
        assertTrue(message.contains("<rep:CarsSold>100</rep:CarsSold>"));
        assertTrue(message.contains("<rep:MotorcyclesSold>50</rep:MotorcyclesSold>"));
    }

    @Test
    @DisplayName("Формирует корректный XML даже со спецсимволами в заголовке")
    void sendEscapesSpecialCharacters() {
        var report = createReportBuilder().title("Продажи <авто> & \"мото\"").build();

        var message = captureSend(new SoapReportSender(), report, "student@hse.ru");
        var envelope = message.substring(message.indexOf("<?xml"));

        assertTrue(message.contains("<rep:Title>Продажи &lt;авто&gt; &amp; &quot;мото&quot;</rep:Title>"));
        assertDoesNotThrow(() -> DocumentBuilderFactory.newInstance()
                .newDocumentBuilder()
                .parse(new InputSource(new StringReader(envelope))));
    }

    @Test
    @DisplayName("Выбрасывает прикладную ошибку, если email получателя не задан")
    void sendRejectsMissingEmail() {
        var exception = assertThrows(ApplicationException.class,
                () -> new SoapReportSender().send(createReport(), null));

        assertEquals(ApplicationErrorCode.VALIDATION_ERROR, exception.getCode());
    }

    @Test
    @DisplayName("Выбрасывает прикладную ошибку, если отчёт не задан")
    void sendRejectsMissingReport() {
        var exception = assertThrows(ApplicationException.class,
                () -> new SoapReportSender().send(null, "student@hse.ru"));

        assertEquals(ApplicationErrorCode.VALIDATION_ERROR, exception.getCode());
    }

    @Test
    @DisplayName("Выбрасывает прикладную ошибку, если адрес сервиса не задан")
    void constructorRejectsMissingEndpoint() {
        var exception = assertThrows(ApplicationException.class, () -> new SoapReportSender(" "));

        assertEquals(ApplicationErrorCode.VALIDATION_ERROR, exception.getCode());
    }

    private String captureSend(SoapReportSender sender, Report report, String email) {
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
