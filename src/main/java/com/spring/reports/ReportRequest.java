package com.spring.reports;

import java.util.List;
import java.util.Map;

public class ReportRequest extends ReportRequestItem {
  String outputFilename;

  public String getOutputFilename() {
    return outputFilename;
  }

  public void setOutputFilename(String outputFilename) {
    this.outputFilename = outputFilename;
  }

}
