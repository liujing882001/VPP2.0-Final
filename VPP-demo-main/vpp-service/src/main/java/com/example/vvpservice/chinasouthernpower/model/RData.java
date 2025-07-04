package com.example.vvpservice.chinasouthernpower.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Data
public class RData implements Serializable {

    @JsonProperty("P")
    @ApiModelProperty(value = "总有功功率(kW)", required = true)
    private Double P;
    @JsonProperty("Q")
    @ApiModelProperty(value = "总无功功率(kVar)", required = true)
    private Double Q;
    @JsonProperty("Pa")
    @ApiModelProperty(value = "A 相有功功率", required = true)
    private Double Pa;
    @JsonProperty("Pb")
    @ApiModelProperty(value = "B 相有功功率", required = true)
    private Double Pb;
    @JsonProperty("Pc")
    @ApiModelProperty(value = "C 相有功功率", required = true)
    private Double Pc;
    @JsonProperty("Qa")
    @ApiModelProperty(value = "A 相无功功率", required = true)
    private Double Qa;
    @JsonProperty("Qb")
    @ApiModelProperty(value = "B 相无功功率", required = true)
    private Double Qb;
    @JsonProperty("Qc")
    @ApiModelProperty(value = "C 相无功功率", required = true)
    private Double Qc;
    @JsonProperty("C")
    @ApiModelProperty(value = "计算电量", required = true)
    private Double C;
    @JsonProperty("Ua")
    @ApiModelProperty(value = "A 相电压", required = true)
    private Double Ua;
    @JsonProperty("Ub")
    @ApiModelProperty(value = "B 相电压", required = true)
    private Double Ub;
    @JsonProperty("Uc")
    @ApiModelProperty(value = "C 相电压", required = true)
    private Double Uc;
    @JsonProperty("Ia")
    @ApiModelProperty(value = "A 相电流", required = true)
    private Double Ia;
    @JsonProperty("Ib")
    @ApiModelProperty(value = "B 相电流", required = true)
    private Double Ib;
    @JsonProperty("Ic")
    @ApiModelProperty(value = "C 相电流", required = true)
    private Double Ic;
    @JsonProperty("Rb")
    @ApiModelProperty(value = "空调工作状态 1 制冷，0 制热", required = false)
    private Integer Rb;
    @JsonProperty("Rf")
    @ApiModelProperty(value = "变频水泵传输实时工作频率", required = false)
    private Double Rf;
    @JsonProperty("Rv")
    @ApiModelProperty(value = "光伏工作状态 1 并网发电，0 脱网不工作", required = false)
    private Integer Rv;
    @JsonProperty("Rc")
    @ApiModelProperty(value = "储能当前状态充电 1,放电 0", required = false)
    private Integer Rc;
    @JsonProperty("Soc")
    @ApiModelProperty(value = "储能电池 SOC 电池荷电状态", required = false)
    private Double Soc;
    @JsonProperty("Rm")
    @ApiModelProperty(value = "照明灯泡当前亮度（流明）", required = false)
    private Integer Rm;

    public Double getP() { return P; }
    public void setP(Double P) { this.P = P; }
    public Double getQ() { return Q; }
    public void setQ(Double Q) { this.Q = Q; }
    public Double getPa() { return Pa; }
    public void setPa(Double Pa) { this.Pa = Pa; }
    public Double getPb() { return Pb; }
    public void setPb(Double Pb) { this.Pb = Pb; }
    public Double getPc() { return Pc; }
    public void setPc(Double Pc) { this.Pc = Pc; }
    public Double getQa() { return Qa; }
    public void setQa(Double Qa) { this.Qa = Qa; }
    public Double getQb() { return Qb; }
    public void setQb(Double Qb) { this.Qb = Qb; }
    public Double getQc() { return Qc; }
    public void setQc(Double Qc) { this.Qc = Qc; }
    public Double getC() { return C; }
    public void setC(Double C) { this.C = C; }
    public Double getUa() { return Ua; }
    public void setUa(Double Ua) { this.Ua = Ua; }
    public Double getUb() { return Ub; }
    public void setUb(Double Ub) { this.Ub = Ub; }
    public Double getUc() { return Uc; }
    public void setUc(Double Uc) { this.Uc = Uc; }
    public Double getIa() { return Ia; }
    public void setIa(Double Ia) { this.Ia = Ia; }
    public Double getIb() { return Ib; }
    public void setIb(Double Ib) { this.Ib = Ib; }
    public Double getIc() { return Ic; }
    public void setIc(Double Ic) { this.Ic = Ic; }
    public Integer getRb() { return Rb; }
    public void setRb(Integer Rb) { this.Rb = Rb; }
    public Double getRf() { return Rf; }
    public void setRf(Double Rf) { this.Rf = Rf; }
    public Integer getRv() { return Rv; }
    public void setRv(Integer Rv) { this.Rv = Rv; }
    public Integer getRc() { return Rc; }
    public void setRc(Integer Rc) { this.Rc = Rc; }
    public Double getSoc() { return Soc; }
    public void setSoc(Double Soc) { this.Soc = Soc; }
    public Integer getRm() { return Rm; }
    public void setRm(Integer Rm) { this.Rm = Rm; }
}
