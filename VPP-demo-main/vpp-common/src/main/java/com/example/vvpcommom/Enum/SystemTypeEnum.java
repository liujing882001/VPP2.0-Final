package com.example.vvpcommom.Enum;

public enum SystemTypeEnum {

    beiyongdianchixitong("beiyongdianchixitong", "备用电池系统"),
    chongdianzhuangxitong("chongdianzhuangxitong", "充电桩系统"),
    chuneng("chuneng", "储能系统"),
    guangfu("guangfu", "光伏系统"),
    huandianzhanxitong("huandianzhanxitong", "换电站系统"),
    kongtiao("kongtiao", "空调系统"),
    nengyuanzongbiao("nengyuanzongbiao", "能源总表"),
    zhaoming("zhaoming", "照明系统");

    private String id;
    private String name;

    SystemTypeEnum(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
