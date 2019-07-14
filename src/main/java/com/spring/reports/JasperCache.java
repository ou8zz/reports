package com.spring.reports;

import net.sf.jasperreports.engine.JasperReport;

import java.util.HashMap;
import java.util.Map;

public class JasperCache {
  private static JasperCache instance;
  private Map<String, JasperReport> cache = new HashMap<>();

  private JasperCache(){}

  public static synchronized JasperCache getInstance(){
    if(instance == null){
      instance = new JasperCache();
    }
    return instance;
  }

  public JasperReport getCompiledFile(String key) {
    return cache.get(key);
  }
  public void setCompiledFile(String key, JasperReport report) {
    cache.put(key, report);
  }
}
