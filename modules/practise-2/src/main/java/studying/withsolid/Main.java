package studying.withsolid;

import studying.withsolid.model.Report;
import studying.withsolid.service.ReportSender;
import studying.withsolid.service.ReportService;
import studying.withsolid.service.impl.EmailReportSender;
import studying.withsolid.service.impl.JsonReportSender;
import studying.withsolid.service.impl.SoapReportSender;
import studying.withsolid.service.impl.TextReportSaver;

import java.time.LocalDateTime;
import java.util.List;

/** Demonstrates the implementation following the SOLID principles. */
public class Main {
    /**
     * Runs the report creation, persistence, and delivery demonstration.
     * Every delivery channel is plugged into the same unchanged {@link ReportService}.
     */
    static void main() {
        var now = LocalDateTime.now();
        var report = Report.builder()
                .title("Отчёт")
                .date(now.toLocalDate())
                .time(now.toLocalTime())
                .carsSold(100)
                .motorcyclesSold(50)
                .build();

        var reportSaver = new TextReportSaver();
        List<ReportSender> reportSenders = List.of(
                new EmailReportSender(),
                new JsonReportSender(),
                new SoapReportSender()
        );

        for (var reportSender : reportSenders) {
            new ReportService(reportSaver, reportSender).process(report, "example@example.com");
        }
    }
}
