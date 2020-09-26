package com.antra.evaluation.reporting_system.pojo.api;

import com.antra.evaluation.reporting_system.pojo.report.ExcelFile;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExcelResponse {
  private UUID fileId;
  private Long fileSize;
  private LocalDateTime generatedTime;

  public static ExcelResponse from(ExcelFile excelFile) {
    return ExcelResponse.builder()
        .fileId(excelFile.getFileId())
        .fileSize(excelFile.getFileSize())
        .generatedTime(excelFile.getGeneratedTime())
        .build();
  }
}
