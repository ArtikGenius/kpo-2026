package studying.withsolid.service.impl;

import studying.withsolid.exception.ApplicationErrorCode;
import studying.withsolid.exception.ApplicationException;
import studying.withsolid.model.Report;
import studying.withsolid.service.ReportSender;

/** Imitates the delivery of a report by email: prints the title and the recipient to the console. */
public final class EmailReportSender implements ReportSender {
    /**
     * Reports the delivery of the report to the recipient.
     *
     * @param report report to send
     * @param email recipient email address
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

        System.out.printf("Отчёт \"%s\" отправлен на email: %s%n", report.title(), email);
    }
}
