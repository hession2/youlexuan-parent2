package com.youlexuan.entity;

import java.io.Serializable;

/**用来包装数据返回的int值 为json
 * @author 王大亮
 * @date 2019/9/24 19:33
 */
public class Result implements Serializable {

    private boolean success;

    private String message;

    public Result() {
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Result(boolean success, String message) {

        this.success = success;
        this.message = message;
    }
}
