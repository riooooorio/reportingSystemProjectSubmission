package com.antra.evaluation.reporting_system.service;

import java.io.IOException;

import com.antra.evaluation.reporting_system.pojo.api.ExcelResponse;
import com.antra.evaluation.reporting_system.pojo.report.ExcelData;

public interface ExcelGenerationService {
    
	ExcelResponse generateExcelReport(ExcelData data) throws IOException;
}
