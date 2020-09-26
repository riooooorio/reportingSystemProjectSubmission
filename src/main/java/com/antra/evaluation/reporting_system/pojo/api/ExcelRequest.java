package com.antra.evaluation.reporting_system.pojo.api;

import org.springframework.lang.NonNull;
import java.util.List;


public class ExcelRequest {
		@NonNull
    private List<String> headers;

    @NonNull
    private String description;

    @NonNull
    private List<List<Object>> data;

    @NonNull
    private String submitter;
    
	public List<String> getHeaders() {
		return headers;
	}
	public void setHeaders(List<String> headers) {
		this.headers = headers;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	
	public List<List<Object>> getData() {
		return data;
	}
	public void setData(List<List<Object>> data) {
		this.data = data;
	}
	public String getSubmitter() {
		return submitter;
	}
	public void setSubmitter(String submitter) {
		this.submitter = submitter;
	}

    
}
