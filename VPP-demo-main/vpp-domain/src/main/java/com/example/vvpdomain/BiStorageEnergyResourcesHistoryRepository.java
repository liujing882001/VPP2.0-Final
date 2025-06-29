package com.example.vvpdomain;

import com.example.vvpdomain.entity.BiStorageEnergyResourcesHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BiStorageEnergyResourcesHistoryRepository extends JpaRepository<BiStorageEnergyResourcesHistory, String>, JpaSpecificationExecutor<BiStorageEnergyResourcesHistory> {

    List<BiStorageEnergyResourcesHistory> findTop10ByOrderByUpdateTimeDesc();
}