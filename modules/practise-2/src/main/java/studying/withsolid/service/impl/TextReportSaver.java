package studying.withsolid.service.impl;

import studying.withsolid.exception.ApplicationErrorCode;
import studying.withsolid.exception.ApplicationException;
import studying.withsolid.model.Report;
import studying.withsolid.service.ReportSaver;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.format.DateTimeFormatter;

/** Saves the text representation of a report to a file named after its date and time. */
public final class TextReportSaver implements ReportSaver {
    private static final Path DEFAULT_DIRECTORY = Path.of("reports");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH-mm-ss");

    private final Path directory;

    /** Creates a saver writing to the default {@code reports} directory. */
    public TextReportSaver() {
        this(DEFAULT_DIRECTORY);
    }

    /**
     * Creates a saver writing to the selected directory.
     *
     * @param directory directory for the generated report files
     * @throws ApplicationException with {@link ApplicationErrorCode#VALIDATION_ERROR}
     *         when the directory is missing
     */
    public TextReportSaver(Path directory) {
        if (directory == null) {
            throw new ApplicationException(ApplicationErrorCode.VALIDATION_ERROR,
                    "Каталог для отчётов не задан");
        }
        this.directory = directory;
    }

    /**
     * Writes the report to {@code <directory>/report-<date>-<time>.txt}.
     *
     * @param report report to save
     * @throws ApplicationException with {@link ApplicationErrorCode#VALIDATION_ERROR}
     *         when the report is missing, or with {@link ApplicationErrorCode#FILE_WRITE_ERROR}
     *         and the original {@link IOException} as the cause when the file cannot be written
     */
    @Override
    public void save(Report report) {
        if (report == null) {
            throw new ApplicationException(ApplicationErrorCode.VALIDATION_ERROR,
                    "Отчёт для сохранения не задан");
        }

        var file = directory.resolve("report-%s-%s.txt".formatted(
                DATE_FORMATTER.format(report.date()),
                TIME_FORMATTER.format(report.time())));

        try {
            Files.createDirectories(directory);
            Files.writeString(file, report.toString());
        } catch (IOException exception) {
            throw new ApplicationException(ApplicationErrorCode.FILE_WRITE_ERROR,
                    "Не удалось сохранить отчёт в " + file, exception);
        }
    }
}
