package studying.withsolid.model;

import lombok.Builder;
import studying.withsolid.exception.ApplicationErrorCode;
import studying.withsolid.exception.ApplicationException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Immutable sales report data. The model knows nothing about the file system, email, or console.
 *
 * @param title report title
 * @param date date the report was created for
 * @param time time the report was created at
 * @param carsSold number of the sold cars
 * @param motorcyclesSold number of the sold motorcycles
 */
@Builder
public record Report(
        String title,
        LocalDate date,
        LocalTime time,
        int carsSold,
        int motorcyclesSold
) {
    private static final String SEPARATOR = "--------------------------------";
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");

    /**
     * Creates a report and validates its own invariants.
     *
     * @throws ApplicationException with {@link ApplicationErrorCode#VALIDATION_ERROR}
     *         when the title, date, or time is missing, or a sales figure is negative
     */
    public Report {
        if (title == null || title.isBlank()) {
            throw new ApplicationException(ApplicationErrorCode.VALIDATION_ERROR,
                    "Заголовок отчёта не задан");
        }
        if (date == null) {
            throw new ApplicationException(ApplicationErrorCode.VALIDATION_ERROR,
                    "Дата отчёта не задана");
        }
        if (time == null) {
            throw new ApplicationException(ApplicationErrorCode.VALIDATION_ERROR,
                    "Время отчёта не задано");
        }
        if (carsSold < 0) {
            throw new ApplicationException(ApplicationErrorCode.VALIDATION_ERROR,
                    "Количество проданных автомобилей отрицательное: " + carsSold);
        }
        if (motorcyclesSold < 0) {
            throw new ApplicationException(ApplicationErrorCode.VALIDATION_ERROR,
                    "Количество проданных мотоциклов отрицательное: " + motorcyclesSold);
        }
    }

    /**
     * Returns the report time in the {@code HH:mm:ss} format shared by every report representation.
     *
     * @return formatted report time
     */
    public String formattedTime() {
        return TIME_FORMATTER.format(time);
    }

    /**
     * Builds the text representation of the report.
     *
     * @return report rendered as plain text
     */
    @Override
    public String toString() {
        return """
                %s
                Дата: %s
                Время: %s
                %s
                Продано автомобилей: %d шт.
                Продано мотоциклов: %d шт.
                %s
                """.formatted(title, date, formattedTime(), SEPARATOR, carsSold, motorcyclesSold, SEPARATOR);
    }
}
