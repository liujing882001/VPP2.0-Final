package com.example.vvpweb.alarmmanagement;

import com.example.vvpcommom.Enum.AlarmSeverityEnum;
import com.example.vvpcommom.Enum.AlarmStatusEnum;
import com.example.vvpcommom.PageModel;
import com.example.vvpcommom.ResponseResult;
import com.example.vvpcommom.TimeUtil;
import com.example.vvpcommom.UserLoginToken;
import com.example.vvpdomain.AlarmLogRepository;
import com.example.vvpdomain.entity.AlarmLog;
import com.example.vvpweb.alarmmanagement.model.AlarmModel;
import com.example.vvpweb.alarmmanagement.model.AlarmSeverityResponse;
import com.example.vvpweb.alarmmanagement.model.AlarmStatusResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author zph
 * @description
 * @date 2022-06-06
 */
@RestController
@RequestMapping("/alarm_management/alarm")
@CrossOrigin
@Api(value = "报警管理", tags = {"报警管理"})
public class AlarmController {

    private static Logger logger = LoggerFactory.getLogger(AlarmController.class);
    @Resource
    private AlarmLogRepository alarmRepository;


    /**
     * 报警等级
     */
    @ApiOperation("获取所有报警等级")
    @UserLoginToken
    @RequestMapping(value = "severityList", method = {RequestMethod.POST})
    public ResponseResult<List<AlarmSeverityResponse>> severityList() {

        List<AlarmSeverityResponse> list = new ArrayList<>();
        for (AlarmSeverityEnum severityEnum : EnumSet.allOf(AlarmSeverityEnum.class)) {
            AlarmSeverityResponse severityRep = new AlarmSeverityResponse();
            severityRep.setSeverity(severityEnum.getId());
            severityRep.setSeverityDesc(severityEnum.getDesc());
            list.add(severityRep);
        }
        return ResponseResult.success(list);
    }


    /**
     * 报警状态
     */
    @ApiOperation("获取所有报警状态")
    @UserLoginToken
    @RequestMapping(value = "statusList", method = {RequestMethod.POST})
    public ResponseResult<List<AlarmStatusResponse>> statusList() {
        List<AlarmStatusResponse> list = new ArrayList<>();

        for (AlarmStatusEnum stEnum : EnumSet.allOf(AlarmStatusEnum.class)) {
            AlarmStatusResponse statusRe = new AlarmStatusResponse();
            statusRe.setStatus(stEnum.getId());
            statusRe.setStatusDesc(stEnum.getDesc());
            list.add(statusRe);
        }
        return ResponseResult.success(list);
    }


    /**
     * 分页查询
     */
    @ApiOperation("分页查询获取设备报警信息")
    @UserLoginToken
    @RequestMapping(value = "list", method = {RequestMethod.POST})
    public ResponseResult<PageModel> alarmLogList(@RequestBody AlarmModel alarmModel) throws ParseException {

        SimpleDateFormat ymd = new SimpleDateFormat("yyyy-MM-dd");
        // 国内时区是GMT+8
        ymd.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        SimpleDateFormat ymd_hms = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        // 国内时区是GMT+8
        ymd_hms.setTimeZone(TimeZone.getTimeZone("GMT+8"));


        String dts = ymd.format(TimeUtil.getMonthStart(alarmModel.getStartTs())) + " 00:00:00";
        String dte = ymd.format(TimeUtil.getMonthEnd(alarmModel.getEndTs())) + " 23:59:59";

        Date date_s = ymd_hms.parse(dts);
        Date date_e = ymd_hms.parse(dte);

        Specification<AlarmLog> spec = new Specification<AlarmLog>() {
            @Override
            public Predicate toPredicate(Root<AlarmLog> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<>();//使用集合可以应对多字段查询的情况
                if (StringUtils.isNotEmpty(alarmModel.getNodeId()) && alarmModel.getNodeId().equals("-1") == false) {
                    predicates.add(cb.equal(root.get("nodeId"), alarmModel.getNodeId()));//对应SQL语句：select * from ### where username= code
                }
                if (alarmModel.getSeverity() != -1) {
                    predicates.add(cb.equal(root.get("severity"), alarmModel.getSeverity()));
                }
                if (alarmModel.getStatus() != -1) {
                    predicates.add(cb.equal(root.get("status"), alarmModel.getStatus()));
                }
                predicates.add(cb.between(root.get("startTs"), date_s, date_e));
                return cb.and(predicates.toArray(new Predicate[predicates.size()]));//以and的形式拼接查询条件，也可以用.or()
            }
        };

        //传入分页条件和排序条件拿到分页对象
        int page = alarmModel.getNumber();
        int size = alarmModel.getPageSize();
        Pageable pageable = PageRequest.of(page - 1, size, Sort.Direction.ASC, "startTs");

        Page<AlarmLog> datas = alarmRepository.findAll(spec, pageable);

        PageModel pageModel = new PageModel();
        //封装到pageUtil
        pageModel.setContent(datas.getContent());
        pageModel.setTotalPages(datas.getTotalPages());
        pageModel.setTotalElements((int) datas.getTotalElements());
        pageModel.setNumber(datas.getNumber() + 1);

        return ResponseResult.success(pageModel);
    }
}