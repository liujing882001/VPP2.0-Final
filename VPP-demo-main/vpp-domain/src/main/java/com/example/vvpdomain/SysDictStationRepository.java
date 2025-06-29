package com.example.vvpdomain;


import com.example.vvpdomain.entity.SysDictStation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface SysDictStationRepository extends JpaRepository<SysDictStation, String>, JpaSpecificationExecutor<SysDictStation> {

}
