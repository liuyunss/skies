package com.skies.utils.crudUtils;

import java.util.List;

/**
 * Created by sunpengyan on 2017/5/15.
 */
public class PageResult<T> {
    private List<T> list; // 结果集
    private int totalRow; // 总记录数

    public PageResult() {
    }

    public PageResult(List<T> list, int totalRow) {
        this.list = list;
        this.totalRow = totalRow;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    public int getTotalRow() {
        return totalRow;
    }

    public void setTotalRow(int totalRow) {
        this.totalRow = totalRow;
    }
}

