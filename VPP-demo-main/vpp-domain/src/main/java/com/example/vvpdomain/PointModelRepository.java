package com.example.vvpdomain;

import com.example.vvpdomain.entity.PointModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface PointModelRepository extends JpaRepository<PointModel, String>, JpaSpecificationExecutor<PointModel> {

}
