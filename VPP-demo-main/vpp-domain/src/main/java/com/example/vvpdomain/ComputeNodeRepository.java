package com.example.vvpdomain;

import com.example.vvpdomain.entity.ComputeNode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;


@Repository
public interface ComputeNodeRepository extends JpaRepository<ComputeNode, String>, JpaSpecificationExecutor<ComputeNode> {

}
