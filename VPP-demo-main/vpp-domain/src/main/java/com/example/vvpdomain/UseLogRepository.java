//package com.example.vvpdomain;
//
//import com.example.vvpdomain.entity.UseLog;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
//import org.springframework.data.jpa.repository.Modifying;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.data.repository.query.Param;
//
//import javax.transaction.Transactional;
//import java.util.Date;
//
//public interface UseLogRepository extends JpaRepository<UseLog, String>, JpaSpecificationExecutor<UseLog> {
//    @Query(value = "UPDATE user_log set logout_time=:date WHERE user_phone=:phone ", nativeQuery = true)
//    @Transactional
//    @Modifying
//    void updateLogoutTime(@Param("phone") String phone, @Param("date") Date date);
//}
