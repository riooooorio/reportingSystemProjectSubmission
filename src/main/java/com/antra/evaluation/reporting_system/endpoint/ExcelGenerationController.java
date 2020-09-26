package com.antra.evaluation.reporting_system.endpoint;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.antra.evaluation.reporting_system.pojo.api.ExcelRequest;
import com.antra.evaluation.reporting_system.pojo.api.ExcelResponse;
import com.antra.evaluation.reporting_system.pojo.api.MultiSheetExcelRequest;
import com.antra.evaluation.reporting_system.pojo.report.ExcelData;
import com.antra.evaluation.reporting_system.pojo.report.ExcelDataHeader;
import com.antra.evaluation.reporting_system.pojo.report.ExcelDataSheet;
import com.antra.evaluation.reporting_system.pojo.report.ExcelFile;
import com.antra.evaluation.reporting_system.service.ExcelService;

import io.swagger.annotations.ApiOperation;

@RestController
public class ExcelGenerationController {

	private static final Logger log = LoggerFactory.getLogger(ExcelGenerationController.class);

	private ExcelService excelService;

	@Autowired
	public ExcelGenerationController(ExcelService excelService) {
		this.excelService = excelService;
	}

	@PostMapping("/excel")
	@ApiOperation("Generate Excel")
	public ResponseEntity<ExcelResponse> createExcel(@RequestBody @Validated ExcelRequest request) {
		ExcelResponse response = null;
		List<ExcelDataSheet> sheets = null;
		ExcelData data = null;
		ExcelDataHeader hdr = null;
		List<ExcelDataHeader> hdrList = null;
		try {
			log.info("In the Create Excel Method Start.....");
			sheets = new ArrayList<>();
			data = new ExcelData();
			data.setTitle(request.getDescription());
			data.setGeneratedTime(LocalDateTime.now());
			data.setSubmitter(request.getSubmitter());
			ExcelDataSheet sheet = new ExcelDataSheet();
			sheet.setTitle(request.getDescription());

			hdrList = new ArrayList<>();
			for (String str : request.getHeaders()) {
				hdr = new ExcelDataHeader();
				hdr.setName(str);
				hdrList.add(hdr);
			}

			sheet.setHeaders(hdrList);
			sheet.setDataRows(request.getData());
			sheets.add(sheet);
			data.setSheets(sheets);

			response = excelService.createExcel(data);
			log.info("In the Create Excel Method END.....");
		} catch (Exception e) {
			log.error("Error in Create Excel Method...." + e.getMessage());
		}
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@PostMapping("/excel/auto")
	@ApiOperation("Generate Multi-Sheet Excel Using Split field")
	public ResponseEntity<ExcelResponse> createMultiSheetExcel(@RequestBody @Validated MultiSheetExcelRequest request) {
		List<ExcelDataSheet> sheets = null;
		ExcelData data = null;
		ExcelDataHeader hdr = null;
		ExcelResponse response = null;
		List<ExcelDataHeader> hdrList = null;
		try {
			log.info("In the createMultiSheetExcel Method Start...");
			sheets = new ArrayList<>();
			data = new ExcelData();
			data.setTitle(request.getDescription());
			data.setGeneratedTime(LocalDateTime.now());
			data.setSubmitter(request.getSubmitter());

			if (!StringUtils.isEmpty(request.getSplitBy())) {
				int index = 0;
				hdrList = new ArrayList<>();
				for (String str : request.getHeaders()) {
					hdr = new ExcelDataHeader();
					hdr.setName(str);
					hdrList.add(hdr);
				}
				for (ExcelDataHeader h : hdrList) {

					if (h.getName().equalsIgnoreCase(request.getSplitBy().trim())) {
						break;
					}
					index++;
				}
				Map<Object, List<List<Object>>> map = new HashMap<Object, List<List<Object>>>();

				for (List<Object> list : request.getData()) {
					if (!map.containsKey(list.get(index))) {
						List<List<Object>> l1 = new ArrayList<>();
						l1.add(list);
						map.put(list.get(index), l1);
					} else {
						map.get(list.get(index)).add(list);
					}

				}
				for (Entry<Object, List<List<Object>>> entry : map.entrySet()) {
					ExcelDataSheet sheet = new ExcelDataSheet();
					sheet.setTitle(entry.getKey().toString());
					sheet.setHeaders(hdrList);
					sheet.setDataRows(entry.getValue());
					sheets.add(sheet);

				}
				data.setSheets(sheets);

				response = excelService.createExcel(data);
				log.info("In the  createMultiSheetExcel Method End.....");
			}

		} catch (Exception e) {
			log.error("Error in createMultiSheetExcel Method...." + e.getMessage());
		}
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@GetMapping("/excel")
	@ApiOperation("List all existing files")
	public ResponseEntity<List<ExcelResponse>> listExcels() {
		log.info("In the listExcels Method Start.....");
		ArrayList<ExcelResponse> response = new ArrayList<ExcelResponse>();
		response = (ArrayList<ExcelResponse>) excelService.findAll();
		log.info("In the listExcels Method End.....");
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@GetMapping("/excel/{id}/content")
	@ApiOperation("Download Excel")
	public void downloadExcel(@PathVariable @NonNull  String id, HttpServletResponse response) throws IOException {
		log.info("In the downloadExcel Method Start.....");
		InputStream fis = excelService.getExcelBodyById(id);
		ExcelFile file = excelService.getExcelFileById(id);
		String fileName = file.getFileName();
		response.setHeader("Content-Type", "application/vnd.ms-excel");
		response.setHeader("Content-Disposition", "attachment; filename="+fileName); 
		FileCopyUtils.copy(fis, response.getOutputStream());
		log.info("In the downloadExcel Method End.....");
	}


	@DeleteMapping(value="/excel/{id}")
	@ApiOperation("Delete Excel File")
	public ResponseEntity<ExcelResponse> deleteExcel(@PathVariable("id") String id) {
		log.info("In the deleteExcel Method Start.....");
		ExcelResponse response = excelService.deleteExcel(id);
		log.info("In the deleteExcel Method End.....");
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
}

