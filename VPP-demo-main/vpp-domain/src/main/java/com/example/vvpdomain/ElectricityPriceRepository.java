package com.example.vvpdomain;

import com.example.vvpdomain.dto.RAPriceDTO;
import com.example.vvpdomain.entity.ElectricityPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ElectricityPriceRepository extends JpaRepository<ElectricityPrice, String>, JpaSpecificationExecutor<ElectricityPrice> {

    @Query(value = "SELECT * FROM electricity_price WHERE node_id IN (:nodeIds) AND effective_date >= :effectiveDate AND price_use = :priceUse ", nativeQuery = true)
    List<ElectricityPrice> findAllByNodeIdAndEffectiveDate(@Param("nodeIds") List<String> nodeIds, @Param("effectiveDate") LocalDate effectiveDate, @Param("priceUse") String priceUse);

    @Query(value = "SELECT * FROM electricity_price WHERE node_id IN (:nodeIds) AND effective_date BETWEEN :sDate AND :eDate ", nativeQuery = true)
    List<ElectricityPrice> findAllByNodeIdsAndDate(@Param("nodeIds") List<String> nodeIds, @Param("sDate") LocalDate sDate, @Param("eDate") LocalDate eDate);

    @Query("SELECT new com.example.vvpdomain.dto.RAPriceDTO(t.effectiveDate, t.sTime, MAX(t.price),SUM(t.strategy)) " +
            "FROM ElectricityPrice t " +
            "WHERE t.nodeId IN :nodeIds AND t.effectiveDate BETWEEN :sDate AND :eDate " +
            "GROUP BY t.effectiveDate, t.sTime")
    List<RAPriceDTO> findRAPriceDTOByNodeIdsAndDate(@Param("nodeIds") List<String> nodeIds,
                                                    @Param("sDate") LocalDateTime sDate,
                                                    @Param("eDate") LocalDateTime eDate);

    @Query(value = "SELECT * FROM electricity_price WHERE node_id IN (:nodeIds) AND price_use = :priceUse ", nativeQuery = true)
    List<ElectricityPrice> findAllByNodeIdsAndPrice_use(@Param("nodeIds") List<String> nodeIds, @Param("priceUse") String priceUse);
    @Query(value = "SELECT * FROM electricity_price WHERE node_id = :nodeId ", nativeQuery = true)
    List<ElectricityPrice> findAllByNodeId(@Param("nodeId") String nodeId);

    List<ElectricityPrice> findAllByNodeIdAndEffectiveDate(String nodeId, LocalDateTime effectiveDate);

    @Transactional
    void deleteAllByNodeIdAndEffectiveDate(String nodeId,LocalDateTime effectiveDate);
}