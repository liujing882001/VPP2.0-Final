package com.example.vvpdomain;

import com.example.vvpdomain.dto.DateProfitDTO;
import com.example.vvpdomain.entity.IotTsKvMeteringDevice96;
import com.example.vvpdomain.entity.NodeProfit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;


/**
 * @author zph
 * @description node
 * @date 2022-07-01
 */
@Repository
public interface NodeProfitRepository extends JpaRepository<NodeProfit, String>, JpaSpecificationExecutor<NodeProfit> {


    @Query(value = "SELECT * FROM node_profit WHERE node_id IN :nodeIds AND profit_date BETWEEN :start AND :end ", nativeQuery = true)
    List<NodeProfit> findByNodeIdInAndProfitDateBetween(
            @Param("nodeIds") List<String> nodeIds,
            @Param("start") Date start,
            @Param("end") Date end
    );

    @Query("SELECT new com.example.vvpdomain.dto.DateProfitDTO(np.profitDate, " +
            "SUM((np.outElectricityHigh - np.inElectricityHigh) * np.priceHigh + " +
            "(np.outElectricityPeak - np.inElectricityPeak) * np.pricePeak + " +
            "(np.outElectricityStable - np.inElectricityStable) * np.priceStable + " +
            "(np.outElectricityLow - np.inElectricityLow) * np.priceLow)) " +
//            "+ (np.inElectricityRavine - np.outElectricityRavine) * np.priceRavine" +
            "FROM NodeProfit np " +
            "WHERE np.nodeId IN :nodeIds " +
            "AND np.profitDate BETWEEN :startDate AND :endDate " +
            "GROUP BY np.profitDate")
    List<DateProfitDTO> calculateTotalProfit(@Param("nodeIds") List<String> nodeIds,
                                             @Param("startDate") Date startDate,
                                             @Param("endDate") Date endDate);
//    @Query("SELECT new com.example.vvpdomain.dto.DateProfitDTO(np.profitDate, " +
//            "SUM((np.outElectricityHigh - np.inElectricityHigh) * np.priceHigh + " +
//            "(np.outElectricityPeak - np.inElectricityPeak) * np.pricePeak + " +
//            "(np.outElectricityStable - np.inElectricityStable) * np.priceStable + " +
//            "(np.outElectricityLow - np.inElectricityLow) * np.priceLow + " +
//            "(np.outElectricityRavine - np.inElectricityRavine) * np.priceRavine)" +
//            ") " +
//            "FROM NodeProfit np " +
//            "WHERE np.nodeId IN :nodeIds " +
//            "AND np.profitDate BETWEEN :startDate AND :endDate " +
//            "GROUP BY np.profitDate")
//    List<DateProfitDTO> calculateTotalProfit(@Param("nodeIds") List<String> nodeIds,
//                                             @Param("startDate") Date startDate,
//                                             @Param("endDate") Date endDate);

    @Query("SELECT new com.example.vvpdomain.dto.DateProfitDTO(np.profitDate, " +
            "SUM((np.outElectricityHigh - np.inElectricityHigh) * np.priceHigh + " +
            "(np.outElectricityPeak - np.inElectricityPeak) * np.pricePeak + " +
            "(np.outElectricityStable - np.inElectricityStable) * np.priceStable + " +
            "(np.outElectricityLow - np.inElectricityLow) * np.priceLow " +
//            "+ (np.outElectricityRavine - np.inElectricityRavine) * np.priceRavine" +
            ")) " +
            "FROM NodeProfit np " +
            "WHERE np.nodeId = :nodeId " +
            "AND np.profitDate BETWEEN :startDate AND :endDate " +
            "GROUP BY np.profitDate")
    List<DateProfitDTO> calculateTotalProfit(@Param("nodeId") String nodeId,
                                             @Param("startDate") Date startDate,
                                             @Param("endDate") Date endDate);



    List<NodeProfit> findAllByNodeIdAndProfitDateBetweenOrderByProfitDateAsc(String nodeId, Date start, Date end);


    List<NodeProfit> findAllByProfitDateBetweenOrderByProfitDateAsc(Date start, Date end);

    @Query(value = "SELECT node_id FROM node_profit GROUP BY node_id",nativeQuery = true)
    List<String> findAllGroupByNodeId();

    @Query(value = "SELECT\n" +
            "\t*\n" +
            "FROM\n" +
            "\tnode_profit \n" +
            "WHERE\n" +
            "\tnode_id = :nodeId \n" +
            "\tAND created_time >= :startDate\n" +
            "\tAND created_time <= :endDate\n",nativeQuery = true)
    List<NodeProfit> findAllByNodeIdAndDate(@Param("nodeId") String nodeId, @Param("startDate") Date startDate, @Param("endDate") Date endDate);


}