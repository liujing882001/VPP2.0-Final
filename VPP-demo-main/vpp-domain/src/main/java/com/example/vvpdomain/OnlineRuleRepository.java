package com.example.vvpdomain;

import com.example.vvpdomain.entity.OnlineRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface OnlineRuleRepository extends JpaRepository<OnlineRule, String>, JpaSpecificationExecutor<OnlineRule> {
	OnlineRule findByNodeId(String nodeId);
}
