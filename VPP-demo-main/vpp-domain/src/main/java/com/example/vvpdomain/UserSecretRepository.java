package com.example.vvpdomain;

import com.example.vvpdomain.entity.UserSecret;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * @author bkq
 * @description user
 * @date 2024-12-24
 */
@Repository
public interface UserSecretRepository extends JpaRepository<UserSecret, String>, JpaSpecificationExecutor<UserSecret> {


}
