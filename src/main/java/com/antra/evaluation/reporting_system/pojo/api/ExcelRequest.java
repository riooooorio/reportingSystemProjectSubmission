package com.antra.evaluation.reporting_system.pojo.api;

import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
public class ExcelRequest {
  @NotNull(message = "Headers field must be specified")
  private List<String> headers;

  @NotNull(message = "Description must be provided")
  private String description;

  @NotNull(message = "Data rows must be provided")
  private List<List<Object>> data;

  @NotNull(message = "Submitter name must be provided")
  private String submitter;
}
