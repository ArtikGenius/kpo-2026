package studying.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import studying.withsolid.exception.ApplicationErrorCode;
import studying.withsolid.exception.ApplicationException;
import studying.withsolid.model.Report;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ReportTest {
    @Test
    @DisplayName("Формирует текстовое представление с показателями продаж")
    void toStringContainsSalesFigures() {
        var text = createReportBuilder().build().toString();

        assertTrue(text.startsWith("Продажи"));
        assertTrue(text.contains("Дата: 2026-09-11"));
        assertTrue(text.contains("Время: 12:00:00"));
        assertTrue(text.contains("Продано автомобилей: 100 шт."));
        assertTrue(text.contains("Продано мотоциклов: 50 шт."));
    }

    @Test
    @DisplayName("Отклоняет отрицательные показатели продаж")
    void rejectsNegativeSalesFigures() {
        var builder = createReportBuilder().carsSold(-1);

        var exception = assertThrows(ApplicationException.class, builder::build);

        assertEquals(ApplicationErrorCode.VALIDATION_ERROR, exception.getCode());
    }

    @Test
    @DisplayName("Отклоняет пустой заголовок отчёта")
    void rejectsBlankTitle() {
        var builder = createReportBuilder().title(" ");

        var exception = assertThrows(ApplicationException.class, builder::build);

        assertEquals(ApplicationErrorCode.VALIDATION_ERROR, exception.getCode());
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
