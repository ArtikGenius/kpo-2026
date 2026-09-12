package studying.withsolid.service;

import studying.withsolid.model.Report;

/** Contract of saving a report. */
@FunctionalInterface
public interface ReportSaver {
    /**
     * Saves the report in the storage of the implementation.
     *
     * @param report report to save
     */
    void save(Report report);
}
