package com.example.vvpdomain;

import com.example.vvpdomain.entity.ApplicationLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApplicationLogRepository extends JpaRepository<ApplicationLog, String>,
		JpaSpecificationExecutor<ApplicationLog> {

	ApplicationLog findByUserIdAndApplicationName(String userId,String name);

	List<ApplicationLog> findAllByUserId(String userId);
}
