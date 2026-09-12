package studying.service.impl;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import studying.withsolid.exception.ApplicationErrorCode;
import studying.withsolid.exception.ApplicationException;
import studying.withsolid.model.Report;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalTime;
import studying.withsolid.service.impl.TextReportSaver;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TextReportSaverTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    @DisplayName("Сохраняет текстовое представление отчёта в динамически сформированный файл")
    void saveWritesReportToFile() throws Exception {
        var report = createReport();
        var file = Path.of("reports", "report-2026-09-11-12-00-00.txt");

        try {
            new TextReportSaver().save(report);

            assertEquals(report.toString(), Files.readString(file));
        } finally {
            Files.deleteIfExists(file);
        }
    }

    @Test
    @DisplayName("Выбрасывает прикладную ошибку, если отчёт не задан")
    void saveRejectsMissingReport() {
        var exception = assertThrows(ApplicationException.class,
                () -> new TextReportSaver().save(null));

        assertEquals(ApplicationErrorCode.VALIDATION_ERROR, exception.getCode());
    }

    @Test
    @DisplayName("Сохраняет отчёт в переданный каталог")
    void saveWritesReportToGivenDirectory() throws Exception {
        var report = createReport();

        new TextReportSaver(temporaryDirectory).save(report);

        var file = temporaryDirectory.resolve("report-2026-09-11-12-00-00.txt");
        assertEquals(report.toString(), Files.readString(file));
    }

    @Test
    @DisplayName("Сбой записи файла даёт код FILE_WRITE_ERROR и сохраняет исходную причину")
    void saveWrapsFileWriteFailure() throws Exception {
        var occupiedPath = temporaryDirectory.resolve("reports");
        Files.writeString(occupiedPath, "не каталог, а файл");

        var exception = assertThrows(ApplicationException.class,
                () -> new TextReportSaver(occupiedPath).save(createReport()));

        assertEquals(ApplicationErrorCode.FILE_WRITE_ERROR, exception.getCode());
        assertInstanceOf(IOException.class, exception.getCause());
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
