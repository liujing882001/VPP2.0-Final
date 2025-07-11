package com.example.vvpcommom.Enum;

/**
 * 需求响应任务状态 by myt 20240307
 */
public enum DemandStatusEnum {

    all(-1,"全部"),
    delete(0,"删除"),
    notStart(1, "未开始"),
    executing(2, "执行中"),
    completed(3, "已完成"),
    absent(4, "不参加");

    private Integer id;
    private String status;

    DemandStatusEnum(Integer id, String status) {
        this.id = id;
        this.status = status;
    }

    public Integer getId() {
        return id;
    }

    public String getStatus() {
        return status;
    }


    // 普通方法
    public static String getName(int id) {
        for (DemandStatusEnum c : DemandStatusEnum.values()) {
            if (c.getId() == id) {
                return c.status;
            }
        }
        return null;
    }
}
