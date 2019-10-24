package com.youlexuan.entity;

import java.io.Serializable;
import java.util.List;

/**创建一个类 用来分页的返回json数据
 * @author 王大亮
 * @date 2019/9/24 16:54
 */
public class PageResult implements Serializable {

    private long total;//总的数据量

    private List rows;//所有数据集合 当前页的全部数据

    public PageResult(long total) {
        this.total = total;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public List getRows() {
        return rows;
    }

    public void setRows(List rows) {
        this.rows = rows;
    }

    public PageResult(long total, List rows) {

        this.total = total;
        this.rows = rows;
    }

    public PageResult() {
    }
}
