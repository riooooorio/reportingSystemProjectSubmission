package com.antra.evaluation.reporting_system.pojo.api;

import com.antra.evaluation.reporting_system.pojo.report.ExcelFile;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import java.time.Instant;

@Data
@Builder
@AllArgsConstructor
public class ExcelResponse {
  private String fileId;
  private String fileName;
  private Long fileSize;
  private Instant generatedTime;

  public static ExcelResponse from(ExcelFile excelFile) {
    return ExcelResponse.builder()
        .fileId(excelFile.getFileId())
        .fileName(excelFile.getFileName())
        .fileSize(excelFile.getFileSize())
        .generatedTime(excelFile.getGeneratedTime())
        .build();
  }
}
