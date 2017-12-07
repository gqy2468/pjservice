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

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.lvmama.phprpc.common.Constants;
import com.lvmama.phprpc.common.Parameter;
import com.lvmama.phprpc.common.URL;
import com.lvmama.phprpc.common.bytecode.Wrapper;
import com.lvmama.phprpc.common.extension.ExtensionLoader;
import com.lvmama.phprpc.common.utils.ConfigUtils;
import com.lvmama.phprpc.common.utils.NetUtils;
import com.lvmama.phprpc.common.utils.StringUtils;
import com.lvmama.phprpc.config.annotation.Service;
import com.lvmama.phprpc.rpc.Exporter;
import com.lvmama.phprpc.rpc.Invoker;
import com.lvmama.phprpc.rpc.Protocol;
import com.lvmama.phprpc.rpc.ProxyFactory;
import com.lvmama.phprpc.rpc.cluster.ConfiguratorFactory;
import com.lvmama.phprpc.rpc.service.GenericService;
import com.lvmama.phprpc.rpc.support.ProtocolUtils;

/**
 * ServiceConfig
 * 
 * @author flashguo
 * @export
 */
public class ServiceConfig<T> extends AbstractServiceConfig {

    private static final long   serialVersionUID = 3033787999037024738L;

    private static final Protocol protocol = ExtensionLoader.getExtensionLoader(Protocol.class).getAdaptiveExtension();
    
    private static final ProxyFactory proxyFactory = ExtensionLoader.getExtensionLoader(ProxyFactory.class).getAdaptiveExtension();

    private static final Map<String, Integer> RANDOM_PORT_MAP = new HashMap<String, Integer>();

    // 接口类型
    private String              interfaceName;

    private Class<?>            interfaceClass;

    // 接口实现类引用
    private T                   ref;

    // 服务名称
    private String              path;

    private final List<URL> urls = new ArrayList<URL>();
    
    private final List<Exporter<?>> exporters = new ArrayList<Exporter<?>>();

    private transient volatile boolean exported;

	private transient volatile boolean unexported;
    
    private volatile String generic;

    public ServiceConfig() {
    }

    public ServiceConfig(Service service) {
        appendAnnotation(Service.class, service);
    }

    public List<ProtocolConfig> getProtocols() {
        return protocols;
    }

    @SuppressWarnings({ "unchecked" })
    public void setProtocols(List<? extends ProtocolConfig> protocols) {
        this.protocols = (List<ProtocolConfig>)protocols;
    }

    @Deprecated
    public void setProtocol(String protocol) {
        this.protocols = Arrays.asList(new ProtocolConfig[] {new ProtocolConfig(protocol)});
    }

    public URL toUrl() {
        return urls == null || urls.size() == 0 ? null : urls.iterator().next();
    }

    public List<URL> toUrls() {
        return urls;
    }
    
    @Parameter(excluded = true)
    public boolean isExported() {
		return exported;
	}

    @Parameter(excluded = true)
	public boolean isUnexported() {
		return unexported;
	}

    public synchronized void export() {
        doExport();
    }
    
    protected synchronized void doExport() {
        if (unexported) {
            throw new IllegalStateException("Already unexported!");
        }
        if (exported) {
            return;
        }
        exported = true;
        if (interfaceName == null || interfaceName.length() == 0) {
            throw new IllegalStateException("<phprpc:service interface=\"\" /> interface not allow null!");
        }
        if (ref instanceof GenericService) {
            interfaceClass = GenericService.class;
            if (StringUtils.isEmpty(generic)) {
                generic = Boolean.TRUE.toString();
            }
        } else {
            try {
                interfaceClass = Class.forName(interfaceName, true, Thread.currentThread()
                        .getContextClassLoader());
            } catch (ClassNotFoundException e) {
                throw new IllegalStateException(e.getMessage(), e);
            }
            checkRef();
            generic = Boolean.FALSE.toString();
        }
        checkProtocol();
        appendProperties(this);
        if (path == null || path.length() == 0) {
            path = interfaceName;
        }
        doExportUrls();
    }

    private void checkRef() {
        // 检查引用不为空，并且引用必需实现接口
        if (ref == null) {
            throw new IllegalStateException("ref not allow null!");
        }
        if (! interfaceClass.isInstance(ref)) {
            throw new IllegalStateException("The class "
                    + ref.getClass().getName() + " unimplemented interface "
                    + interfaceClass + "!");
        }
    }

    private void checkProtocol() {
        if ((protocols == null || protocols.size() == 0)) {
            setProtocols(getProtocols());
        }
        for (ProtocolConfig protocolConfig : protocols) {
            if (StringUtils.isEmpty(protocolConfig.getName())) {
                protocolConfig.setName("thrift");
            }
            appendProperties(protocolConfig);
        }
    }

    public synchronized void unexport() {
        if (! exported) {
            return;
        }
        if (unexported) {
            return;
        }
    	if (exporters != null && exporters.size() > 0) {
    		for (Exporter<?> exporter : exporters) {
    			try {
                    exporter.unexport();
                } catch (Throwable t) {
                	throw new IllegalStateException("unexpected err when unexport" + exporter, t);
                }
    		}
    		exporters.clear();
    	}
        unexported = true;
    }
    
    @SuppressWarnings({ })
    private void doExportUrls() {
        List<URL> registryURLs = loadRegistries(true);
        for (ProtocolConfig protocolConfig : protocols) {
            doExportUrlsFor1Protocol(protocolConfig, registryURLs);
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
	private void doExportUrlsFor1Protocol(ProtocolConfig protocolConfig, List<URL> registryURLs) {
        String name = protocolConfig.getName();
        if (name == null || name.length() == 0) {
            name = "thrift";
        }

        String host = protocolConfig.getHost();
        boolean anyhost = false;
        if (NetUtils.isInvalidLocalHost(host)) {
            anyhost = true;
            try {
                host = InetAddress.getLocalHost().getHostAddress();
            } catch (UnknownHostException e) {
                logger.warn(e.getMessage(), e);
            }
            if (NetUtils.isInvalidLocalHost(host)) {
                if (registryURLs != null && registryURLs.size() > 0) {
                    for (URL registryURL : registryURLs) {
                        try {
                            Socket socket = new Socket();
                            try {
                                SocketAddress addr = new InetSocketAddress(registryURL.getHost(), registryURL.getPort());
                                socket.connect(addr, 1000);
                                host = socket.getLocalAddress().getHostAddress();
                                break;
                            } finally {
                                try {
                                    socket.close();
                                } catch (Throwable e) {}
                            }
                        } catch (Exception e) {
                            logger.warn(e.getMessage(), e);
                        }
                    }
                }
                if (NetUtils.isInvalidLocalHost(host)) {
                    host = NetUtils.getLocalHost();
                }
            }
        }

        Integer port = protocolConfig.getPort();
        final int defaultPort = ExtensionLoader.getExtensionLoader(Protocol.class).getExtension(name).getDefaultPort();
        if (port == null || port == 0) {
            port = defaultPort;
        }
        if (port == null || port <= 0) {
            port = getRandomPort(name);
            if (port == null || port < 0) {
                port = NetUtils.getAvailablePort(defaultPort);
                putRandomPort(name, port);
            }
            logger.warn("Use random available port(" + port + ") for protocol " + name);
        }

        Map<String, String> map = new HashMap<String, String>();
        if (anyhost) {
            map.put(Constants.ANYHOST_KEY, "true");
        }
        map.put(Constants.SIDE_KEY, Constants.PROVIDER_SIDE);
        map.put(Constants.TIMESTAMP_KEY, String.valueOf(System.currentTimeMillis()));
        if (ConfigUtils.getPid() > 0) {
            map.put(Constants.PID_KEY, String.valueOf(ConfigUtils.getPid()));
        }
        appendParameters(map, protocolConfig);
        appendParameters(map, this);

        if (ProtocolUtils.isGeneric(generic)) {
            map.put("generic", generic);
            map.put("methods", Constants.ANY_VALUE);
        } else {
            String[] methods = Wrapper.getWrapper(interfaceClass).getMethodNames();
            if(methods.length == 0) {
                logger.warn("NO method found in service interface " + interfaceClass.getName());
                map.put("methods", Constants.ANY_VALUE);
            }
            else {
                map.put("methods", StringUtils.join(new HashSet<String>(Arrays.asList(methods)), ","));
            }
        }
        if (! ConfigUtils.isEmpty(token)) {
            if (ConfigUtils.isDefault(token)) {
                map.put("token", UUID.randomUUID().toString());
            } else {
                map.put("token", token);
            }
        }
        if ("injvm".equals(protocolConfig.getName())) {
            protocolConfig.setRegister(false);
            map.put("notify", "false");
        }
        // 导出服务
        String contextPath = protocolConfig.getContextpath();
        URL url = new URL(name, host, port, (contextPath == null || contextPath.length() == 0 ? "" : contextPath + "/") + path, map);
        
        if (ExtensionLoader.getExtensionLoader(ConfiguratorFactory.class)
                .hasExtension(url.getProtocol())) {
            url = ExtensionLoader.getExtensionLoader(ConfiguratorFactory.class)
                  
            		.getExtension(url.getProtocol()).getConfigurator(url).configure(url);
        }

        String scope = url.getParameter(Constants.SCOPE_KEY);
        //配置为none不暴露
        if (! Constants.SCOPE_NONE.toString().equalsIgnoreCase(scope)) {

            //配置不是remote的情况下做本地暴露 (配置为remote，则表示只暴露远程服务)
            if (Constants.SCOPE_LOCAL.toString().equalsIgnoreCase(scope)) {
                exportLocal(url);
            }
            //如果配置不是local则暴露为远程服务.(配置为local，则表示只暴露本地服务)
            if (! Constants.SCOPE_LOCAL.toString().equalsIgnoreCase(scope) ){
                if (logger.isInfoEnabled()) {
                    logger.info("Export thrift service " + interfaceClass.getName() + " to url " + url);
                }
                if (registryURLs != null && registryURLs.size() > 0
                        && url.getParameter("register", true)) {
                    for (URL registryURL : registryURLs) {
                        url = url.addParameterIfAbsent("dynamic", registryURL.getParameter("dynamic"));
                        URL monitorUrl = loadMonitor(registryURL);
                        if (monitorUrl != null) {
                            url = url.addParameterAndEncoded(Constants.MONITOR_KEY, monitorUrl.toFullString());
                        }
                        if (logger.isInfoEnabled()) {
                            logger.info("Register thrift service " + interfaceClass.getName() + " url " + url + " to registry " + registryURL);
                        }
                        Invoker<?> invoker = proxyFactory.getInvoker(ref, (Class) interfaceClass, registryURL.addParameterAndEncoded(Constants.EXPORT_KEY, url.toFullString().replace("$Iface", "")));

                        Exporter<?> exporter = protocol.export(invoker);
                        exporters.add(exporter);
                    }
                } else {
					Invoker<?> invoker = proxyFactory.getInvoker(ref, (Class<T>) interfaceClass, url);

                    Exporter<?> exporter = protocol.export(invoker);
                    exporters.add(exporter);
                }
            }
        }
        this.urls.add(url);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private void exportLocal(URL url) {
        if (!Constants.LOCAL_PROTOCOL.equalsIgnoreCase(url.getProtocol())) {
            URL local = URL.valueOf(url.toFullString())
                    .setProtocol(Constants.LOCAL_PROTOCOL)
                    .setHost(NetUtils.LOCALHOST)
                    .setPort(0);
            Exporter<?> exporter = protocol.export(
                    proxyFactory.getInvoker(ref, (Class) interfaceClass, local));
            exporters.add(exporter);
            logger.info("Export thrift service " + interfaceClass.getName() +" to local registry");
        }
    }

    public Class<?> getInterfaceClass() {
        if (interfaceClass != null) {
            return interfaceClass;
        }
        if (ref instanceof GenericService) {
            return GenericService.class;
        }
        try {
            if (interfaceName != null && interfaceName.length() > 0) {
                this.interfaceClass = Class.forName(interfaceName, true, Thread.currentThread()
                    .getContextClassLoader());
            }
        } catch (ClassNotFoundException t) {
            throw new IllegalStateException(t.getMessage(), t);
        }
        return interfaceClass;
    }

    /**
     * @deprecated
     * @see #setInterface(Class)
     * @param interfaceClass
     */
    public void setInterfaceClass(Class<?> interfaceClass) {
        setInterface(interfaceClass);
    }

    public String getInterface() {
        return interfaceName;
    }

    public void setInterface(String interfaceName) {
        this.interfaceName = interfaceName;
        if (id == null || id.length() == 0) {
            id = interfaceName;
        }
    }
    
    public void setInterface(Class<?> interfaceClass) {
        if (interfaceClass != null && ! interfaceClass.isInterface()) {
            throw new IllegalStateException("The interface class " + interfaceClass + " is not a interface!");
        }
        this.interfaceClass = interfaceClass;
        setInterface(interfaceClass == null ? (String) null : interfaceClass.getName());
    }

    public T getRef() {
        return ref;
    }

    public void setRef(T ref) {
        this.ref = ref;
    }

    @Parameter(excluded = true)
    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        checkPathName("path", path);
        this.path = path;
    }

    public void setGeneric(String generic) {
        if (StringUtils.isEmpty(generic)) { return; }
        if (ProtocolUtils.isGeneric(generic)) {
            this.generic = generic;
        } else {
            throw new IllegalArgumentException("Unsupported generic type " + generic);
        }
    }

    public String getGeneric() {
        return generic;
    }

    private static Integer getRandomPort(String protocol) {
        protocol = protocol.toLowerCase();
        if (RANDOM_PORT_MAP.containsKey(protocol)) {
            return RANDOM_PORT_MAP.get(protocol);
        }
        return Integer.MIN_VALUE;
    }

    private static void putRandomPort(String protocol, Integer port) {
        protocol = protocol.toLowerCase();
        if (!RANDOM_PORT_MAP.containsKey(protocol)) {
            RANDOM_PORT_MAP.put(protocol, port);
        }
    }
}