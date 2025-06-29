package com.example.vvpservice.demand.service;
import java.util.regex.Matcher;

import com.example.vvpservice.demand.model.DemandRespStrategyModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * 需求响应策略服务实现类单元测试
 * 
 * @author VPP Team
 * @version 2.0.0
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("需求响应策略服务测试")
class DemandRespStrategyServiceImplTest {

    @Mock
    private EntityManager entityManager;

    @Mock
    private Query query;

    @InjectMocks
    private DemandRespStrategyServiceImpl demandRespStrategyService;

    private List<DemandRespStrategyModel> testData;

    @BeforeEach
    void setUp() {
        // 准备测试数据
        DemandRespStrategyModel model1 = new DemandRespStrategyModel();
        model1.setSId("test_s_id_1");
        model1.setRespId("test_resp_id_1");
        model1.setStrategyId("test_strategy_id_1");
        model1.setCreateBy("test_user");

        DemandRespStrategyModel model2 = new DemandRespStrategyModel();
        model2.setSId("test_s_id_2");
        model2.setRespId("test_resp_id_2");
        model2.setStrategyId("test_strategy_id_2");
        model2.setCreateBy("test_user");

        testData = Arrays.asList(model1, model2);
    }

    @Test
    @DisplayName("批量插入成功测试")
    void testBatchInsert_Success() {
        // Given
        when(entityManager.createNativeQuery(anyString())).thenReturn(query);
        when(query.executeUpdate()).thenReturn(2);

        // When
        boolean result = demandRespStrategyService.batchInsert(testData);

        // Then
        assertThat(result).isTrue();
        verify(entityManager, times(1)).createNativeQuery(anyString());
        verify(query, times(1)).executeUpdate();
    }

    @Test
    @DisplayName("批量插入空列表测试")
    void testBatchInsert_EmptyList() {
        // Given
        List<DemandRespStrategyModel> emptyList = Arrays.asList();

        // When
        boolean result = demandRespStrategyService.batchInsert(emptyList);

        // Then
        assertThat(result).isTrue();
        verify(entityManager, never()).createNativeQuery(anyString());
    }

    @Test
    @DisplayName("批量插入异常处理测试")
    void testBatchInsert_Exception() {
        // Given
        when(entityManager.createNativeQuery(anyString())).thenReturn(query);
        when(query.executeUpdate()).thenThrow(new RuntimeException("Database error"));

        // When
        boolean result = demandRespStrategyService.batchInsert(testData);

        // Then
        assertThat(result).isTrue(); // 即使异常，方法仍返回true
        verify(entityManager, times(1)).createNativeQuery(anyString());
        verify(query, times(1)).executeUpdate();
    }

    @Test
    @DisplayName("SQL生成正确性测试")
    void testBatchInsert_SqlGeneration() {
        // Given
        when(entityManager.createNativeQuery(anyString())).thenReturn(query);
        when(query.executeUpdate()).thenReturn(2);

        // When
        demandRespStrategyService.batchInsert(testData);

        // Then
        verify(entityManager).createNativeQuery(argThat(sql -> {
            String expectedSql = sql.toString();
            return expectedSql.contains("INSERT INTO demand_resp_strategy") &&
                   expectedSql.contains("test_s_id_1") &&
                   expectedSql.contains("test_resp_id_1") &&
                   expectedSql.contains("test_strategy_id_1") &&
                   expectedSql.contains("ON CONFLICT (s_id)") &&
                   expectedSql.contains("DO UPDATE SET");
        }));
    }

    @Test
    @DisplayName("单个记录插入测试")
    void testBatchInsert_SingleRecord() {
        // Given
        DemandRespStrategyModel singleModel = testData.get(0);
        List<DemandRespStrategyModel> singleList = Arrays.asList(singleModel);
        
        when(entityManager.createNativeQuery(anyString())).thenReturn(query);
        when(query.executeUpdate()).thenReturn(1);

        // When
        boolean result = demandRespStrategyService.batchInsert(singleList);

        // Then
        assertThat(result).isTrue();
        verify(entityManager, times(1)).createNativeQuery(anyString());
        verify(query, times(1)).executeUpdate();
    }

    @Test
    @DisplayName("Null值处理测试")
    void testBatchInsert_WithNullValues() {
        // Given
        DemandRespStrategyModel modelWithNulls = new DemandRespStrategyModel();
        modelWithNulls.setSId("test_id");
        modelWithNulls.setRespId(null);
        modelWithNulls.setStrategyId(null);
        modelWithNulls.setCreateBy(null);
        
        List<DemandRespStrategyModel> listWithNulls = Arrays.asList(modelWithNulls);
        
        when(entityManager.createNativeQuery(anyString())).thenReturn(query);
        when(query.executeUpdate()).thenReturn(1);

        // When
        boolean result = demandRespStrategyService.batchInsert(listWithNulls);

        // Then
        assertThat(result).isTrue();
        verify(entityManager, times(1)).createNativeQuery(anyString());
    }
} 