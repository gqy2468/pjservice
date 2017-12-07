/*
 * Copyright 1999-2016 Joyo Group.
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *      http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.lvmama.phprpc.monitor.thrift;

import com.lvmama.phprpc.common.Constants;
import com.lvmama.phprpc.common.URL;
import com.lvmama.phprpc.monitor.Monitor;
import com.lvmama.phprpc.monitor.MonitorService;
import com.lvmama.phprpc.monitor.MonitorService.Iface;
import com.lvmama.phprpc.monitor.support.AbstractMonitorFactory;
import com.lvmama.phprpc.rpc.Invoker;
import com.lvmama.phprpc.rpc.Protocol;
import com.lvmama.phprpc.rpc.ProxyFactory;

/**
 * DefaultMonitorFactroy
 * 
 * @author william.liangf
 */
public class ThriftMonitorFactroy extends AbstractMonitorFactory {

    private Protocol protocol;
    
    private ProxyFactory proxyFactory;

    public void setProtocol(Protocol protocol) {
        this.protocol = protocol;
    }

    public void setProxyFactory(ProxyFactory proxyFactory) {
        this.proxyFactory = proxyFactory;
    }
    
    @Override
    protected Monitor createMonitor(URL url) {
        url = url.setProtocol(url.getParameter(Constants.PROTOCOL_KEY, "thrift"));
        if (url.getPath() == null || url.getPath().length() == 0) {
            url = url.setPath(MonitorService.class.getName());
        }
        String filter = url.getParameter(Constants.REFERENCE_FILTER_KEY);
        if (filter == null || filter.length() == 0) {
            filter = "";
        } else {
            filter = filter + ",";
        }
        url = url.addParameters(Constants.CLUSTER_KEY, "failsafe", Constants.CHECK_KEY, String.valueOf(false), 
                Constants.REFERENCE_FILTER_KEY, filter + "-monitor");
        Invoker<Iface> monitorInvoker = protocol.refer(MonitorService.Iface.class, url);
        MonitorService.Iface monitorService = (MonitorService.Iface) proxyFactory.getProxy(monitorInvoker);
        return new ThriftMonitor(monitorInvoker, monitorService);
    }

}