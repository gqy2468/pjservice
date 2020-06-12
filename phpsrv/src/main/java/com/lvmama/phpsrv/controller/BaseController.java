package com.lvmama.phpsrv.controller;

import com.lvmama.phpsrv.entity.ErrorResponseBean;
import com.lvmama.phpsrv.entity.SuccessResponseBean;
import com.lvmama.phpsrv.utils.Common;

/**
 * 消费者控制器
 */
public class BaseController {

	public BaseController(){
    }

    /**
     * 输出错误格式信息
     * @param error
     * @param message
     * @return
     */
    public String errorResponse(int error, String message){
        ErrorResponseBean bean = new ErrorResponseBean();

        bean.setError(error);
        bean.setMessage(message);

        return Common.objectToJson(bean);
    }

    /**
     * 输出正确格式信息
     * @param success
     * @param content
     * @return
     */
    public String successResponse(int success, String content){
        SuccessResponseBean bean = new SuccessResponseBean();

        bean.setSuccess(success);
        bean.setContent(content);

        return Common.objectToJson(bean);
    }
}
