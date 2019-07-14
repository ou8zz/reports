package com.spring.reports;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.export.ExporterInputItem;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimplePdfExporterConfiguration;
import net.sf.jasperreports.export.SimplePdfReportConfiguration;
import net.sf.jasperreports.export.SimpleExporterInputItem;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.*;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

// http://www.baeldung.com/spring-jasper
// https://github.com/iNamik/java_text_tables
@RestController
public class ReportController {
  private Logger logger = LoggerFactory.getLogger(ReportController.class);

  @ResponseBody
  @RequestMapping(value = "/reports/{filename}", method = RequestMethod.GET)
  public void readPdf(@PathVariable String filename, HttpServletResponse response) throws IOException {
    byte[] bytes = Files.readAllBytes(FileSystems.getDefault().getPath("tmp", filename));
    response.addHeader("Content-Disposition", "inline; filename=" + new String("filename".getBytes("GBK"), "ISO-8859-1"));
    String contentType = "application/octet-stream";
    String[] namePart = filename.split("\\.");
    switch (namePart[namePart.length-1]) {
      case "pdf": contentType = "application/pdf";
    }
    response.setContentType(contentType);
    response.setContentLength(bytes.length);
    ServletOutputStream out = response.getOutputStream();
    out.write(bytes);
    out.flush();
    out.close();
  }


  @ResponseBody
  @RequestMapping(value = "/reports/{reportId}/{format}", method = RequestMethod.POST, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
  public Map<String, Object> generateReport(@PathVariable String reportId, @PathVariable String format,
                               HttpEntity<String> httpEntity) throws IOException, JRException {

    ObjectMapper objectMapper = new ObjectMapper();

    List<ExporterInputItem> jrPrints = new ArrayList<>();

    String filename = String.format("%s.pdf", System.currentTimeMillis());

    try {
      CombinedReportRequest combinedRequest = objectMapper.readValue(httpEntity.getBody(), CombinedReportRequest.class);

      for (ReportRequestItem bodyItem : combinedRequest.getItems()) {
        jrPrints.add(new SimpleExporterInputItem(_generateReport(reportId, "pdf", bodyItem)));
      }
      if (StringUtils.isNotBlank(combinedRequest.outputFilename)) {
        filename = combinedRequest.outputFilename;
      }
    } catch (JsonMappingException e) {
      // just ignore
    }
    try {
      ReportRequest singleRequest = objectMapper.readValue(httpEntity.getBody(), ReportRequest.class);
      jrPrints.add(new SimpleExporterInputItem(_generateReport(reportId, "pdf", singleRequest)));
      if (StringUtils.isNotBlank(singleRequest.outputFilename)) {
        filename = singleRequest.outputFilename;
      }
    } catch (JsonMappingException e) {
      // just ignore
    }


    JRPdfExporter exporter = new JRPdfExporter();

    exporter.setExporterInput(new SimpleExporterInput(jrPrints));

//    ByteArrayOutputStream reportOut = new ByteArrayOutputStream();
//    exporter.setExporterOutput(
//            new SimpleOutputStreamExporterOutput(reportOut));


    exporter.setExporterOutput(
            new SimpleOutputStreamExporterOutput(String.format("tmp/%s", filename)));


    SimplePdfReportConfiguration reportConfig
            = new SimplePdfReportConfiguration();
    reportConfig.setSizePageToContent(true);
    reportConfig.setForceLineBreakPolicy(false);

    SimplePdfExporterConfiguration exportConfig
            = new SimplePdfExporterConfiguration();
    exportConfig.setMetadataAuthor("SYL");
    exportConfig.setEncrypted(true);
    exportConfig.setAllowedPermissionsHint("PRINTING");

    exporter.setConfiguration(reportConfig);
    exporter.setConfiguration(exportConfig);

    exporter.exportReport();

//    response.addHeader("Content-Disposition", "inline; filename=" + new String("a.pdf".getBytes("GBK"), "ISO-8859-1"));
//    response.setContentType("application/"+format);
//    response.setContentLength(reportOut.size());
//    ServletOutputStream out = response.getOutputStream();
//    reportOut.writeTo(out);
//    out.flush();
//    out.close();
    Map<String, Object> result = new HashMap<>();
    result.put("filename", filename);
    return result;
  }

  public JasperPrint _generateReport(String reportId, String format, ReportRequestItem body) throws IOException, JRException {
    logger.debug("reportId: {}, format: {}", reportId, format);
    if (body.params != null) {
      logger.debug("in params:");
      for (Map.Entry<String, Object> entry : body.params.entrySet()) {
        logger.debug("    {} -> {}@{}", entry.getKey(), entry.getValue(), entry.getValue().getClass());
      }
    }
    logger.debug("in dataSource:");
    List<Map<String, Object>> dataSource = new ArrayList<>();
    for (Map<String, Object> item : body.dataSource) {
      Map<String, Object> newItem = new HashMap<>();
      for (Map.Entry<String, Object> entry : item.entrySet()) {
        logger.debug("    {}: {} -> {}@{}", dataSource.size(), entry.getKey(), entry.getValue(), entry.getValue().getClass());
        if (entry.getValue() instanceof java.util.ArrayList) {
          newItem.put(entry.getKey(), new JRBeanCollectionDataSource((java.util.List<Map<String, Object>>)entry.getValue()));
        } else {
          newItem.put(entry.getKey(), entry.getValue());
        }
      }
      dataSource.add(newItem);
    }
    Resource resource = new ClassPathResource(String.format("jasper/%s.jasper", reportId));

    InputStream resourceInputStream = resource.getInputStream();
    JasperReport jasperReport = (JasperReport)JRLoader.loadObject(resourceInputStream);
    return JasperFillManager.fillReport(jasperReport, body.params, new JRBeanCollectionDataSource(dataSource));
  }
}
