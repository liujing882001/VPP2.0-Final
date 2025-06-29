package com.example.vvpdomain;

import com.example.vvpdomain.entity.AliyunMessageLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AliyunMessageLogRepository extends JpaRepository<AliyunMessageLog,String> {
}
