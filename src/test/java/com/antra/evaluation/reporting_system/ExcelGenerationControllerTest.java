package com.antra.evaluation.reporting_system;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.antra.evaluation.reporting_system.endpoint.ExcelGenerationController;
import com.antra.evaluation.reporting_system.pojo.api.ExcelRequest;
import com.antra.evaluation.reporting_system.pojo.api.ExcelResponse;
import com.antra.evaluation.reporting_system.pojo.report.ExcelData;
import com.antra.evaluation.reporting_system.pojo.report.ExcelFile;
import com.antra.evaluation.reporting_system.service.ExcelService;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@WebMvcTest(ExcelGenerationController.class)
public class ExcelGenerationControllerTest {
	private static final UUID FILE_ID = UUID.randomUUID();

	@MockBean
	private ExcelService excelService;

	@Autowired
	private MockMvc mockMvc;

	@BeforeEach
	public void configMock() {
		MockitoAnnotations.initMocks(this);
		RestAssuredMockMvc.standaloneSetup(new ExcelGenerationController(excelService));
	}

	@Test
	public void testCreateExcelBadRequest() throws Exception {
		ExcelResponse res = createResponse();
		when(excelService.createExcel(any())).thenReturn(res);
		this.mockMvc
				.perform(MockMvcRequestBuilders.post("/excel").contentType(MediaType.APPLICATION_JSON)
						.content("{\"headers\":[\"Name\",\"Age\"], \"data\":[[\"Teresa\",\"5\"],[\"Daniel\",\"1\"]]}"))
				.andExpect(status().is4xxClientError());
	}

	@Test
	public void testCreateExcelSuccess() throws Exception {
		ExcelResponse res = createResponse();
		when(excelService.createExcel(any())).thenReturn(res);
		this.mockMvc
				.perform(MockMvcRequestBuilders.post("/excel").contentType(MediaType.APPLICATION_JSON)
						.content("{\"description\":\"Sample\",\"headers\":[\"Student #\",\"Name\",\"Class\",\"Score\"],\"data\":[[\"s-001\",\"James\",\"Class-A\",\"A+\"]], \"submitter\":\"Mrs. York\"}"))
				.andExpect(status().isOk());

	}

	@Test
	public void testCreateMultiSheetExcel() throws Exception {
		ExcelResponse res = createResponse();
		when(excelService.createExcel(any())).thenReturn(res);
		this.mockMvc
				.perform(MockMvcRequestBuilders.post("/excel").contentType(MediaType.APPLICATION_JSON)
						.content("{\"description\":\"Sample\",\"headers\":[\"Student #\",\"Name\",\"Class\",\"Score\"],\"data\":[[\"s-001\",\"James\",\"Class-A\",\"A+\"]], \"submitter\":\"Mrs. York\",\"splitBy\":\"Score\"}"))
				.andExpect(status().isOk());
	}

	@Test
	public void testListExcels() throws Exception {
		List<ExcelResponse> list = new ArrayList<>();
		list.add(createResponse());
		Mockito.when(excelService.findAll()).thenReturn(list);

		when(excelService.findAll()).thenReturn(list);
		this.mockMvc.perform(MockMvcRequestBuilders.get("/excel"))
				.andExpect(MockMvcResultMatchers.jsonPath("$[0].fileId").value(FILE_ID.toString()))
				.andExpect(status().isOk());
	} // Done

	@Test
	public void testDownloadExcel() throws Exception {
		String string = "hello";
		ExcelFile file = ExcelFile.builder()
				.title("Test File")
				.filePath(File.createTempFile("_tmp", "").toPath())
				.build();
		Mockito.when(excelService.getExcelFileById(any())).thenReturn(file);
		this.mockMvc.perform(MockMvcRequestBuilders.get("/excel/" + FILE_ID.toString() + "/content"))
				.andExpect(status().isOk());

	}

	@Test
	public void testDeleteExcel() throws Exception {
		ExcelResponse res = createResponse();
		Mockito.when(excelService.deleteExcel(any())).thenReturn(res);
		this.mockMvc.perform(MockMvcRequestBuilders.delete("/excel/" + FILE_ID.toString()))
				.andExpect(status().isOk());
	}

	private ExcelResponse createResponse() {
		return ExcelResponse.builder()
				.generatedTime(LocalDateTime.now())
				.fileSize(1457L)
				.fileId(FILE_ID)
				.build();
	}

}
＀
