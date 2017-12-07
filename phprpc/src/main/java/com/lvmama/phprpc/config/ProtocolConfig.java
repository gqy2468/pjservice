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

import java.util.Map;

import com.lvmama.phprpc.common.StatusChecker;
import com.lvmama.phprpc.common.ThreadPool;
import com.lvmama.phprpc.common.extension.ExtensionLoader;
import com.lvmama.phprpc.common.utils.NetUtils;
import com.lvmama.phprpc.common.utils.StringUtils;
import com.lvmama.phprpc.common.Parameter;
import com.lvmama.phprpc.rpc.Protocol;

/**
 * ProtocolConfig
 * 
 * @author flashguo
 * @export
 */
public class ProtocolConfig extends AbstractConfig {

    private static final long   serialVersionUID = 6913423882496634749L;

    // 服务协议
    private String              name;

    // 服务IP地址(多网卡时使用)
    private String              host;

    // 服务端口
    private Integer             port;

    // 上下文路径
    private String              contextpath;
    
    // 线程池类型
    private String              threadpool;
    
    // 线程池大小(固定大小)
    private Integer             threads;
    
    // IO线程池大小(固定大小)
    private Integer             iothreads;
    
    // 线程池队列大小
    private Integer             queues;
    
    // 最大接收连接数
    private Integer             accepts;
    
    // 字符集
    private String              charset;
    
    // 最大请求数据长度
    private Integer             payload;
    
    // 缓存区大小
    private Integer             buffer;
    
    // 心跳间隔
    private Integer             heartbeat;

    // 访问日志
    private String              accesslog;

    // 对称网络组网方式
    private String              networker;
    
    // 命令行提示符
    private String              prompt;

    // status检查
    private String              status;
    
    // 是否注册
    private Boolean             register;
    
    // 参数
    private Map<String, String> parameters;

    // 是否为缺省
    private Boolean isDefault;

    /**
     * 生成服务地址<br>
     * Server端：ip:port <br>
     * Client端：ip:port:i_节点序列号 <br>
     * <p>
     * 
     * @return ip:port
     */
    public String getAddress() {
    	host = StringUtils.isEmpty(host) ? NetUtils.getLocalHost() : host;
        return host + ":" + port;
    }
    
    public ProtocolConfig() {
    }
    
    public ProtocolConfig(String name) {
        setName(name);
    }

    public ProtocolConfig(String name, int port) {
        setName(name);
        setPort(port);
    }
    
    @Parameter(excluded = true)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        checkName("name", name);
        this.name = name;
        if (id == null || id.length() == 0) {
            id = name;
        }
    }

    @Parameter(excluded = true)
    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        checkName("host", host);
        this.host = host;
    }

    @Parameter(excluded = true)
    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    @Deprecated
    @Parameter(excluded = true)
    public String getPath() {
        return getContextpath();
    }

    @Deprecated
    public void setPath(String path) {
        setContextpath(path);
    }

    @Parameter(excluded = true)
    public String getContextpath() {
        return contextpath;
    }

    public void setContextpath(String contextpath) {
        checkPathName("contextpath", contextpath);
        this.contextpath = contextpath;
    }

    public String getThreadpool() {
        return threadpool;
    }

    public void setThreadpool(String threadpool) {
        checkExtension(ThreadPool.class, "threadpool", threadpool);
        this.threadpool = threadpool;
    }

    public Integer getThreads() {
        return threads;
    }

    public void setThreads(Integer threads) {
        this.threads = threads;
    }

    public Integer getIothreads() {
        return iothreads;
    }

    public void setIothreads(Integer iothreads) {
        this.iothreads = iothreads;
    }

    public Integer getQueues() {
        return queues;
    }
    
    public void setQueues(Integer queues) {
        this.queues = queues;
    }
    
    public Integer getAccepts() {
        return accepts;
    }
    
    public void setAccepts(Integer accepts) {
        this.accepts = accepts;
    }

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    public Integer getPayload() {
        return payload;
    }

    public void setPayload(Integer payload) {
        this.payload = payload;
    }

    public Integer getBuffer() {
        return buffer;
    }

    public void setBuffer(Integer buffer) {
        this.buffer = buffer;
    }

    public Integer getHeartbeat() {
        return heartbeat;
    }

    public void setHeartbeat(Integer heartbeat) {
        this.heartbeat = heartbeat;
    }
    
    public String getAccesslog() {
        return accesslog;
    }
    
    public void setAccesslog(String accesslog) {
        this.accesslog = accesslog;
    }

    @Parameter(escaped = true)
    public String getPrompt() {
        return prompt;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }

    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        checkMultiExtension(StatusChecker.class, "status", status);
        this.status = status;
    }

    public Boolean isRegister() {
        return register;
    }
    
    public void setRegister(Boolean register) {
        this.register = register;
    }

    public String getNetworker() {
        return networker;
    }

    public void setNetworker(String networker) {
        this.networker = networker;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, String> parameters) {
        this.parameters = parameters;
    }

    public Boolean isDefault() {
        return isDefault;
    }

    public void setDefault(Boolean isDefault) {
        this.isDefault = isDefault;
    }

    public void destory() {
        if (name != null) {
            ExtensionLoader.getExtensionLoader(Protocol.class).getExtension(name).destroy();;
        }
    }

    public static void destroyAll() {
        ExtensionLoader<Protocol> loader = ExtensionLoader.getExtensionLoader(Protocol.class);
        for (String protocolName : loader.getLoadedExtensions()) {
            try {
                Protocol protocol = loader.getLoadedExtension(protocolName);
                if (protocol != null) {
                    protocol.destroy();
                }
            } catch (Throwable t) {
                logger.warn(t.getMessage(), t);
            }
        }
    }

}