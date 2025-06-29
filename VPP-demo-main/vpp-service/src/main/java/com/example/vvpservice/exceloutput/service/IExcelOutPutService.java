package com.example.vvpservice.exceloutput.service;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

public interface IExcelOutPutService {

    void excelTemplateOutput(OutputStream outputStream, String templatePath, Map<String,Object> data);

    void excelTemplateOutput(OutputStream outputStream, InputStream in, Map<String,Object> data);

    void excelDataOutPut(OutputStream outputStream, Class clazz, List data);

    void excelDataOutPut(OutputStream outputStream, List<List<String>> head, List<Map<String, Object>> data);
}
