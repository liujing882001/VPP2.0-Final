package com.example.vvpdomain;

import com.example.vvpdomain.entity.CfgStorageEnergyStrategyPower96Log;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface CfgStorageEnergyStrategyPower96LogRepository extends JpaRepository<CfgStorageEnergyStrategyPower96Log, String>, JpaSpecificationExecutor<CfgStorageEnergyStrategyPower96Log> {

}