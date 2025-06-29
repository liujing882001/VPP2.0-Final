package com.example.vvpdomain.alarm.rule;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlarmRuleRepository extends JpaRepository<AlarmRule, String>, JpaSpecificationExecutor<AlarmRule> {


	List<AlarmRule> findAllByAlarmTypeAndEnable(int type,boolean enable);
}
