package com.example.vvpdomain;

import com.example.vvpdomain.view.StorageEnergyNodeInfoView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface StorageEnergyNodeInfoViewRepository extends JpaRepository<StorageEnergyNodeInfoView, String>, JpaSpecificationExecutor<StorageEnergyNodeInfoView> {
}
