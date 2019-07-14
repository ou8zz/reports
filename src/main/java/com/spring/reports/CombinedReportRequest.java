package com.spring.reports;

import java.util.List;
import java.util.Map;

public class CombinedReportRequest {
  String outputFilename;
  List<ReportRequestItem> items;

  public String getOutputFilename() {
    return outputFilename;
  }

  public void setOutputFilename(String outputFilename) {
    this.outputFilename = outputFilename;
  }

  public List<ReportRequestItem> getItems() {
    return items;
  }

  public void setItems(List<ReportRequestItem> items) {
    this.items = items;
  }
}
