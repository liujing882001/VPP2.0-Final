package com.example.vvpcommom;

import java.util.ArrayList;
import java.util.List;

/**
 * 分页
 */
public class ListUtils {
    /**
     *
     * @param pageSize  当前页面大小
     * @param pageIndex  当前页码
     * @param list  需要分页的集合
     * @return 当前展示页内容
     */
    public static<T> List<T>  Pager(int pageSize, int pageIndex, List<T> list){
        //使用list 中的sublist方法分页
        List<T> dataList = new ArrayList<>();
        // 每页显示多少条记录

        int currentPage; //当前第几页数据
        // 一共多少条记录
        int totalRecord = list.size();
        // 一共多少页
        int totalPage = totalRecord % pageSize;
        if (totalPage > 0) {
            totalPage = totalRecord / pageSize + 1;
        } else {
            totalPage = totalRecord / pageSize;
        }

        System.out.println("总页数:" + totalPage);

        // 当前第几页数据
        currentPage = totalPage < pageIndex ? totalPage : pageIndex;

        // 起始索引
        int fromIndex = pageSize * (currentPage - 1);

        // 结束索引
        int toIndex = pageSize * currentPage > totalRecord ? totalRecord : pageSize * currentPage;
        try{
            dataList = list.subList(fromIndex, toIndex);
        }catch(IndexOutOfBoundsException e){
            e.printStackTrace();
        }
        return dataList;
    }

    /**
     * @description 获取当前页数
     * @method getCurrentPage
     * @param pageSize
     * @param pageIndex
     * @param list
     * @return int
     */
    public static<T> int  getCurrentPage(int pageSize, int pageIndex, List<T> list){
        //使用list 中的sublist方法分页
        List<T> dataList = new ArrayList<>();
        // 每页显示多少条记录

        int currentPage; //当前第几页数据
        // 一共多少条记录
        int totalRecord = list.size();
        // 一共多少页
        int totalPage = totalRecord % pageSize;
        if (totalPage > 0) {
            totalPage = totalRecord / pageSize + 1;
        } else {
            totalPage = totalRecord / pageSize;
        }

        System.out.println("总页数:" + totalPage);

        // 当前第几页数据
        currentPage = totalPage < pageIndex ? totalPage : pageIndex;

        return currentPage;
    }
}
