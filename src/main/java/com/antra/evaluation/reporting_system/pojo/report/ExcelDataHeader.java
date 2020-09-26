package com.antra.evaluation.reporting_system.pojo.report;


import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ExcelDataHeader {
	private static final int DEFAULT_WIDTH = 5;

	private String name;
	private ExcelDataType type;
	private int width;

	public ExcelDataHeader(String name) {
		this(name, ExcelDataType.STRING, DEFAULT_WIDTH);
	}
}
