package com.antra.evaluation.reporting_system;

import com.antra.evaluation.reporting_system.pojo.report.ExcelData;
import com.antra.evaluation.reporting_system.pojo.report.ExcelDataHeader;
import com.antra.evaluation.reporting_system.pojo.report.ExcelDataSheet;
import com.antra.evaluation.reporting_system.pojo.report.ExcelDataSheet.ExcelDataRow;
import com.antra.evaluation.reporting_system.pojo.report.ExcelDataType;
import com.antra.evaluation.reporting_system.service.ExcelGenerationService;
import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ReportingSystemApplicationTests {

  @Autowired
  ExcelGenerationService reportService;

  ExcelData data;

  @BeforeEach // We are using JUnit 5, @Before is replaced by @BeforeEach
  public void setUpData() {

    List<ExcelDataSheet> sheets = new ArrayList<>();

    List<ExcelDataHeader> headersS1 = new ArrayList<>();
    headersS1.add(new ExcelDataHeader("NameTest"));
    headersS1.add(new ExcelDataHeader("Age", ExcelDataType.NUMBER, 5));

    List<ExcelDataRow> dataRows = new ArrayList<>();

    List<Object> row1 = new ArrayList<>();
    row1.add("Dawei");
    row1.add(12);
    List<Object> row2 = new ArrayList<>();
    row2.add("Dawei2");
    row2.add(23);
    dataRows.add(new ExcelDataRow(row1));
    dataRows.add(new ExcelDataRow(row2));

    sheets.add(new ExcelDataSheet("First Sheet", headersS1, dataRows));
    sheets.add(new ExcelDataSheet("second Sheet", headersS1, dataRows));

    this.data = ExcelData.builder()
        .title("Test book")
        .fileId(UUID.randomUUID())
        .createdAt(LocalDateTime.now())
        .sheets(sheets)
        .build();
  }

  @Test
  public void testExcelGegeration() {
    File file = null;
    try {
      file = reportService.generateExcelReport(data);
    } catch (IOException e) {
      e.printStackTrace();
    }
    assertTrue(file != null);
  }
}
