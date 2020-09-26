package com.antra.evaluation.reporting_system.converter;

import com.antra.evaluation.reporting_system.pojo.api.ExcelRequest;
import com.antra.evaluation.reporting_system.pojo.report.ExcelData;
import com.antra.evaluation.reporting_system.pojo.report.ExcelDataHeader;
import com.antra.evaluation.reporting_system.pojo.report.ExcelDataSheet;
import com.antra.evaluation.reporting_system.pojo.report.ExcelDataSheet.ExcelDataRow;
import org.springframework.core.convert.converter.Converter;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.UUID;
import java.util.stream.Collectors;

public class ExcelRequestToExcelDataConverter implements Converter<ExcelRequest, ExcelData> {

  private static final String DEFAULT_SHEET_NAME = "Sheet1";

  @Override
  public ExcelData convert(ExcelRequest request) {
    ExcelDataSheet sheet = new ExcelDataSheet(
        DEFAULT_SHEET_NAME,
        request.getHeaders().stream().map(ExcelDataHeader::new).collect(Collectors.toList()),
        request.getData().stream().map(ExcelDataRow::new).collect(Collectors.toList()));
    return ExcelData.builder()
        .fileId(UUID.randomUUID())
        .title(request.getDescription())
        .createdAt(LocalDateTime.now())
        .author(request.getSubmitter())
        .sheets(Collections.singletonList(sheet))
        .build();
  }
}
