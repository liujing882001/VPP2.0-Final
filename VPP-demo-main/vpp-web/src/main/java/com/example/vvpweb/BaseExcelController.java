package com.example.vvpweb;

import com.alibaba.excel.support.ExcelTypeEnum;
import com.example.vvpcommom.IdGenerator;
import com.example.vvpservice.exceloutput.service.IExcelOutPutService;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;
import java.util.stream.Collectors;

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
            List<String> sets = new ArrayList<>(data.get(0).keySet());
            sets.sort(new Comparator<String>() {
                @Override
                public int compare(String o1, String o2) {
                    return o1.compareTo(o2);
                }
            });
            heads.add(Arrays.asList("节点"));
            sets.forEach(e -> {
                List el = new ArrayList();
                el.add(e);
                if(!e.equals("节点") && !e.equals("合计")){
                    heads.add(el);
                }

            });
            heads.add(Arrays.asList("合计"));
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

    protected void execTemplate(HttpServletResponse response,  String templatePath, Map<String,Object> data, IExcelOutPutService
            iExcelOutPutService) {
        response.setCharacterEncoding("UTF-8");
        response.setHeader("content-Type", "application/vnd.ms-excel");
        try {
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(IdGenerator.generateId() + ExcelTypeEnum.XLSX.getValue(), "UTF-8"));

            iExcelOutPutService.excelTemplateOutput(response.getOutputStream(), templatePath, data);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    protected void execTemplate(HttpServletResponse response, InputStream in, Map<String,Object> data, IExcelOutPutService
            iExcelOutPutService) {
        response.setCharacterEncoding("UTF-8");
        response.setHeader("content-Type", "application/vnd.ms-excel");
        try {
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(IdGenerator.generateId() + ExcelTypeEnum.XLSX.getValue(), "UTF-8"));

            iExcelOutPutService.excelTemplateOutput(response.getOutputStream(), in, data);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    protected void execTemplate(HttpServletResponse response, InputStream in, Map<String,Object> data, IExcelOutPutService
            iExcelOutPutService,String fileName) {
        response.setCharacterEncoding("UTF-8");
        response.setHeader("content-Type", "application/vnd.ms-excel");
        try {
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName + ExcelTypeEnum.XLSX.getValue());

            iExcelOutPutService.excelTemplateOutput(response.getOutputStream(), in, data);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
