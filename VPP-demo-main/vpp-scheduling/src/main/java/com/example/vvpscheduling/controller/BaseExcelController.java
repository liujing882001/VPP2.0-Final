package com.example.vvpscheduling.controller;

import com.alibaba.excel.support.ExcelTypeEnum;
import com.example.vvpcommom.IdGenerator;
import com.example.vvpservice.exceloutput.service.IExcelOutPutService;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class BaseExcelController {

    protected void exec(HttpServletResponse response, List<Map<String, Object>> data, IExcelOutPutService
            iExcelOutPutService) {
        response.setCharacterEncoding("UTF-8");
        response.setHeader("content-Type", "application/vnd.ms-excel");
        try {
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(IdGenerator.generateId() + ExcelTypeEnum.XLSX.getValue(), "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if (data != null && data.size() > 0) {
            List<List<String>> heads = new ArrayList<>();
            data.get(0).keySet().forEach(e -> {
                ArrayList el = new ArrayList();
                el.add(e);
                heads.add(el);
            });
            try {
                iExcelOutPutService.excelDataOutPut(response.getOutputStream(), heads, data);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    protected void exec(HttpServletResponse response, List exports, Class clazz, IExcelOutPutService
            iExcelOutPutService) {
        response.setCharacterEncoding("UTF-8");
        response.setHeader("content-Type", "application/vnd.ms-excel");
        try {
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(IdGenerator.generateId() + ExcelTypeEnum.XLSX.getValue(), "UTF-8"));

            iExcelOutPutService.excelDataOutPut(response.getOutputStream(), clazz, exports);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
