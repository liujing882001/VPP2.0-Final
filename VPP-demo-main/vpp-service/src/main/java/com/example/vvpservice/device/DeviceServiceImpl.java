package com.example.vvpservice.device;

import com.example.vvpdomain.DeviceRepository;
import com.example.vvpdomain.entity.Device;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

@Service
public class DeviceServiceImpl implements DeviceService {

    @Resource
    private DeviceRepository deviceRepository;

    @Override
    public Page<Device> devicePageByNodeId(String nodeId,Integer number,Integer pageSize) {
        Pageable pageable = PageRequest.of(number - 1, pageSize);
        Specification<Device> spec = (root, criteriaQuery, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("node").get("nodeId").as(String.class), nodeId));
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        return deviceRepository.findAll(spec, pageable);
    }
    @Override
    public Page<Device> devicePageByNodeIds(List<String> nodeIds, Integer number, Integer pageSize) {
        Pageable pageable = PageRequest.of(number - 1, pageSize);
        Specification<Device> spec = (root, criteriaQuery, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(root.get("node").get("nodeId").in(nodeIds));
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        return deviceRepository.findAll(spec, pageable);
    }
}
