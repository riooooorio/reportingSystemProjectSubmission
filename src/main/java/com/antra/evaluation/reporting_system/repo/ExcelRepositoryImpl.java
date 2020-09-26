package com.antra.evaluation.reporting_system.repo;

import com.antra.evaluation.reporting_system.exception.ExcelFileNotFoundException;
import com.antra.evaluation.reporting_system.pojo.report.ExcelFile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Repository
@Slf4j
public class ExcelRepositoryImpl implements ExcelRepository {

  private final Map<UUID, ExcelFile> excelData;

  @Autowired
  public ExcelRepositoryImpl() {
    this.excelData = new ConcurrentHashMap<>();
  }

  @Override
  public Optional<ExcelFile> getFileById(UUID id) {
    return Optional.ofNullable(excelData.get(id));
  }

  @Override
  public ExcelFile saveFile(ExcelFile file) {
    excelData.put(file.getFileId(), file);
    return file;
  }

  @Override
  public ExcelFile deleteFile(UUID id) {
    ExcelFile exlFile = getFileById(id).orElseThrow(
        () -> new ExcelFileNotFoundException("Unable to locate file by Id: " + id));
    excelData.remove(id);
    return exlFile;
  }

  @Override
  public List<ExcelFile> getFiles() {
    return new ArrayList<>(excelData.values());
  }
}

