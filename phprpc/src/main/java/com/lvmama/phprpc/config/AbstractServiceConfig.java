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
package com.lvmama.phprpc.config;

import java.util.ArrayList;
import java.util.Arrays;
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

/**
 * AbstractServiceConfig
 * 
 * @author flashguo
 * @export
 */
public abstract class AbstractServiceConfig extends AbstractConfig {

    private static final long      serialVersionUID = 1L;

    // 服务版本
    protected String               version;

    // 服务分组
    protected String               group;

    // 服务是否已经deprecated
    protected Boolean              deprecated;

    // 延迟暴露
    protected Integer              delay;

    // 是否暴露
    protected Boolean              export;

    // 权重
    protected Integer              weight;

    // 应用文档
    protected String               document;

    // 在注册中心上注册成动态的还是静态的服务
    protected Boolean              dynamic;

    // 是否使用令牌
    protected String               token;

    // 访问日志
    protected String               accesslog;

    // 允许执行请求数
    private Integer                executes;

    protected List<ProtocolConfig> protocols;

    // 是否注册
    private Boolean                register;

    // 注册中心
    private List<RegistryConfig>   registries;

    // 服务监控
    protected MonitorConfig        monitor;

    // 服务暴露或引用的scope,如果为local，则表示只在当前JVM内查找.
    private String scope;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        checkKey("version", version);
        this.version = version;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        checkKey("group", group);
        this.group = group;
    }

	public Integer getDelay() {
		return delay;
	}

	public void setDelay(Integer delay) {
	    this.delay = delay;
	}

    public Boolean getExport() {
        return export;
    }

    public void setExport(Boolean export) {
        this.export = export;
    }
    
    public Integer getWeight() {
        return weight;
    }
    
    public void setWeight(Integer weight) {
        this.weight = weight;
    }
    
    @Parameter(escaped = true)
    public String getDocument() {
        return document;
    }
    
    public void setDocument(String document) {
        this.document = document;
    }

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
	    checkName("token", token);
		this.token = token;
	}
	
	public void setToken(Boolean token) {
        if (token == null) {
            setToken((String) null);
        } else {
            setToken(String.valueOf(token));
        }
    }

    public Boolean isDeprecated() {
        return deprecated;
    }

    public void setDeprecated(Boolean deprecated) {
        this.deprecated = deprecated;
    }

    public Boolean isDynamic() {
        return dynamic;
    }

    public void setDynamic(Boolean dynamic) {
        this.dynamic = dynamic;
    }

    public List<ProtocolConfig> getProtocols() {
        return protocols;
    }

    @SuppressWarnings({ "unchecked" })
    public void setProtocols(List<? extends ProtocolConfig> protocols) {
        this.protocols = (List<ProtocolConfig>)protocols;
    }

    public ProtocolConfig getProtocol() {
        return protocols == null || protocols.size() == 0 ? null : protocols.get(0);
    }

    public void setProtocol(ProtocolConfig protocol) {
        this.protocols = Arrays.asList(new ProtocolConfig[] {protocol});
    }

    public String getAccesslog() {
        return accesslog;
    }

    public void setAccesslog(String accesslog) {
        this.accesslog = accesslog;
    }
    
    public void setAccesslog(Boolean accesslog) {
        if (accesslog == null) {
            setAccesslog((String) null);
        } else {
            setAccesslog(String.valueOf(accesslog));
        }
    }

    public Integer getExecutes() {
        return executes;
    }
    
    public void setExecutes(Integer executes) {
        this.executes = executes;
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
                                                    + ", Please add <phprpc:registry address=\"...\" /> to your spring config. If you want unregister, please set <thrift:service registry=\"N/A\" />");
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

	public String getScope() {
		return scope;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}
}