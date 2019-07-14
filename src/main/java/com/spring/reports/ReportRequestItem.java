package com.spring.reports;

import java.util.List;
import java.util.Map;

public class ReportRequestItem {
  Map<String, Object> params;
  List<Map<String, Object>> dataSource;

  public Map<String, Object> getParams() {
    return params;
  }

  public void setParams(Map<String, Object> params) {
    this.params = params;
  }

  public List<Map<String, Object>> getDataSource() {
    return dataSource;
  }

  public void setDataSource(List<Map<String, Object>> dataSource) {
    this.dataSource = dataSource;
  }
}
