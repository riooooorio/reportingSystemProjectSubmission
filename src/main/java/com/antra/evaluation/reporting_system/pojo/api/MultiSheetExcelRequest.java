package com.antra.evaluation.reporting_system.pojo.api;

import org.springframework.lang.NonNull;

public class MultiSheetExcelRequest extends ExcelRequest{
	@NonNull
	private String splitBy;

	public String getSplitBy() {
		return splitBy;
	}

	public void setSplitBy(String splitBy) {
		this.splitBy = splitBy;
	}
	
}
