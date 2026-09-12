package studying.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import studying.withsolid.model.Report;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import studying.withsolid.service.ReportSaver;
import studying.withsolid.service.ReportSender;
import studying.withsolid.service.ReportService;
import studying.withsolid.service.impl.EmailReportSender;
import studying.withsolid.service.impl.JsonReportSender;
import studying.withsolid.service.impl.SoapReportSender;
import studying.withsolid.service.impl.TextReportSaver;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ReportServiceTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    @DisplayName("Обрабатывает отчёт: сначала сохраняет, затем отправляет")
    void processSavesAndThenSendsTheSameReport() {
        var calls = new ArrayList<String>();
        ReportSaver saver = report -> calls.add("save:" + report.title());
        ReportSender sender = (report, email) -> calls.add("send:" + report.title() + ":" + email);
        var report = createReport();

        new ReportService(saver, sender).process(report, "student@hse.ru");

        assertEquals(List.of("save:Продажи", "send:Продажи:student@hse.ru"), calls);
    }

    @Test
    @DisplayName("Работает с любым каналом доставки без изменения самого сервиса")
    void processAcceptsAnyReportSenderImplementation() {
        var report = createReport();
        List<ReportSender> senders = List.of(
                new EmailReportSender(),
                new JsonReportSender(),
                new SoapReportSender());
        var saver = new TextReportSaver(temporaryDirectory);
        var originalOutput = System.out;
        var output = new ByteArrayOutputStream();

        try (var interceptedOutput = new PrintStream(output, true, StandardCharsets.UTF_8)) {
            System.setOut(interceptedOutput);
            senders.forEach(sender -> new ReportService(saver, sender).process(report, "student@hse.ru"));
        } finally {
            System.setOut(originalOutput);
        }

        var message = output.toString(StandardCharsets.UTF_8);
        assertTrue(message.contains("отправлен на email: student@hse.ru"));
        assertTrue(message.contains("Content-Type: application/json"));
        assertTrue(message.contains("<soap:Envelope"));
    }

    private Report createReport() {
        return Report.builder()
                .title("Продажи")
                .date(LocalDate.of(2026, 9, 11))
                .time(LocalTime.NOON)
                .carsSold(100)
                .motorcyclesSold(50)
                .build();
    }
}
