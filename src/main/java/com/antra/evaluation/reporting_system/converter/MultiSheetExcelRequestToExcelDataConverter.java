package com.antra.evaluation.reporting_system.converter;

import com.antra.evaluation.reporting_system.pojo.api.MultiSheetExcelRequest;
import com.antra.evaluation.reporting_system.pojo.report.ExcelData;
import com.antra.evaluation.reporting_system.pojo.report.ExcelDataHeader;
import com.antra.evaluation.reporting_system.pojo.report.ExcelDataSheet;
import com.antra.evaluation.reporting_system.pojo.report.ExcelDataSheet.ExcelDataRow;
import org.springframework.core.convert.converter.Converter;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class MultiSheetExcelRequestToExcelDataConverter implements
    Converter<MultiSheetExcelRequest, ExcelData>
{

  @Override
  public ExcelData convert(MultiSheetExcelRequest request) {
    List<ExcelDataRow> rows = request.getData().stream()
        .map(ExcelDataRow::new)
        .collect(Collectors.toList());
    List<ExcelDataHeader> headers = request.getHeaders().stream()
        .map(ExcelDataHeader::new)
        .collect(Collectors.toList());
    int splitByColumnIndex = request.getHeaders().indexOf(request.getSplitBy());
    if (splitByColumnIndex == -1) {
      throw new IllegalStateException("Unable to locate the split column in headers");
    }
    List<ExcelDataSheet> sheets = rows.stream().collect(
        Collectors.groupingBy(row -> row.get(splitByColumnIndex)))
        .entrySet().stream().map(sheetNameAndRows -> new ExcelDataSheet(
            sheetNameAndRows.getKey().toString(), headers, sheetNameAndRows.getValue())
        ).collect(Collectors.toList());
    return ExcelData.builder()
        .fileId(UUID.randomUUID())
        .title(request.getDescription())
        .createdAt(LocalDateTime.now())
        .author(request.getSubmitter())
        .sheets(sheets)
        .build();
  }
}
