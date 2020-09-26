package com.antra.evaluation.reporting_system.pojo.api;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ErrorResponse {
  private String msg;
  private String statusCode;
}