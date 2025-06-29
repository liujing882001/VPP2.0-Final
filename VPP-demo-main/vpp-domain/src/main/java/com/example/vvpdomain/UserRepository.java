package com.example.vvpdomain;

import com.example.vvpdomain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author zph
 * @description user
 * @date 2022-07-21
 */
@Repository
public interface UserRepository extends JpaRepository<User, String>, JpaSpecificationExecutor<User> {

    User findSystemUserByUserName(String userName);

    User findUserByUserNameAndUserPassword(String userName, String passWord);

    User findByUserId(String userId);

    User findUserByUserId(String userId);

//    @Query(value = "select * from sys_user where user_id =:userId", nativeQuery = true)
//    @Modifying
//    List<User> findPowerGridByUseId(@Param("userId") String userId);

    @Query(value = "delete from sys_user where user_id= :userId ", nativeQuery = true)
    @Modifying
    void deleteByUserId(@Param("userId") String userId);

    @Query(value = "UPDATE sys_user " +
            " SET power_grid = :powerGrid " +
            " WHERE user_id =:userId", nativeQuery = true)
    @Modifying
    void setPowerGridByUseId(@Param("userId") String userId, @Param("powerGrid") int powerGrid);

    @Query(value = "UPDATE sys_user " +
            " SET power_grid = :powerGrid ", nativeQuery = true)
    @Modifying
    void setPowerGrid(@Param("powerGrid") int powerGrid);
}