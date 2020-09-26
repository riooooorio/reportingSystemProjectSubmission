package com.antra.evaluation.reporting_system.service;

import com.antra.evaluation.reporting_system.converter.ExcelRequestToExcelDataConverter;
import com.antra.evaluation.reporting_system.converter.MultiSheetExcelRequestToExcelDataConverter;
import com.antra.evaluation.reporting_system.exception.ExcelFileCreationException;
import com.antra.evaluation.reporting_system.exception.ExcelFileDeletionException;
import com.antra.evaluation.reporting_system.exception.ExcelFileNotFoundException;
import com.antra.evaluation.reporting_system.pojo.api.ExcelRequest;
import com.antra.evaluation.reporting_system.pojo.api.ExcelResponse;
import com.antra.evaluation.reporting_system.pojo.api.MultiSheetExcelRequest;
import com.antra.evaluation.reporting_system.pojo.report.ExcelData;
import com.antra.evaluation.reporting_system.pojo.report.ExcelFile;
import com.antra.evaluation.reporting_system.repo.ExcelRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ExcelServiceImpl implements ExcelService {

  private final ExcelRepository excelRepository;
  private final ExcelGenerationService excelGenService;
  private final ExcelRequestToExcelDataConverter singleSheetConverter;
  private final MultiSheetExcelRequestToExcelDataConverter multiSheetConverter;

  @Autowired
  public ExcelServiceImpl(
      ExcelRepository excelRepository,
      ExcelGenerationService excelGenService
  ) {
    this.excelRepository = excelRepository;
    this.excelGenService = excelGenService;
    this.singleSheetConverter = new ExcelRequestToExcelDataConverter();
    this.multiSheetConverter = new MultiSheetExcelRequestToExcelDataConverter();
  }

  @Override
  public ExcelResponse createExcel(ExcelRequest request) {
    try {
      ExcelData data = singleSheetConverter.convert(request);
      return doCreateExcel(data);
    } catch (IOException e) {
      throw new ExcelFileCreationException("Unable to create file", e);
    }
  }

  @Override
  public ExcelResponse createMultiSheetExcel(MultiSheetExcelRequest request) {
    try {
      ExcelData data = multiSheetConverter.convert(request);
      return doCreateExcel(data);
    } catch (IOException e) {
      throw new ExcelFileCreationException("Unable to create file", e);
    }
  }

  @Override
  public ExcelResponse deleteExcel(UUID id) {
    ExcelFile excelFile = getExcelFileById(id);
    if (excelFile.getFilePath().toFile().delete()) {
      excelRepository.deleteFile(id);
      return ExcelResponse.from(excelFile);
    } else {
      throw new ExcelFileDeletionException("Unable to delete file");
    }
  }

  @Override
  public ExcelFile getExcelFileById(UUID id) {
    return excelRepository.getFileById(id).orElseThrow(() ->
        new ExcelFileNotFoundException("Unable to locate file by id: " + id));
  }

  @Override
  public List<ExcelResponse> findAll() {
   return excelRepository.getFiles().stream()
       .map(ExcelResponse::from)
       .collect(Collectors.toList());
  }

  private ExcelResponse doCreateExcel(ExcelData data) throws IOException {
    File rawFile = excelGenService.generateExcelReport(data);
    ExcelFile file = ExcelFile.builder()
        .fileId(data.getFileId())
        .title(data.getTitle())
        .fileSize(rawFile.length())
        .filePath(rawFile.toPath())
        .generatedTime(data.getCreatedAt())
        .build();
    excelRepository.saveFile(file);
    return ExcelResponse.builder()
        .fileId(data.getFileId())
        .fileSize(rawFile.length())
        .generatedTime(data.getCreatedAt())
        .build();
  }

}
