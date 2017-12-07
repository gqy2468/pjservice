package com.lvmama.phpsrv.controller;

import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.monitor.MonitorService;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 推送数据
 * Created by guoqiya on 2016/12/16.
 */
@RestController
@RequestMapping("/monitor")
public class MonitorController extends BaseController implements com.lvmama.phprpc.monitor.MonitorService.Iface{

    /**
     * 获取图片
     * @param id
     * @return String
     */
    @RequestMapping(value = "/collect", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String collect(@RequestParam(value = "statistics") String statistics){
    	MonitorService ms = (MonitorService)this.comsumeContext.getBean("monitorService");
        ms.collect(URL.valueOf(statistics));
        return statistics;
    }

    /**
     * 删除图片
     * @param id
     * @return String
     */
    @RequestMapping(value = "/lookup", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String lookup(@RequestParam(value = "query") String query){
    	MonitorService ms = (MonitorService)this.comsumeContext.getBean("monitorService");
        ms.lookup(URL.valueOf(query));
        return query;
    }

}
