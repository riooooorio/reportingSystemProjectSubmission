package com.antra.evaluation.reporting_system.service;

import java.io.InputStream;
import java.util.List;

import com.antra.evaluation.reporting_system.pojo.api.ExcelResponse;
import com.antra.evaluation.reporting_system.pojo.report.ExcelData;
import com.antra.evaluation.reporting_system.pojo.report.ExcelFile;

public interface ExcelService {

  InputStream getExcelBodyById(String id);

  ExcelResponse createExcel(ExcelData data);

  ExcelResponse deleteExcel(String id);

  ExcelFile getExcelFileById(String id);

  List<ExcelResponse> findAll();
}
