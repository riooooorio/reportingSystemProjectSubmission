package com.antra.evaluation.reporting_system.pojo.report;

import lombok.Builder;
import lombok.Getter;
import java.nio.file.Path;
import java.time.Instant;

@Builder
@Getter
public class ExcelFile {
  private final String fileId;
  private final String fileName;
  private final Long fileSize;
  private final Instant generatedTime;
  private final Path filePath;
}
