package com.example.vvpweb.demand;

import com.example.vvpcommom.Enum.CalendarTypeEnum;
import com.example.vvpcommom.Enum.DayOfWeekEnum;
import com.example.vvpcommom.ResponseResult;
import com.example.vvpcommom.TimeUtil;
import com.example.vvpcommom.UserLoginToken;
import com.example.vvpdomain.DemandCalendarRepository;
import com.example.vvpdomain.entity.DemandCalendar;
import com.example.vvpservice.demand.service.DemandCalendarService;
import com.example.vvpweb.demand.model.CalendarModel;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.*;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * @author maoyating
 * @description 需求响应-基线负荷
 * @date 2022-08-09
 */
@RestController
@RequestMapping("/demand_resp/calendar")
@CrossOrigin
@Api(value = "需求响应-基线负荷", tags = {"需求响应-基线负荷"})
public class DemandCalendarController {

    private static Logger logger = LoggerFactory.getLogger(DemandCalendarController.class);

    @Autowired
    private DemandCalendarRepository calendarRepository;
    @Autowired
    private DemandCalendarService demandCalendarService;

    @ApiOperation("查询基线负荷日期")
    @UserLoginToken
    @RequestMapping(value = "/getDateList", method = {RequestMethod.POST})
    public ResponseResult<List<DemandCalendar>> getDateList(@RequestParam("month")  String month) {
        if (StringUtils.isNotEmpty(month)) {
            String[] monthStr = month.split("-");
            Specification<DemandCalendar> spec = new Specification<DemandCalendar>() {
                @Override
                public Predicate toPredicate(Root<DemandCalendar> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder cb) {
                    List<Predicate> predicates = new ArrayList<>();
                    predicates.add(cb.equal(root.get("year"), Integer.valueOf(monthStr[0])));//年
                    predicates.add(cb.equal(root.get("month"), Integer.valueOf(monthStr[1])));//月
                    criteriaQuery.where(cb.and(predicates.toArray(new Predicate[predicates.size()])));

                    return criteriaQuery.getRestriction();
                }
            };
            List<DemandCalendar> list = calendarRepository.findAll(spec);
            return ResponseResult.success(list);
        }else{
            return ResponseResult.error("日期不能为空");
        }
    }

    @ApiOperation("转日期分类")
    @UserLoginToken
    @RequestMapping(value = "/convertDateStr", method = {RequestMethod.POST})
    public ResponseResult convertDateStr(@RequestBody List<DemandCalendar> list) {
        String respStr = "";
        if (list!=null && list.size()>0) {
            //根据属性分组
            Map<Integer,List<DemandCalendar>> listMap = list.stream()
                    .collect(Collectors.groupingBy(DemandCalendar::getDateType));
            for(Integer dateType : listMap.keySet()){
                Map<String,String> map = new HashMap<>();
                //获得属性标记的中文
                respStr += CalendarTypeEnum.getName(dateType)+";";

                //从小到大排序-》升序排序
                List<DemandCalendar> calendarList = listMap.get(dateType).stream()
                        .sorted(Comparator.comparing(DemandCalendar::getDate))
                        .collect(Collectors.toList());

                String convert = "";

                SimpleDateFormat fmt_ymd = new SimpleDateFormat("yyyy-MM-dd");
                fmt_ymd.setTimeZone(TimeZone.getTimeZone("GMT+8"));
                //最小
                DemandCalendar min=calendarList.get(0);
                convert= fmt_ymd.format(min.getDate())+"("+ DayOfWeekEnum.getName(min.getDayOfWeek()) +")";
                if(calendarList.size()>1){
                    //最大
                    DemandCalendar max=calendarList.get(calendarList.size()-1);

                    convert+="~"+fmt_ymd.format(max.getDate())+"("+ DayOfWeekEnum.getName(max.getDayOfWeek()) +")";
                }
                respStr+=convert+";";
            }
        }else{
            return ResponseResult.error("选中的日期不能为空");
        }
        return ResponseResult.success(respStr);

    }

    @ApiOperation("编辑基线负荷日期")
    @UserLoginToken
    @RequestMapping(value = "/editDateType", method = {RequestMethod.POST})
    public ResponseResult editDateType(@RequestBody CalendarModel model) {
        if (model.getDateList()==null || model.getDateList().size()==0) {
            return ResponseResult.error("编辑的日期不能为空");
        }
        if (model.getDateType()==null) {
            return ResponseResult.error("日期类型不能为空");
        }
        try {
            calendarRepository.updateDateType(model.getDateList(),model.getDateType());
        }catch (Exception e){
            e.printStackTrace();
            return ResponseResult.error("编辑基线负荷日期失败");
        }

        return ResponseResult.success();

    }

    @ApiOperation("根据日期手动生成基线")
    @UserLoginToken
    @RequestMapping(value = "/generateBaseline", method = {RequestMethod.POST})
    public ResponseResult generateBaseline(@RequestParam("baselineDate")  String baselineDate) {
        if (StringUtils.isNotEmpty(baselineDate)) {
            logger.info("重新生成基线负荷开始===="+baselineDate);
            Date date = TimeUtil.strDDToDate(baselineDate,"yyyy-MM-dd");
            demandCalendarService.generateBaseline(date);
            logger.info("重新生成基线负荷结束===="+baselineDate);
            return ResponseResult.success();
        }else{
            return ResponseResult.error("日期不能为空");
        }
    }

}