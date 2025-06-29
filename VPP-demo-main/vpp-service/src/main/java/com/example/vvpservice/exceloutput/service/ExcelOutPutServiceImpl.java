package com.example.vvpservice.exceloutput.service;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.write.style.column.SimpleColumnWidthStyleStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ExcelOutPutServiceImpl implements IExcelOutPutService {

    private static Logger logger = LoggerFactory.getLogger(ExcelOutPutServiceImpl.class);

    @Override
    public void excelTemplateOutput(OutputStream outputStream, String templatePath ,Map<String,Object> data) {
        EasyExcel.write(outputStream).withTemplate(templatePath).sheet().doFill(data);

    }

    @Override
    public void excelTemplateOutput(OutputStream outputStream, InputStream in, Map<String,Object> data) {
        EasyExcel.write(outputStream).withTemplate(in).sheet().doFill(data);
    }

    @Override
    public void excelDataOutPut(OutputStream outputStream, Class clazz, List data) {
        EasyExcel.write(outputStream).head(clazz)
                .registerWriteHandler(new SimpleColumnWidthStyleStrategy(18)).sheet().doWrite(data);
    }

    @Override
    public void excelDataOutPut(OutputStream outputStream, List<List<String>> head, List<Map<String, Object>> data) {

        List<List<Object>> contents = new ArrayList<>();

        List<String> hs = new ArrayList<>();
        head.forEach(e -> hs.add(e.get(0)));

        data.forEach(d -> {
            List<Object> content = new ArrayList<>();
            hs.forEach(h -> content.add(d.get(h)));
            contents.add(content);
        });

        EasyExcel.write(outputStream).head(head).sheet().doWrite(contents);

    }
}
