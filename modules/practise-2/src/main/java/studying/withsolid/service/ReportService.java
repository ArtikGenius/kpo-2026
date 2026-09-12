package studying.withsolid.service;

import lombok.RequiredArgsConstructor;
import studying.withsolid.model.Report;

/**
 * Main scenario of the report processing. Depends only on the {@link ReportSaver}
 * and {@link ReportSender} contracts, so new formats and channels are added without changing it.
 */
@RequiredArgsConstructor
public final class ReportService {
    private final ReportSaver reportSaver;
    private final ReportSender reportSender;

    /**
     * Saves the report and then sends it to the recipient.
     *
     * @param report report to process
     * @param email recipient address
     */
    public void process(Report report, String email) {
        reportSaver.save(report);
        reportSender.send(report, email);
    }
}
