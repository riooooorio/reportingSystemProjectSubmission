package com.antra.evaluation.reporting_system.pojo.report;


import lombok.AllArgsConstructor;
import lombok.Getter;
import java.util.List;

@Getter
@AllArgsConstructor
public class ExcelDataSheet {
  private String title;
  private List<ExcelDataHeader> headers;
  private List<ExcelDataRow> dataRows;

  @Getter
  @AllArgsConstructor
  public static class ExcelDataRow {
    private List<Object> data;

    public Object get(Integer id) {
      if (id == null || id >= data.size() || id < 0) {
        throw new IllegalArgumentException("Invalid column Id provided");
      }
      return data.get(id);
    }

    public Integer size() {
      return data.size();
    }
  }
}
