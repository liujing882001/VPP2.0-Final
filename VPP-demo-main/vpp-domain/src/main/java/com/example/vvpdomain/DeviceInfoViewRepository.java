package com.example.vvpdomain;

import com.example.vvpdomain.view.DeviceInfoView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface DeviceInfoViewRepository extends JpaRepository<DeviceInfoView, String>, JpaSpecificationExecutor<DeviceInfoView> {


    @Query(value = "SELECT sum(device_real_power) from device_info_view WHERE config_key='other'  AND load_type='air_conditioning'",nativeQuery = true)
    double getAirConditioning();

    @Query(value = "SELECT sum(device_real_power) from device_info_view WHERE config_key='other'  AND load_type='lighting'",nativeQuery = true)
    double getLighting();

    @Query(value = "SELECT sum(device_real_power) from device_info_view WHERE config_key='other'  AND load_type='charging_piles'",nativeQuery = true)
    double getChargingPiles();

    @Query(value = "SELECT sum(device_real_power) from device_info_view WHERE config_key='other'  AND load_type='others'",nativeQuery = true)
    double getOthers();
    @Query(value = "SELECT SUM\n" +
            "\t( device_real_power ) AS device_real_power \n" +
            "FROM\n" +
            "\tdevice_info_view \n" +
            "WHERE\n" +
            "\tconfig_key = 'other' \n" +
            "\tAnd load_type=:loadType\n" +
            "\tAND device_sn IN (:deviceSnItems)\n" +
            "\t",nativeQuery = true)
    double findDeviceLoadByDeviceSnAndLoadType( @Param("deviceSnItems") Collection<String> deviceSnItems
                                                ,@Param("loadType") String loadType);
}
