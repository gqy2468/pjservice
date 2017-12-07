/*
 * Copyright 1999-2011 Joyo Group.
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
package com.lvmama.phprpc.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.lvmama.phprpc.common.Constants;
import com.lvmama.phprpc.common.Parameter;
import com.lvmama.phprpc.common.URL;
import com.lvmama.phprpc.common.extension.ExtensionLoader;
import com.lvmama.phprpc.common.utils.ConfigUtils;
import com.lvmama.phprpc.common.utils.NetUtils;
import com.lvmama.phprpc.common.utils.StringUtils;
import com.lvmama.phprpc.common.utils.UrlUtils;
import com.lvmama.phprpc.monitor.MonitorFactory;
import com.lvmama.phprpc.monitor.MonitorService;
import com.lvmama.phprpc.registry.RegistryFactory;
import com.lvmama.phprpc.rpc.cluster.Cluster;
import com.lvmama.phprpc.rpc.support.ProtocolUtils;


/**
 * AbstractConsumerConfig
 * 
 * @see com.lvmama.phprpc.config.ReferenceConfig
 * @author william.liangf
 * @export
 */
public abstract class AbstractReferenceConfig extends AbstractConfig {

    private static final long serialVersionUID = -2786526984373031126L;

    // ======== 引用缺省值，当引用属性未设置时使用该缺省值替代  ========

    // 远程调用超时时间(毫秒)
    protected Integer             timeout;
    
    // 检查服务提供者是否存在
    protected Boolean             check;

    // 是否加载时即刻初始化
    protected Boolean             init;

    // 是否使用泛接口
    protected String             generic;

    // 优先从JVM内获取引用实例
    protected Boolean             injvm;
    
    // lazy create connection
    protected Boolean             lazy;

    protected String              reconnect;
    
    protected Boolean             sticky;
    
    //stub是否支持event事件. //TODO slove merge problem 
    protected Boolean             stubevent ;//= Constants.DEFAULT_STUB_EVENT;

    // 版本
    protected String               version;

    // 服务分组
    protected String               group;

    // 是否注册
    private Boolean                register;

    // 注册中心
    private List<RegistryConfig>   registries;
    
    // 集群方式
    protected String               cluster;

    // 服务监控
    protected MonitorConfig        monitor;

    public Integer getTimeout() {
        return timeout;
    }

    public void setTimeout(Integer timeout) {
        this.timeout = timeout;
    }

    public Boolean isCheck() {
        return check;
    }

    public void setCheck(Boolean check) {
        this.check = check;
    }

    public Boolean isInit() {
        return init;
    }

    public void setInit(Boolean init) {
        this.init = init;
    }

    @Parameter(excluded = true)
    public Boolean isGeneric() {
        return ProtocolUtils.isGeneric(generic);
    }

    public void setGeneric(Boolean generic) {
        if (generic != null) {
            this.generic = generic.toString();
        }
    }

    public void setGeneric(String generic) {
        this.generic = generic;
    }

    public String getGeneric() {
        return generic;
    }

    /**
     * @return
     * @deprecated 通过scope进行判断，scope=local
     */
    @Deprecated
    public Boolean isInjvm() {
        return injvm;
    }
    
    /**
     * @param injvm
     * @deprecated 通过scope设置，scope=local表示使用injvm协议.
     */
    @Deprecated 
    public void setInjvm(Boolean injvm) {
        this.injvm = injvm;
    }

    @Parameter(key = Constants.LAZY_CONNECT_KEY)
    public Boolean getLazy() {
        return lazy;
    }

    public void setLazy(Boolean lazy) {
        this.lazy = lazy;
    }

    @Parameter(key = Constants.STUB_EVENT_KEY)
    public Boolean getStubevent() {
        return stubevent;
    }
    
    @Parameter(key = Constants.RECONNECT_KEY)
    public String getReconnect() {
        return reconnect;
    }

    public void setReconnect(String reconnect) {
        this.reconnect = reconnect;
    }

    @Parameter(key = Constants.CLUSTER_STICKY_KEY)
    public Boolean getSticky() {
        return sticky;
    }

    public void setSticky(Boolean sticky) {
        this.sticky = sticky;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        checkKey("version", version);
        this.version = version;
    }

    public Boolean isRegister() {
        return register;
    }

    public RegistryConfig getRegistry() {
        return registries == null || registries.size() == 0 ? null : registries.get(0);
    }

    public void setRegistry(RegistryConfig registry) {
        List<RegistryConfig> registries = new ArrayList<RegistryConfig>(1);
        registries.add(registry);
        this.registries = registries;
    }

    public void setRegister(Boolean register) {
        this.register = register;
        if (Boolean.FALSE.equals(register)) {
            setRegistry(new RegistryConfig(RegistryConfig.NO_AVAILABLE));
        }
    }

    public List<RegistryConfig> getRegistries() {
        return registries;
    }

    @SuppressWarnings({ "unchecked" })
    public void setRegistries(List<? extends RegistryConfig> registries) {
        this.registries = (List<RegistryConfig>)registries;
    }

    public MonitorConfig getMonitor() {
        return monitor;
    }

    public void setMonitor(MonitorConfig monitor) {
        this.monitor = monitor;
    }

    public void setMonitor(String monitor) {
        this.monitor = new MonitorConfig(monitor);
    }

    protected void checkRegistry() {
        // 兼容旧版本
        if (registries == null || registries.size() == 0) {
            String address = ConfigUtils.getProperty("thrift.registry.address");
            if (address != null && address.length() > 0) {
                registries = new ArrayList<RegistryConfig>();
                String[] as = address.split("\\s*[|]+\\s*");
                for (String a : as) {
                    RegistryConfig registryConfig = new RegistryConfig();
                    registryConfig.setAddress(a);
                    registries.add(registryConfig);
                }
            }
        }
        if ((registries == null || registries.size() == 0)) {
            throw new IllegalStateException((getClass().getSimpleName().startsWith("Reference") 
                    ? "No such any registry to refer service in consumer " 
                        : "No such any registry to export service in provider ")
                                                    + NetUtils.getLocalHost()
                                                    + ", Please add <phprpc:registry address=\"...\" /> to your spring config. If you want unregister, please set <phprpc:service registry=\"N/A\" />");
        }
        for (RegistryConfig registryConfig : registries) {
            appendProperties(registryConfig);
        }
    }
    
    protected List<URL> loadRegistries(boolean provider) {
        checkRegistry();
        List<URL> registryList = new ArrayList<URL>();
        if (registries != null && registries.size() > 0) {
            for (RegistryConfig config : registries) {
                String address = config.getAddress();
                if (address == null || address.length() == 0) {
                	address = Constants.ANYHOST_VALUE;
                }
                String sysaddress = System.getProperty("thrift.registry.address");
                if (sysaddress != null && sysaddress.length() > 0) {
                    address = sysaddress;
                }
                if (address != null && address.length() > 0 
                        && ! RegistryConfig.NO_AVAILABLE.equalsIgnoreCase(address)) {
                    Map<String, String> map = new HashMap<String, String>();
                    appendParameters(map, config);
                    map.put(Constants.TIMESTAMP_KEY, String.valueOf(System.currentTimeMillis()));
                    if (ConfigUtils.getPid() > 0) {
                        map.put(Constants.PID_KEY, String.valueOf(ConfigUtils.getPid()));
                    }
                    if (! map.containsKey("protocol")) {
                        if (ExtensionLoader.getExtensionLoader(RegistryFactory.class).hasExtension("remote")) {
                            map.put("protocol", "remote");
                        } else {
                            map.put("protocol", "thrift");
                        }
                    }
                    List<URL> urls = UrlUtils.parseURLs(address, map);
                    for (URL url : urls) {
                        url = url.addParameter(Constants.REGISTRY_KEY, url.getProtocol());
                        url = url.setProtocol(Constants.REGISTRY_PROTOCOL);
                        if ((provider && url.getParameter(Constants.REGISTER_KEY, true))
                                || (! provider && url.getParameter(Constants.SUBSCRIBE_KEY, true))) {
                            registryList.add(url);
                        }
                    }
                }
            }
        }
        return registryList;
    }
    
    protected URL loadMonitor(URL registryURL) {
        if (monitor == null) {
            String monitorAddress = ConfigUtils.getProperty("thrift.monitor.address");
            String monitorProtocol = ConfigUtils.getProperty("thrift.monitor.protocol");
            if (monitorAddress != null && monitorAddress.length() > 0
                    || monitorProtocol != null && monitorProtocol.length() > 0) {
                monitor = new MonitorConfig();
            } else {
                return null;
            }
        }
        appendProperties(monitor);
        Map<String, String> map = new HashMap<String, String>();
        map.put(Constants.INTERFACE_KEY, MonitorService.class.getName());
        map.put(Constants.CLIENT_KEY, "compact");
        map.put("thrift", "0.9.1");
        map.put(Constants.TIMESTAMP_KEY, String.valueOf(System.currentTimeMillis()));
        if (ConfigUtils.getPid() > 0) {
            map.put(Constants.PID_KEY, String.valueOf(ConfigUtils.getPid()));
        }
        appendParameters(map, monitor);
        String address = monitor.getAddress();
        String sysaddress = System.getProperty("thrift.monitor.address");
        if (sysaddress != null && sysaddress.length() > 0) {
            address = sysaddress;
        }
        if (ConfigUtils.isNotEmpty(address)) {
            if (! map.containsKey(Constants.PROTOCOL_KEY)) {
                if (ExtensionLoader.getExtensionLoader(MonitorFactory.class).hasExtension("logstat")) {
                    map.put(Constants.PROTOCOL_KEY, "logstat");
                } else {
                    map.put(Constants.PROTOCOL_KEY, "thrift");
                }
            }
            return UrlUtils.parseURL(address, map);
        } else if (Constants.REGISTRY_PROTOCOL.equals(monitor.getProtocol()) && registryURL != null) {
            return registryURL.setProtocol("thrift").addParameter(Constants.PROTOCOL_KEY, "registry").addParameterAndEncoded(Constants.REFER_KEY, StringUtils.toQueryString(map));
        }
        return null;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        checkKey("group", group);
        this.group = group;
    }
    
    public String getCluster() {
        return cluster;
    }

    public void setCluster(String cluster) {
        checkExtension(Cluster.class, "cluster", cluster);
        this.cluster = cluster;
    }

}