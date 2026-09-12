package studying.withsolid.service.impl;

import studying.withsolid.exception.ApplicationErrorCode;
import studying.withsolid.exception.ApplicationException;
import studying.withsolid.model.Report;
import studying.withsolid.service.ReportSender;

/**
 * Imitates the delivery of a report through a SOAP web service.
 * Instead of a real call the request with its XML envelope is printed to the console.
 */
public final class SoapReportSender implements ReportSender {
    private static final String DEFAULT_ENDPOINT = "https://reports.example/soap/v1";
    private static final String SOAP_ACTION = "urn:reports:SendReport";

    private final String endpoint;

    /** Creates a sender addressing the default demonstration endpoint. */
    public SoapReportSender() {
        this(DEFAULT_ENDPOINT);
    }

    /**
     * Creates a sender addressing the selected endpoint.
     *
     * @param endpoint URL of the SOAP service
     * @throws ApplicationException with {@link ApplicationErrorCode#VALIDATION_ERROR}
     *         when the endpoint is missing
     */
    public SoapReportSender(String endpoint) {
        if (endpoint == null || endpoint.isBlank()) {
            throw new ApplicationException(ApplicationErrorCode.VALIDATION_ERROR,
                    "Адрес SOAP-сервиса не задан");
        }
        this.endpoint = endpoint;
    }

    /**
     * Reports the delivery of the report as a SOAP request.
     *
     * @param report report to send
     * @param email recipient address placed into the envelope
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

        System.out.printf("POST %s%nContent-Type: text/xml; charset=utf-8%nSOAPAction: \"%s\"%n%s%n",
                endpoint, SOAP_ACTION, toEnvelope(report, email));
    }

    /**
     * Builds the SOAP envelope carrying the report.
     *
     * @param report report to serialize
     * @param email recipient address
     * @return XML envelope of the SOAP request
     */
    private String toEnvelope(Report report, String email) {
        return """
                <?xml version="1.0" encoding="UTF-8"?>
                <soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
                  <soap:Body>
                    <rep:SendReport xmlns:rep="urn:reports">
                      <rep:Recipient>%s</rep:Recipient>
                      <rep:Title>%s</rep:Title>
                      <rep:Date>%s</rep:Date>
                      <rep:Time>%s</rep:Time>
                      <rep:CarsSold>%d</rep:CarsSold>
                      <rep:MotorcyclesSold>%d</rep:MotorcyclesSold>
                    </rep:SendReport>
                  </soap:Body>
                </soap:Envelope>""".formatted(
                escape(email),
                escape(report.title()),
                report.date(),
                report.formattedTime(),
                report.carsSold(),
                report.motorcyclesSold());
    }

    /**
     * Escapes the characters that have a special meaning inside XML text.
     *
     * @param value raw text
     * @return text safe to embed into XML
     */
    private String escape(String value) {
        return value.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");
    }
}
