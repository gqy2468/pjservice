package com.lvmama.phpsrv.controller;

import com.lvmama.phpsrv.Repository.cache.RedisCache;
import com.lvmama.phpsrv.Repository.response.Code;
import com.lvmama.phpsrv.Repository.response.ErrorResponseBean;
import com.lvmama.phpsrv.Repository.response.SuccessResponseBean;
import com.lvmama.phpsrv.utils.Common;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * 消费者控制器
 * Created by libiying on 2016/10/13.
 */
public class BaseController {

    protected ApplicationContext comsumeContext;

    protected ApplicationContext providerContext;

    protected RedisCache redisCache;

    @SuppressWarnings("resource")
	public BaseController(){
        comsumeContext = new ClassPathXmlApplicationContext("applicationContext-Consume.xml");
        providerContext = new ClassPathXmlApplicationContext("applicationContext-Provider.xml");

        ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext-Redis.xml");
        redisCache = (RedisCache) context.getBean("redisCache");
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
