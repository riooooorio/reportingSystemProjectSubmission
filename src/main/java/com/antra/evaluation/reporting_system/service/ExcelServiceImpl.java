package com.antra.evaluation.reporting_system.service;

import com.antra.evaluation.reporting_system.exception.ExcelFileCreationException;
import com.antra.evaluation.reporting_system.exception.ExcelFileDeletionException;
import com.antra.evaluation.reporting_system.exception.ExcelFileNotFoundException;
import com.antra.evaluation.reporting_system.pojo.api.ExcelResponse;
import com.antra.evaluation.reporting_system.pojo.report.ExcelData;
import com.antra.evaluation.reporting_system.pojo.report.ExcelFile;
import com.antra.evaluation.reporting_system.repo.ExcelRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ExcelServiceImpl implements ExcelService {

  private final ExcelRepository excelRepository;
  private final ExcelGenerationService excelGenService;

  @Autowired
  public ExcelServiceImpl(
      ExcelRepository excelRepository,
      ExcelGenerationService excelGenService
  ) {
    this.excelRepository = excelRepository;
    this.excelGenService = excelGenService;
  }

  public InputStream getExcelBodyById(String id) {
    ExcelFile fileInfo = getExcelFileById(id);
    File localExcelFile = fileInfo.getFilePath().toFile();
    try {
      return new DataInputStream(new FileInputStream(localExcelFile));
    } catch (FileNotFoundException e) {
      throw new ExcelFileNotFoundException("Unable to locate local file: " + localExcelFile.getAbsolutePath());
    }
  }

  @Override
  public ExcelResponse createExcel(ExcelData data) {
    try {
      ExcelResponse response = excelGenService.generateExcelReport(data);
      ExcelFile file = ExcelFile.builder()
          .fileId(response.getFileId())
          .fileName(response.getFileName())
          .fileSize(response.getFileSize())
          .build();
      excelRepository.saveFile(file);
      return response;
    } catch (IOException e) {
      throw new ExcelFileCreationException("Unable to create file", e);
    }
  }

  @Override
  public ExcelResponse deleteExcel(String id) {
    ExcelFile excelFile = getExcelFileById(id);
    if (excelFile.getFilePath().toFile().delete()) {
      excelRepository.deleteFile(id);
      return ExcelResponse.from(excelFile);
    } else {
      throw new ExcelFileDeletionException("Unable to delete file");
    }
  }

  @Override
  public ExcelFile getExcelFileById(String id) {
    return excelRepository.getFileById(id).orElseThrow(() ->
        new ExcelFileNotFoundException("Unable to locate file by id: " + id));
  }

  @Override
  public List<ExcelResponse> findAll() {
   return excelRepository.getFiles().stream()
       .map(ExcelResponse::from)
       .collect(Collectors.toList());
  }

}
