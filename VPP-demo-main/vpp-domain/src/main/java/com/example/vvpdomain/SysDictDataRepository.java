package com.example.vvpdomain;

import com.example.vvpdomain.entity.SysDictData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author zph
 * @description 参数配置表
 * @date 2022-07-01
 */
@Repository
public interface SysDictDataRepository extends JpaRepository<SysDictData, String>, JpaSpecificationExecutor<SysDictData> {

    List<SysDictData> findAllByModelKeyType(int model_key_type);

    List<SysDictData> findAllByModelKeyTypeAndModelKey(int model_key_type, String metering_device);

    SysDictData findByModelKey(String modeKey);
    @Query(value = "SELECT model_key FROM sys_dict_data ",nativeQuery = true)
    List<String> findAllModelKey();
}