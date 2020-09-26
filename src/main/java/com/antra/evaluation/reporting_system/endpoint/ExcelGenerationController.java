package com.antra.evaluation.reporting_system.endpoint;

import com.antra.evaluation.reporting_system.exception.ExcelFileNotFoundException;
import com.antra.evaluation.reporting_system.pojo.api.ErrorResponse;
import com.antra.evaluation.reporting_system.pojo.api.ExcelRequest;
import com.antra.evaluation.reporting_system.pojo.api.ExcelResponse;
import com.antra.evaluation.reporting_system.pojo.api.MultiSheetExcelRequest;
import com.antra.evaluation.reporting_system.pojo.report.ExcelFile;
import com.antra.evaluation.reporting_system.service.ExcelService;
import io.swagger.annotations.ApiOperation;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileCopyUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;


@Validated
@RestController
public class ExcelGenerationController {

	private ExcelService excelService;

	@Autowired
	public ExcelGenerationController(ExcelService excelService) {
		this.excelService = excelService;
	}

	@PostMapping("/excel")
	@ApiOperation("Generate Excel")
	public ResponseEntity<ExcelResponse> createExcel(@Valid @RequestBody ExcelRequest request) {
		return new ResponseEntity<>(excelService.createExcel(request), HttpStatus.OK);
	}

	@PostMapping("/excel/auto")
	@ApiOperation("Generate Multi-Sheet Excel Using Split field")
	public ResponseEntity<ExcelResponse> createMultiSheetExcel(@Valid @RequestBody MultiSheetExcelRequest request) {
		return new ResponseEntity<>(excelService.createMultiSheetExcel(request), HttpStatus.OK);
	}

	@GetMapping("/excel")
	@ApiOperation("List all existing files")
	public ResponseEntity<List<ExcelResponse>> listExcels() {
		return new ResponseEntity<>(excelService.findAll(), HttpStatus.OK);
	}

	@GetMapping("/excel/{id}/content")
	@ApiOperation("Download Excel")
	public void downloadExcel(@PathVariable @Valid @NotNull UUID id, HttpServletResponse response) throws IOException {
		ExcelFile excelFile = excelService.getExcelFileById(id);
		try (InputStream fileStream =
				new DataInputStream(new FileInputStream(excelFile.getFilePath().toFile()))) {
			String fileName = excelFile.getTitle().replace(" ", "_") + ".xlsx";
			response.setHeader("Content-Type", "application/vnd.ms-excel");
			response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
			FileCopyUtils.copy(fileStream, response.getOutputStream());
		}
	}

	@DeleteMapping(value="/excel/{id}")
	@ApiOperation("Delete Excel File")
	public ResponseEntity<ExcelResponse> deleteExcel(@PathVariable("id") @Valid @NotNull UUID id) {
		return new ResponseEntity<>(excelService.deleteExcel(id), HttpStatus.OK);
	}

	@ExceptionHandler(ExcelFileNotFoundException.class)
	public ResponseEntity<ErrorResponse> fileNotFoundExceptionHandler(Exception e) {
		return new ResponseEntity<>(
				new ErrorResponse("The file could not be found", "404"), HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse> handleConstraintViolationException(MethodArgumentNotValidException e) {
		return new ResponseEntity<>(
				new ErrorResponse("Input validation error: " + e.getMessage(), "400"), HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler
	public ResponseEntity<ErrorResponse> exceptionHandler(Exception e) {
		return new ResponseEntity<>(
				new ErrorResponse(e.getMessage(), "500"), HttpStatus.INTERNAL_SERVER_ERROR);
	}
}

