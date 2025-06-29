package com.example.vvpweb.carbon;

import com.example.vvpcommom.ResponseResult;
import com.example.vvpcommom.UserLoginToken;
import com.example.vvpservice.carbon.service.CaCollectionModelService;
import com.example.vvpweb.carbon.model.CaReportModel;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author maoyating
 * @description 碳资产-碳排放因子
 * @date 2022-08-09
 */
@RestController
@RequestMapping("/carbon/report")
@CrossOrigin
@Api(value = "碳资产管理-碳报告", tags = {"碳资产管理-碳报告"})
public class CaReportController {

    @Autowired
    private CaCollectionModelService caCollectionModelService;

    @ApiOperation("碳报告")
    @UserLoginToken
    @RequestMapping(value = "/getReportCount", method = {RequestMethod.POST})
    public ResponseResult getReportCount(@RequestParam("nodeId") String nodeId, @RequestParam("year") String year) {
        Map<String, List<CaReportModel>> maps = new HashMap<>();
        List<CaReportModel> caReportModels = new ArrayList<>();
        List<Object[]> guDRSY = caCollectionModelService.getReportCount(nodeId, 1, 1, year, "");
        CaReportModel guDRSYResp = new CaReportModel();
        guDRSYResp.setName("固定燃烧源");
        guDRSYResp.setValue(guDRSY.size() > 0 ? "0" : guDRSY.get(0)[0].toString());
        caReportModels.add(guDRSYResp);
        List<Object[]> liuDRSY = caCollectionModelService.getReportCount(nodeId, 1, 2, year, "");
        CaReportModel liuDRSYResp = new CaReportModel();
        liuDRSYResp.setName("流动燃烧源");
        liuDRSYResp.setValue(liuDRSY.size() > 0 ? "0" : liuDRSY.get(0)[0].toString());
        caReportModels.add(liuDRSYResp);
        List<Object[]> taoYXPF = caCollectionModelService.getReportCount(nodeId, 1, 3, year, "");
        CaReportModel taoYXPFResp = new CaReportModel();
        taoYXPFResp.setName("逃逸性排放");
        taoYXPFResp.setValue(taoYXPF.size() > 0 ? "0" : taoYXPF.get(0)[0].toString());
        caReportModels.add(taoYXPFResp);
        maps.put("scope1", caReportModels);
        List<Object[]> fanWE = caCollectionModelService.getReportCount(nodeId, 2, null, year, "cef.emission_factor_name,");
        maps.put("scope2", setCaReportModel(fanWE));
        List<Object[]> fanWS = caCollectionModelService.getReportCount(nodeId, 3, null, year, "cef.emission_factor_name,");
        maps.put("scope3", setCaReportModel(fanWS));
        return ResponseResult.success(maps);
    }

    @ApiOperation("碳报告导出")
    @UserLoginToken
    @RequestMapping(value = "/exportReportCount", method = {RequestMethod.POST})
    public void exportReportCount(HttpServletRequest request, HttpServletResponse response, @RequestParam("nodeId") String nodeId, @RequestParam("year") String year) {
        Map<String, List<CaReportModel>> maps = new HashMap<>();
        List<CaReportModel> fanWY = new ArrayList<>();
        List<Object[]> guDRSY = caCollectionModelService.getReportCount(nodeId, 1, 1, year, "");
        CaReportModel guDRSYResp = new CaReportModel();
        guDRSYResp.setName("固定燃烧源");
        guDRSYResp.setValue(guDRSY.size() > 0 ? "0" : guDRSY.get(0)[0].toString());
        fanWY.add(guDRSYResp);
        List<Object[]> liuDRSY = caCollectionModelService.getReportCount(nodeId, 1, 2, year, "");
        CaReportModel liuDRSYResp = new CaReportModel();
        liuDRSYResp.setName("流动燃烧源");
        liuDRSYResp.setValue(liuDRSY.size() > 0 ? "0" : liuDRSY.get(0)[0].toString());
        fanWY.add(liuDRSYResp);
        List<Object[]> taoYXPF = caCollectionModelService.getReportCount(nodeId, 1, 3, year, "");
        CaReportModel taoYXPFResp = new CaReportModel();
        taoYXPFResp.setName("逃逸性排放");
        taoYXPFResp.setValue(taoYXPF.size() > 0 ? "0" : taoYXPF.get(0)[0].toString());
        fanWY.add(taoYXPFResp);
        maps.put("scope1", fanWY);
        List<CaReportModel> fanWE = setCaReportModel(caCollectionModelService.getReportCount(nodeId, 2, null, year, "cef.emission_factor_name,"));
        List<CaReportModel> fanWS = setCaReportModel(caCollectionModelService.getReportCount(nodeId, 3, null, year, "cef.emission_factor_name,"));

        HSSFWorkbook wb = new HSSFWorkbook();
        HSSFSheet sheet = wb.createSheet("");
        HSSFRow row = sheet.createRow(0);
        HSSFCell cells = row.createCell(0);
        String[] equipmentPmCell = {"序号", "名称", "范围类型", "二氧化碳 （CO2）", "甲烷 （CH4）", "一氧化氮 （N2O）", "氢氟碳化合物 （HFC）", "全氟化碳 （PFCs）", "全部的"};
        for (int c = 1; c < equipmentPmCell.length + 1; c++) {
            cells.setCellValue(equipmentPmCell[c - 1]);
        }
        int count = 0;
        for (int i = 0; i < fanWY.size(); i++) {
            CaReportModel crm = fanWY.get(i);
            row = sheet.createRow(count++);
            row.createCell(0).setCellValue(count);
            row.createCell(1).setCellValue(crm.getName());
            row.createCell(2).setCellValue("范围一");
            row.createCell(3).setCellValue("0.0");
            row.createCell(4).setCellValue("0.0");
            row.createCell(5).setCellValue("0.0");
            row.createCell(6).setCellValue("0.0");
            row.createCell(7).setCellValue("0.0");
            row.createCell(8).setCellValue(crm.getValue());
        }

        for (int i = 0; i < fanWE.size(); i++) {
            CaReportModel crm = fanWE.get(i);
            row = sheet.createRow(count++);
            row.createCell(0).setCellValue(count);
            row.createCell(1).setCellValue(crm.getName());
            row.createCell(2).setCellValue("范围二");
            row.createCell(3).setCellValue("0.0");
            row.createCell(4).setCellValue("0.0");
            row.createCell(5).setCellValue("0.0");
            row.createCell(6).setCellValue("0.0");
            row.createCell(7).setCellValue("0.0");
            row.createCell(8).setCellValue(crm.getValue());
        }

        for (int i = 0; i < fanWS.size(); i++) {
            CaReportModel crm = fanWS.get(i);
            row = sheet.createRow(count++);
            row.createCell(0).setCellValue(count);
            row.createCell(1).setCellValue(crm.getName());
            row.createCell(2).setCellValue("范围三");
            row.createCell(3).setCellValue("0.0");
            row.createCell(4).setCellValue("0.0");
            row.createCell(5).setCellValue("0.0");
            row.createCell(6).setCellValue("0.0");
            row.createCell(7).setCellValue("0.0");
            row.createCell(8).setCellValue(crm.getValue());
        }

        try {
            String newFileName, fileName = "楼宇碳排放核查报告";
            if (request.getHeader("User-Agent").toLowerCase().indexOf("firefox") > 0) {
                newFileName = new String(fileName.getBytes("GBK"), "iso-8859-1");
            } else if (request.getHeader("USER-AGENT").toLowerCase().indexOf("msie") > 0) {
                newFileName = URLEncoder.encode(fileName, "UTF-8");
                newFileName = newFileName.replace("+", "%20");
            } else {
                newFileName = new String(fileName.getBytes("UTF-8"), "iso-8859-1");
            }
            response.addHeader("Content-Disposition", "attachment;filename=\"" + newFileName + "\"" + ".xls");
            response.setContentType("application/octet-stream");
            OutputStream out = new BufferedOutputStream((OutputStream) response.getOutputStream());

            wb.write(out);
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private List<CaReportModel> setCaReportModel(List<Object[]> objects) {
        List<CaReportModel> caReportModels = new ArrayList<>();
        for (Object[] object : objects) {
            CaReportModel ccm = new CaReportModel();
            ccm.setName(object[0].toString());
            ccm.setValue(object[1].toString());
            caReportModels.add(ccm);
        }
        return caReportModels;
    }

}