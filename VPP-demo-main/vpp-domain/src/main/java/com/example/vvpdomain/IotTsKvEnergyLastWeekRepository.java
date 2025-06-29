package com.example.vvpdomain;

import com.example.vvpdomain.entity.IotTsKvLast;
import com.example.vvpdomain.view.IotTsKvEnergyLastWeekView;
import com.example.vvpdomain.view.IotTsKvLoadLastWeekView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;


@Repository
public interface IotTsKvEnergyLastWeekRepository extends JpaRepository<IotTsKvEnergyLastWeekView, String>, JpaSpecificationExecutor<IotTsKvEnergyLastWeekView> {

    @Query(value = "SELECT\n" +
            "\t* \n" +
            "FROM\n" +
            "\tiot_ts_kv_energy_lastweek_view \n" +
            "WHERE\n" +
            "\tpoint_desc = 'energy' \n" +
            "\tAND ts >= ( CURRENT_DATE - INTERVAL '3 days' ) \n" +
            "\tAND ts <= NOW( ) \n" +
            "ORDER BY\n" +
            "\tts", nativeQuery = true)
    List<IotTsKvEnergyLastWeekView> findAllEnergyInterval3days();
}
