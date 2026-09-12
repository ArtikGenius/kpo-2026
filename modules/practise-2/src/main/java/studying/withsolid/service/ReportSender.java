package studying.withsolid.service;

import studying.withsolid.model.Report;

/** Contract of delivering a report to a recipient. */
@FunctionalInterface
public interface ReportSender {
    /**
     * Sends the report to the recipient of the implementation channel.
     *
     * @param report report to send
     * @param email recipient address
     */
    void send(Report report, String email);
}
