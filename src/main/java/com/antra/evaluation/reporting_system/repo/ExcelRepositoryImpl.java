package com.antra.evaluation.reporting_system.repo;

import com.antra.evaluation.reporting_system.exception.ExcelFileNotFoundException;
import com.antra.evaluation.reporting_system.pojo.report.ExcelFile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

@Repository
@Slf4j
public class ExcelRepositoryImpl implements ExcelRepository {

  private final Map<String, ExcelFile> excelData;

  @Autowired
  public ExcelRepositoryImpl(@Value("${datastore.excel.local.dir}") String directoryName) {
    this.excelData = new ConcurrentHashMap<>();
    loadExistingExcelFiles(directoryName);
  }

  @Override
  public Optional<ExcelFile> getFileById(String id) {
    return Optional.ofNullable(excelData.get(id));
  }

  @Override
  public ExcelFile saveFile(ExcelFile file) {
    excelData.put(file.getFileId(), file);
    return file;
  }

  @Override
  public ExcelFile deleteFile(String id) {
    ExcelFile exlFile = getFileById(id).orElseThrow(
        () -> new ExcelFileNotFoundException("Unable to locate file by Id: " + id));
    excelData.remove(id);
    return exlFile;
  }

  @Override
  public List<ExcelFile> getFiles() {
    return new ArrayList<>(excelData.values());
  }

  private void loadExistingExcelFiles(String directoryName) {
    try (Stream<Path> paths = Files.walk(Paths.get(directoryName))) {
      paths.filter(Files::isRegularFile)
          .map(filePath -> ExcelFile.builder()
              .fileName(filePath.getFileName().toString())
              .fileId(getFileName(filePath.getFileName().toString()))
              .fileSize(filePath.toFile().length())
              .generatedTime(Instant.ofEpochMilli(filePath.toFile().lastModified()))
              .filePath(filePath)
              .build()
          ).forEach(excelFile -> excelData.put(excelFile.getFileId(), excelFile));
    } catch (IOException e) {
      log.error("Error loading files from local directory {}", directoryName, e);
    }
  }

  private String getFileName(String fileName) {
    return fileName.substring(0, fileName.lastIndexOf('.'));
  }
}

