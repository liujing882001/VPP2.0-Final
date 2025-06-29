package com.example.vvpservice.externalapi.model;

import com.example.vvpdomain.entity.PointModel;
import com.example.vvpdomain.entity.PointModelMapping;
import lombok.Data;

@Data
public class DataType {
    private String dataId;
    private String dataNameEn;
    private String dataNameZh;
    private String calcFormula;

    public DataType(){}
    public DataType(PointModelMapping v, PointModel pointModel){
        this.dataId = v.getMappingId();
        this.dataNameEn = pointModel.getPointNameEn();
        this.dataNameZh = pointModel.getPointNameZh();
        this.calcFormula = v.getCalculation_formula();

    }

}
