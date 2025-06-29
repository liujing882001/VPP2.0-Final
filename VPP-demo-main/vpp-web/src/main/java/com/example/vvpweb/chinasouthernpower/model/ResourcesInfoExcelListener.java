package com.example.vvpweb.chinasouthernpower.model;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * excel 导入设备信息
 */
public class ResourcesInfoExcelListener extends AnalysisEventListener<ResourcesInfo> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ResourcesInfoExcelListener.class);
    /**
     * 每隔5条存储数据库，实际使用中可以3000条，然后清理list ，方便内存回收
     */
    private static final int BATCH_COUNT = 1000;
    private final List<ResourcesInfo> resourcesInfos = new ArrayList<>();


    /**
     * 这个每一条数据解析都会来调用

     */
    @Override
    public void invoke(ResourcesInfo resourcesInfo, AnalysisContext context) {

        if (resourcesInfo == null) {
            return;
        }
        resourcesInfos.add(resourcesInfo);

        // 达到BATCH_COUNT了，需要去存储一次数据库，防止数据几万条数据在内存，容易OOM
        if (resourcesInfos.size() >= BATCH_COUNT) {
            // 存储完成清理 list
            resourcesInfos.clear();
        }
    }


    /**
     * 所有数据解析完成了 都会来调用
     *
     * @param context
     */
    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        // 这里也要保存数据，确保最后遗留的数据也存储到数据库
        LOGGER.info("所有数据解析完成！");
    }

    public List<ResourcesInfo> getData(){
        return resourcesInfos;
    }



}