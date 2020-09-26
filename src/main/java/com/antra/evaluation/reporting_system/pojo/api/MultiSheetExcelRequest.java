package com.antra.evaluation.reporting_system.pojo.api;

import javax.validation.constraints.NotNull;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class MultiSheetExcelRequest extends ExcelRequest{
	@NotNull(message = "Split by column must be provided")
	private String splitBy;

	public String getSplitBy() {
		return splitBy;
	}

	public void setSplitBy(String splitBy) {
		this.splitBy = splitBy;
	}
	
}
