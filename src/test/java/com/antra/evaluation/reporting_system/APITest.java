package com.antra.evaluation.reporting_system;

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
import java.io.InputStream;
import java.nio.charset.Charset;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@WebMvcTest(ExcelGenerationController.class)
public class APITest {
	@MockBean
	ExcelService excelService;

	@Autowired
	private MockMvc mockMvc;
	@MockBean
	private ExcelRequest request;

	@BeforeEach
	public void configMock() {
		MockitoAnnotations.initMocks(this);
		RestAssuredMockMvc.standaloneSetup(new ExcelGenerationController(excelService));
	}

	@Test
	void testCreateExcel() throws Exception {
		ExcelData data = new ExcelData();

		ExcelResponse res =  mock(ExcelResponse.class);

		when(excelService.createExcel(data)).thenReturn(res);

		this.mockMvc
				.perform(MockMvcRequestBuilders.post("/excel").contentType(MediaType.APPLICATION_JSON)
						.content("{\"headers\":[\"Name\",\"Age\"], \"data\":[[\"Teresa\",\"5\"],[\"Daniel\",\"1\"]]}"))
				.andExpect(status().isOk());

	}

	@Test
	void testCreateMultiSheetExcel() throws Exception {
		ExcelData data = new ExcelData();

		ExcelResponse res = mock(ExcelResponse.class);

		when(excelService.createExcel(data)).thenReturn(res);

		this.mockMvc
				.perform(MockMvcRequestBuilders.post("/excel").contentType(MediaType.APPLICATION_JSON)
						.content("{\"headers\":[\"Name\",\"Age\"], \"data\":[[\"Teresa\",\"5\"],[\"Daniel\",\"1\"]]}"))
				.andExpect(status().isOk());
	}

	@Test
	void testListExcels() throws Exception {
		List<ExcelResponse> list = new ArrayList<>();
		list.add(new ExcelResponse("1", "1.xlsx", 10L, Instant. now()));
		Mockito.when(excelService.findAll()).thenReturn(list);

		when(excelService.findAll()).thenReturn(list);
		this.mockMvc.perform(MockMvcRequestBuilders.get("/excel"))
				.andExpect(MockMvcResultMatchers.jsonPath("$[0].fileId").value("1")).andExpect(status().isOk());
	} // Done

	@Test
	void testDownloadExcel() throws Exception {

		String string = "hello";
		InputStream inputStream = new ByteArrayInputStream(string.getBytes(Charset.forName("UTF-8")));
		ExcelFile file = ExcelFile.builder().fileName("1.xlsx").build();

		Mockito.when(excelService.getExcelBodyById(anyString())).thenReturn(inputStream);
		Mockito.when(excelService.getExcelFileById(anyString())).thenReturn(file);
		this.mockMvc.perform(MockMvcRequestBuilders.get("/excel/8/content")).andExpect(status().isOk());

	}

	@Test
	void testDeleteExcel() throws Exception {
		ExcelResponse res = new ExcelResponse("1", "1.xlsx", 10L, Instant.now());
		Mockito.when(excelService.deleteExcel(toString())).thenReturn(res);
		this.mockMvc.perform(MockMvcRequestBuilders.delete("/excel/8")).andExpect(status().isOk());
	}

}
