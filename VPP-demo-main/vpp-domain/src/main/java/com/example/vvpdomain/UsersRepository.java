//package com.example.vvpdomain;
//
//import com.example.vvpdomain.entity.UserLogin;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.data.repository.query.Param;
//import org.springframework.stereotype.Repository;
//
//@Repository
//public interface UsersRepository extends JpaRepository<UserLogin, String>, JpaSpecificationExecutor<UserLogin> {
//    @Query(value = "select * from users where phone=:phone", nativeQuery = true)
//    UserLogin findUsersByPhone(@Param("phone") String phone);
//}
