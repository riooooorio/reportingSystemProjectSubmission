package com.antra.evaluation.reporting_system.service;

import com.antra.evaluation.reporting_system.pojo.api.ExcelRequest;
import com.antra.evaluation.reporting_system.pojo.api.ExcelResponse;
import com.antra.evaluation.reporting_system.pojo.api.MultiSheetExcelRequest;
import com.antra.evaluation.reporting_system.pojo.report.ExcelFile;
import java.util.List;
import java.util.UUID;

public interface ExcelService {

  ExcelResponse createExcel(ExcelRequest request);

  ExcelResponse createMultiSheetExcel(MultiSheetExcelRequest request);

  ExcelResponse deleteExcel(UUID id);

  ExcelFile getExcelFileById(UUID id);

  List<ExcelResponse> findAll();
}
