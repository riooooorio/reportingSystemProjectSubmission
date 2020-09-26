package com.antra.evaluation.reporting_system.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.sql.Date;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.poi.ooxml.POIXMLProperties;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.WorkbookUtil;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.antra.evaluation.reporting_system.pojo.api.ExcelResponse;
import com.antra.evaluation.reporting_system.pojo.report.ExcelData;
import com.antra.evaluation.reporting_system.pojo.report.ExcelDataHeader;
import com.antra.evaluation.reporting_system.pojo.report.ExcelDataSheet;

/**
 * Data Stucture data - title, generatedTime - sheets -sheet1 - title (required) - headers - name -
 * width - type - dataRows - List of objects/values
 */
@Service
public class ExcelGenerationServiceImpl implements ExcelGenerationService {

  private final String directoryName;

  public ExcelGenerationServiceImpl(@Value("${datastore.excel.local.dir}") String directoryName) {
    this.directoryName = directoryName;
  }

  @Override
  public ExcelResponse generateExcelReport(ExcelData data) throws IOException {
    ExcelResponse response = null;
    validateData(data);
    // Create a workbook
    XSSFWorkbook workBook = new XSSFWorkbook();
    POIXMLProperties props = workBook.getProperties();
    /* Let us set some core properties now */
    POIXMLProperties.CoreProperties coreProp = props.getCoreProperties();
    coreProp.setCreator(data.getSubmitter()); // set document creator
    coreProp.setDescription(data.getTitle()); // set Description

    // sheet = generateSheet(workBook, "Sheet 1");

    for (ExcelDataSheet s : data.getSheets()) {
      Sheet sheet = generateSheet(workBook, s.getTitle());

      // Write Header
      writeHeader(workBook, sheet, s.getHeaders());
      // Write data
      createDataRows(workBook, sheet, s.getDataRows());
    }

    String uniqueFileName = getTheNewestFile(directoryName);

    try (FileOutputStream outputStream = new FileOutputStream(directoryName + uniqueFileName)) {
      workBook.write(outputStream);
    }

    response = getFileMetaData(new File(directoryName + uniqueFileName));

    return response;
  }

  private void validateData(ExcelData data) {
    if (data.getSheets().size() < 1) {
      throw new RuntimeException("Excel Data Error: no sheet is defined");
    }
    for (ExcelDataSheet sheet : data.getSheets()) {
      if (StringUtils.isEmpty(sheet.getTitle())) {
        throw new RuntimeException("Excel Data Error: sheet name is missing");
      }
      if (sheet.getHeaders() != null) {
        int columns = sheet.getHeaders().size();
        for (List<Object> dataRow : sheet.getDataRows()) {
          if (dataRow.size() != columns) {
            throw new RuntimeException(
                "Excel Data Error: sheet data has difference length than header number");
          }
        }
      }
    }
  }


  private Sheet generateSheet(Workbook workBook, String sheetName) {

    return workBook.createSheet(WorkbookUtil.createSafeSheetName(sheetName));
  }

  private void writeHeader(Workbook workBook, Sheet sheet, List<ExcelDataHeader> headers) {

    int rowCount = 0;
    int columnCount = 0;

    try {

      Row row = sheet.createRow(rowCount);
      for (ExcelDataHeader header : headers) {
        Cell headerCell = row.createCell(columnCount++);
        headerCell.setCellValue(header.getName());

        headerCell.setCellStyle(generateDefaultHdrStyle(workBook));
      }
    } catch (Exception e) {

    }
  }

  private XSSFCellStyle generateDefaultHdrStyle(Workbook workBook) {
    XSSFCellStyle hdrCellStyle = (XSSFCellStyle) workBook.createCellStyle();
    hdrCellStyle.setBorderTop(BorderStyle.MEDIUM);
    hdrCellStyle.setBorderBottom(BorderStyle.MEDIUM);
    hdrCellStyle.setBorderLeft(BorderStyle.MEDIUM);
    hdrCellStyle.setBorderRight(BorderStyle.MEDIUM);

    XSSFFont hdrFont = (XSSFFont) workBook.createFont();
    hdrFont.setBold(true);
    hdrCellStyle.setFont(hdrFont);
    return hdrCellStyle;
  }

  private XSSFCellStyle generateDataStyle(Workbook workBook) {
    XSSFCellStyle dataCellStyle = (XSSFCellStyle) workBook.createCellStyle();
    dataCellStyle.setBorderTop(BorderStyle.THIN);
    dataCellStyle.setBorderBottom(BorderStyle.THIN);
    dataCellStyle.setBorderLeft(BorderStyle.THIN);
    dataCellStyle.setBorderRight(BorderStyle.THIN);

    return dataCellStyle;
  }

  private void createDataRows(Workbook workBook, Sheet sheet, List<List<Object>> dataRows) {
    int rowCount = 1;
    int columnCount = 0;
    Row row = null;
    Cell dataCell = null;
    XSSFCellStyle cellDataStyle = null;

    try {
      for (List<Object> dataList : dataRows) {
        row = sheet.createRow(rowCount++);
        columnCount = 0;
        for (Object data : dataList) {
          dataCell = row.createCell(columnCount++);
          cellDataStyle = (XSSFCellStyle) workBook.createCellStyle();

          if (data != null) {
            if (data instanceof String) {
              dataCell.setCellValue((String) data);
            } else if (data instanceof Long) {
              dataCell.setCellValue((Long) data);
            } else if (data instanceof Integer) {
              dataCell.setCellValue((Integer) data);
            } else if (data instanceof Double) {
              dataCell.setCellValue((Double) data);

            } else if (data instanceof BigDecimal) {
              dataCell.setCellValue(Double.parseDouble(data.toString()));
            } else if (data instanceof Date) {
              dataCell.setCellValue((Date) data);
              cellDataStyle.setDataFormat(
                  workBook.getCreationHelper().createDataFormat().getFormat("yyyy-dd-MM"));
            }
          }
          dataCell.setCellStyle(generateDataStyle(workBook));
        }
      }
    } catch (Exception e) {
      // TODO: handle exception
    }
  }

  private ExcelResponse getFileMetaData(File file) {
    if (!file.exists() || !file.isFile()) {
      throw new RuntimeException("File does not exist : " + file.getName());
    }
    BasicFileAttributes attr;
    try {
      attr = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
      return ExcelResponse.builder()
          .fileSize(file.length())
          .fileName(file.getName())
          .generatedTime(attr.creationTime().toInstant())
          .fileId(getFileName(file.getName()))
          .build();

    } catch (IOException e) {
      // TODO Auto-generated catch block
      throw new RuntimeException("An error occurred");
    }
  }


  public Set<String> getAllFiles(String dir) {
    return Stream.of(new File(dir).listFiles()).filter(file -> !file.isDirectory())
        .map(File::getName)
        .collect(Collectors.toSet());
  }

  public String getTheNewestFile(String filePath) throws IOException {
    Path dir = Paths.get(filePath);
    String uniqueFileName = "1";
    String extn = ".xlsx";
    Optional<Path> lastFilePath = Files
        .list(dir) // here we get the stream with full directory listing
        .filter(f -> !Files.isDirectory(f)) // exclude subdirectories from listing
        .max(Comparator
            .comparingLong(f -> f.toFile().lastModified())); // finally get the last file using
    // simple comparator by lastModified
    // field

    if (lastFilePath.isPresent()) // your folder may be empty
    {
      String name = getFileName(lastFilePath.get().getFileName().toString());
      uniqueFileName = (Integer.valueOf(name) + 1) + extn;
    } else {
      uniqueFileName = uniqueFileName + extn;
    }

    return uniqueFileName;
  }

  private String getFileName(String fileName) {
    return fileName.substring(0, fileName.lastIndexOf('.'));
  }

}
