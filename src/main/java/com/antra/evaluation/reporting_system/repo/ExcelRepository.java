package com.antra.evaluation.reporting_system.repo;

import com.antra.evaluation.reporting_system.pojo.report.ExcelFile;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ExcelRepository {
    Optional<ExcelFile> getFileById(UUID id);

    ExcelFile saveFile(ExcelFile file);

    ExcelFile deleteFile(UUID id);

    List<ExcelFile> getFiles();
}
