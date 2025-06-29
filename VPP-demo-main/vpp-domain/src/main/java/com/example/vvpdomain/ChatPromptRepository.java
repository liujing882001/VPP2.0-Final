package com.example.vvpdomain;

import com.example.vvpdomain.entity.ChatPrompt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ChatPromptRepository extends JpaRepository<ChatPrompt, String>, JpaSpecificationExecutor<ChatPrompt> {

    @Query(value = "SELECT * FROM chat_prompt cp WHERE cp.query_type = :queryType LIMIT :queryCount", nativeQuery = true)
    List<ChatPrompt> findByQueryTypeAndLimit(@Param("queryType") String queryType, @Param("queryCount") int queryCount);
}