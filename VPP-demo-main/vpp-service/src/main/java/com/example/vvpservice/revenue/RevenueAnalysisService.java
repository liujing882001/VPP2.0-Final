package com.example.vvpservice.revenue;
import java.util.Date;
import java.time.LocalDate;

import com.example.vvpdomain.dto.RAEnergyBaseDTO;
import com.example.vvpdomain.dto.RAPriceDTO;
import com.example.vvpservice.revenue.model.RAInfoDTO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface RevenueAnalysisService {
    List<RAInfoDTO> revenueAnalysis(Map<String, Map<String, Double>> socMap,
                                    Map<String, Map<String, RAPriceDTO>> priceMap,
                                    RAEnergyBaseDTO energyBaseInfoMap,
                                    Map<String, Double> dateProfitMap,
                                    Map<Long, Double> loadMap,
                                    LocalDateTime eLocalDate
    );
}
