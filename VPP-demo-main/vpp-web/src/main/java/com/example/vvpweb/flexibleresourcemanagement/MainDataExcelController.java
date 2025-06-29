package com.example.vvpweb.flexibleresourcemanagement;

import com.example.vvpcommom.ResponseResult;
import com.example.vvpcommom.UserLoginToken;
import com.example.vvpservice.exceloutput.service.IExcelOutPutService;
import com.example.vvpweb.BaseExcelController;
import com.example.vvpweb.flexibleresourcemanagement.model.AutoMonthLoadModel;
import com.example.vvpweb.flexibleresourcemanagement.model.LoadModelResponse;
import com.example.vvpweb.flexibleresourcemanagement.model.LoadMonthModelResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("/mainExcel")
@CrossOrigin
public class MainDataExcelController extends BaseExcelController {

    @Autowired
    private IExcelOutPutService iExcelOutPutService;

    @Autowired
    private MainController mainController;


    @UserLoginToken
    @RequestMapping(value = "getNearlyADayListExcel", method = {RequestMethod.POST})
    public void getNearlyADayListExcel(HttpServletResponse response) {
        try {
            ResponseResult<List<LoadModelResponse>> nearlyADayList = mainController.getNearlyADayList();
            exec(response, nearlyADayList.getData(), LoadModelResponse.class, iExcelOutPutService);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @UserLoginToken
    @RequestMapping(value = "getNearlySevenDaysListExcel", method = {RequestMethod.POST})
    public void getNearlySevenDaysListExcel(HttpServletResponse response) {
        try {
            ResponseResult<List<LoadModelResponse>> nearlyADayList = mainController.getNearlySevenDaysList();
            exec(response, nearlyADayList.getData(), LoadModelResponse.class, iExcelOutPutService);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @UserLoginToken
    @RequestMapping(value = "getNearlyAMonthListExcel", method = {RequestMethod.POST})
    public void getNearlyAMonthListExcel(HttpServletResponse response) {
        try {
            ResponseResult<List<LoadMonthModelResponse>> nearlyADayList = mainController.getNearlyAMonthList();
            exec(response, nearlyADayList.getData(), LoadMonthModelResponse.class, iExcelOutPutService);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @UserLoginToken
    @RequestMapping(value = "getAutoMonthListExcel", method = {RequestMethod.POST})
    public void getAutoMonthListExcel(HttpServletResponse response, @RequestBody AutoMonthLoadModel autoMonthLoadModel) {
        try {
            ResponseResult<List<LoadMonthModelResponse>> nearlyADayList = mainController.getAutoMonthList(autoMonthLoadModel);
            exec(response, nearlyADayList.getData(), LoadMonthModelResponse.class, iExcelOutPutService);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}
