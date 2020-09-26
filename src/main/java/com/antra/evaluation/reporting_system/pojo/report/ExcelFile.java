package com.antra.evaluation.reporting_system.pojo.report;

import lombok.Builder;
import lombok.Getter;
import java.nio.file.Path;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Getter
public class ExcelFile {
  private final UUID fileId;
  private final String title;
  private final Long fileSize;
  private final LocalDateTime generatedTime;
  private final Path filePath;
}
