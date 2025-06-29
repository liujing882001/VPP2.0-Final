package com.example.vvpservice.usernode.service;

import com.example.vvpcommom.RequestHeaderContext;
import com.example.vvpdomain.*;
import com.example.vvpdomain.entity.*;
import com.example.vvpservice.prouser.service.IUserService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

@Service
public class PageableServiceImpl implements IPageableService {

    private static Logger logger = LoggerFactory.getLogger(PageableServiceImpl.class);

    @Autowired
    private UserRepository sysUserRepository;

    @Autowired
    private NodeRepository nodeRepository;

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private ScheduleStrategyRepository scheduleStrategyRepository;

    @Autowired
    private ScheduleStrategyViewRepository scheduleStrategyViewRepository;

    @Autowired
    private IUserService userService;
    @Resource
    private DevicePointRepository devicePointRepository;

    @Override
    public Page<User> getUserLikeUserName(String userName, int number, int pageSize) {
        Pageable pageable = PageRequest.of(number - 1, pageSize);

        Specification<User> spec = new Specification<User>() {
            @Override
            public Predicate toPredicate(Root<User> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<>();//使用集合可以应对多字段查询的情况
                if (StringUtils.isNotEmpty(userName)) {
                    predicates.add(cb.like(root.get("userName").as(String.class), "%" + userName + "%"));//对应SQL语句：select * from ### where username= code
                }
                Predicate[] predicate = new Predicate[predicates.size()];
                criteriaQuery.where(cb.and(predicates.toArray(predicate)));
                criteriaQuery.orderBy(cb.asc(root.get("userName").as(String.class)));
                return criteriaQuery.getRestriction();
            }
        };
        return sysUserRepository.findAll(spec, pageable);
    }

    @Override
    public Page<Node> getNodeLikeNodeName(String nodeName, int number, int pageSize) {
        Pageable pageable = PageRequest.of(number - 1, pageSize);

        List<String> allowNodeIds = userService.getAllowNodeIds();


        Specification<Node> spec = new Specification<Node>() {
            @Override
            public Predicate toPredicate(Root<Node> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<>();//使用集合可以应对多字段查询的情况
                if (StringUtils.isNotEmpty(nodeName)) {
                    predicates.add(cb.like(root.get("nodeName").as(String.class), "%" + nodeName + "%"));//对应SQL语句：select * from ### where username= code
                }
                if (allowNodeIds != null && !allowNodeIds.isEmpty()) {
                    predicates.add(cb.in(root.get("nodeId")).value(allowNodeIds));
                }

                Predicate[] predicate = new Predicate[predicates.size()];
                criteriaQuery.where(cb.and(predicates.toArray(predicate)));
                criteriaQuery.orderBy(cb.desc(root.get("createdTime").as(String.class)));
                return criteriaQuery.getRestriction();
            }
        };
        return nodeRepository.findAll(spec, pageable);
    }

    @Override
    public Page<DevicePoint> getPointByNodeIdOrSystemIdOrDeviceId(String nodeId, String systemId, String deviceId, int number, int pageSize) {
        Pageable pageable = PageRequest.of(number - 1, pageSize);

        Specification<DevicePoint> spec = new Specification<DevicePoint>() {
            @Override
            public Predicate toPredicate(Root<DevicePoint> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<>();//使用集合可以应对多字段查询的情况
                if (StringUtils.isNotEmpty(deviceId)) {
                    predicates.add(cb.equal(root.get("device").get("deviceId").as(String.class), deviceId));//对应SQL语句：select * from ### where username= code
                } else {
                    if (!StringUtils.isNotEmpty(nodeId)) {
                        List<String> allowNodeIds = userService.getAllowNodeIds();
                        predicates.add(cb.in(root.get("device").get("node").get("nodeId")).value(allowNodeIds));
                    } else {
                        if (StringUtils.isNotEmpty(nodeId)) {
                            predicates.add(cb.equal(root.get("device").get("node").get("nodeId").as(String.class), nodeId));

                            if (StringUtils.isNotEmpty(systemId)) {
                                predicates.add(cb.equal(root.get("device").get("systemType").get("systemId").as(String.class), systemId));
                            }
                        }
                    }

                }

                Predicate[] predicate = new Predicate[predicates.size()];
                criteriaQuery.where(cb.and(predicates.toArray(predicate)));
                criteriaQuery.orderBy(cb.desc(root.get("pointSn").as(String.class)));
                return criteriaQuery.getRestriction();
            }
        };

        Page<DevicePoint> all = devicePointRepository.findAll(spec, pageable);

        return all;
    }

    @Override
    public List<Node> getPermissionNodes() {

        return nodeRepository.findAllByNodeIdIn(userService.getAllowNodeIds());
    }

    @Override
    public Node getPermissionNode(String nodeId) {

        if (userService.getAllowNodeIds().contains(nodeId)) {
            return nodeRepository.findByNodeId(nodeId);
        }

        throw new IllegalArgumentException("用户权限下的节点" + nodeId + "不存在");
    }

    @Override
    public Page<ScheduleStrategy> getStrategyLikeStrategyName(String strategyName, int number, int pageSize) {

        Pageable pageable = PageRequest.of(number - 1, pageSize, Sort.by("createdTime").descending());

        Specification<ScheduleStrategy> spec = new Specification<ScheduleStrategy>() {
            @Override
            public Predicate toPredicate(Root<ScheduleStrategy> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<>();//使用集合可以应对多字段查询的情况
                if (StringUtils.isNotEmpty(strategyName)) {
                    predicates.add(cb.like(root.get("strategyName").as(String.class), "%" + strategyName + "%"));//对应SQL语句：select * from ### where username= code
                }

                if (!userService.isManger()) {
                    String userId = RequestHeaderContext.getInstance().getUserId();
                    predicates.add(cb.in(root.get("userId")).value(userId));
                }

                return cb.and(predicates.toArray(new Predicate[predicates.size()]));//以and的形式拼接查询条件，也可以用.or()
            }
        };
        return scheduleStrategyRepository.findAll(spec, pageable);
    }

    @Override
    public Page<ScheduleStrategyView> getStrategyDeviceByStrategyId(String strategyId, String deviceName, int number, int pageSize) {
        Pageable pageable = PageRequest.of(number - 1, pageSize, Sort.by("createdTime").descending());

        Specification<ScheduleStrategyView> spec = new Specification<ScheduleStrategyView>() {
            @Override
            public Predicate toPredicate(Root<ScheduleStrategyView> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder cb) {

                List<Predicate> predicates = new ArrayList<>();//使用集合可以应对多字段查询的情况
                predicates.add(cb.equal(root.get("strategyId").as(String.class), strategyId));
                if (StringUtils.isNotEmpty(deviceName)) {
                    predicates.add(cb.like(root.get("deviceName").as(String.class), "%" + deviceName + "%"));//对应SQL语句：select * from ### where username= code
                }


                return cb.and(predicates.toArray(new Predicate[predicates.size()]));//以and的形式拼接查询条件，也可以用.or()
            }
        };
        return scheduleStrategyViewRepository.findAll(spec, pageable);
    }

    @Override
    public Page<ScheduleStrategyView> getStrategyDeviceByStrategyId(String strategyId, String deviceName, int number, int pageSize, boolean on) {
        Pageable pageable = PageRequest.of(number - 1, pageSize, Sort.by("createdTime").descending());

        Specification<ScheduleStrategyView> spec = new Specification<ScheduleStrategyView>() {
            @Override
            public Predicate toPredicate(Root<ScheduleStrategyView> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder cb) {

                List<Predicate> predicates = new ArrayList<>();//使用集合可以应对多字段查询的情况
                predicates.add(cb.equal(root.get("strategyId").as(String.class), strategyId));

                predicates.add(cb.equal(root.get("online").as(Boolean.class), on));

                if (StringUtils.isNotEmpty(deviceName)) {
                    predicates.add(cb.like(root.get("deviceName").as(String.class), "%" + deviceName + "%"));//对应SQL语句：select * from ### where username= code
                }

                return cb.and(predicates.toArray(new Predicate[predicates.size()]));//以and的形式拼接查询条件，也可以用.or()
            }
        };
        return scheduleStrategyViewRepository.findAll(spec, pageable);
    }

    @Override
    public Page<Device> getDeviceByNodeIdAndSystemId(String nodeId, String systemId, int number, int pageSize) {
        Pageable pageable = PageRequest.of(number - 1, pageSize, Sort.by("createdTime").descending());

        Specification<Device> spec = new Specification<Device>() {
            @Override
            public Predicate toPredicate(Root<Device> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder cb) {

                List<Predicate> predicates = new ArrayList<>();//使用集合可以应对多字段查询的情况
                predicates.add(cb.equal(root.get("node").get("nodeId").as(String.class), nodeId));

                if (StringUtils.isNotEmpty(systemId)) {
                    {
                        predicates.add(cb.equal(root.get("systemType").get("systemId").as(String.class), systemId));

                    }
                }

                return cb.and(predicates.toArray(new Predicate[predicates.size()]));//以and的形式拼接查询条件，也可以用.or()
            }
        };
        return deviceRepository.findAll(spec, pageable);
    }

    @Override
    public Page<Device> getDeviceLikeDeviceName(String deviceName, int number, int pageSize) {
        Pageable pageable = PageRequest.of(number - 1, pageSize, Sort.by("createdTime").descending());

        Specification<Device> spec = new Specification<Device>() {
            @Override
            public Predicate toPredicate(Root<Device> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder cb) {

                List<Predicate> predicates = new ArrayList<>();//使用集合可以应对多字段查询的情况
                predicates.add(cb.like(root.get("deviceName").as(String.class), "%" + deviceName + "%"));

                return cb.and(predicates.toArray(new Predicate[predicates.size()]));//以and的形式拼接查询条件，也可以用.or()
            }
        };
        return deviceRepository.findAll(spec, pageable);

    }

    @Override
    public Page<Role> getRoleLikeRoleName(String roleName, int number, int pageSize) {

        Pageable pageable = PageRequest.of(number - 1, pageSize, Sort.by("createdTime").descending());

        Specification<Role> spec = new Specification<Role>() {
            @Override
            public Predicate toPredicate(Root<Role> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder cb) {

                List<Predicate> predicates = new ArrayList<>();//使用集合可以应对多字段查询的情况

                if (StringUtils.isNotEmpty(roleName)) {
                    predicates.add(cb.like(root.get("roleName").as(String.class), "%" + roleName + "%"));
                }


                return cb.and(predicates.toArray(new Predicate[predicates.size()]));//以and的形式拼接查询条件，也可以用.or()
            }
        };
        return roleRepository.findAll(spec, pageable);

    }
}
