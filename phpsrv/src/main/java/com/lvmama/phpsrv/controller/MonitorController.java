package com.lvmama.phpsrv.controller;

import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.monitor.MonitorService;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 推送数据
 */
@RestController
@RequestMapping("/monitor")
public class MonitorController extends BaseController implements com.lvmama.phprpc.monitor.MonitorService.Iface{

    @Reference
    private MonitorService monitorService;

    /**
     * 获取图片
     * @return String
     */
    @RequestMapping(value = "/collect", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String collect(@RequestParam(value = "statistics") String statistics){
        monitorService.collect(URL.valueOf(statistics));
        return statistics;
    }

    /**
     * 删除图片
     * @return String
     */
    @RequestMapping(value = "/lookup", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String lookup(@RequestParam(value = "query") String query){
        monitorService.lookup(URL.valueOf(query));
        return query;
    }

}
