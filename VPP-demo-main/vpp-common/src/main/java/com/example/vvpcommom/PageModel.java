package com.example.vvpcommom;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Zhaoph 表格分页数据对象
 */
@Data
public class PageModel implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 每页大小
     */
    private int pageSize;
    /**
     * 每页显示记录数
     */
    private int number;
    /**
     * 总记页数
     */
    private int totalPages;
    /**
     * 总记录数
     */
    private int totalElements;

    /**
     * 列表数据
     */
    private List<?> content = new ArrayList();

}
