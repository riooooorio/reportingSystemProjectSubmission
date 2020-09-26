package com.antra.evaluation.reporting_system.pojo.report;

import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Builder
@Getter
public class ExcelData {
  private UUID fileId;
  private String title;
  private LocalDateTime createdAt;
  private List<ExcelDataSheet> sheets;
  private String author;
}
