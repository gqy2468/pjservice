package com.lvmama.phpsrv.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.lvmama.comm.pet.po.user.UserUser;
import com.lvmama.comm.pet.service.user.UserUserProxy;

import com.lvmama.phpsrv.Service.UserService;
import com.lvmama.phpsrv.utils.Common;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

/**
 * 用户
 */
@RestController
@RequestMapping("/user")
public class UserController extends BaseController implements UserService.Iface{

    @Reference
    private UserUserProxy userUserProxy;

    /**
     * 通过userNo获取用户详情，example：userNo = 40288a8c2362a15301236559c73a2de8
     * @param userNo
     * @return UserUser
     */
    @RequestMapping(value = "/getByUserNo", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String getByUserNo(@RequestParam(value = "userNo") String userNo){
        UserUser user = userUserProxy.getUserUserByUserNo(userNo);
        String resStr = Common.objectToJson(user);
        return resStr;
    }

    /**
     * 通过userNoList获取用户详情，example：userNoList = 40288a8c2362a15301236559c73a2de8,40288a8b24c93bcd0124d6b64fc33895
     * @param userNoList
     * @return
     */
    @RequestMapping(value = "/getListByUserNoList", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String getListByUserNoList(@RequestParam(value = "userNoList") String userNoList){
        List<String> users = Arrays.asList(userNoList.split(","));
        List<UserUser> user = userUserProxy.getUsersListByUserNoList(users);
        String resStr = Common.objectToJson(user);
        return resStr;
    }

    @RequestMapping(value="/test", produces="text/html;charset=UTF-8")
    @ResponseBody
    public String getTabJson() {
        UserUser user = new UserUser();
        user.setUserName("test");
        String strKey = "110";
        String json = "{'msg':'token获取成功','result':'1','token':'9ebd141e-64a9-46b0-85be-5c0c21dc7d60'}";
        return json;
    }

}
