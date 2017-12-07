/**
 * File Created at 2011-12-06
 * $Id$
 *
 * Copyright 2008 Alibaba.com Croporation Limited.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Alibaba Company. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Alibaba.com.
 */
package com.lvmama.phprpc.rpc.protocol;

import java.lang.reflect.Constructor;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.thrift.TMultiplexedProcessor;
import org.apache.thrift.TProcessor;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.protocol.TMultiplexedProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.server.ServerContext;
import org.apache.thrift.server.TNonblockingServer;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TServerEventHandler;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TNonblockingServerSocket;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;

import com.lvmama.phprpc.common.Constants;
import com.lvmama.phprpc.common.URL;
import com.lvmama.phprpc.rpc.RpcContext;
import com.lvmama.phprpc.rpc.RpcException;

/**
 * @author flashguo
 */
public class ThriftProtocol extends AbstractProxyProtocol  {

    public static final int DEFAULT_PORT = 8090;

    public static final String NAME = "thrift";

    public static TNonblockingServerSocket transport = null;

    public static TMultiplexedProcessor tprocessor = null;

	public static Map<String, TTransport> ttransport = new ConcurrentHashMap<String, TTransport>();
	
	private TServerEventHandler eventHandler = new TServerEventHandler() {

		public ServerContext createContext(TProtocol input, TProtocol output) {
			// TODO Auto-generated method stub
			return null;
		}

		public void deleteContext(ServerContext serverContext, TProtocol input, TProtocol output) {
			// TODO Auto-generated method stub
			
		}

		public void preServe() {
			// TODO Auto-generated method stub
			
		}

		public void processContext(ServerContext serverContext, TTransport inputTransport, TTransport outputTransport) {
			// TODO Auto-generated method stub
	        //TSocket socket = (TSocket)inputTransport;
            //RpcContext.getContext().setRemoteAddress((InetSocketAddress) socket.getSocket().getRemoteSocketAddress());
            RpcContext.getContext().setRemoteAddress(new InetSocketAddress("127.0.0.1", 0));
			
		}
	
	};

    public int getDefaultPort() {
        return DEFAULT_PORT;
    }

    protected <T> Runnable doExport(T impl, Class<T> type, URL url)
            throws RpcException {

        logger.info("impl => " + impl.getClass());
        logger.info("type => " + type.getName());
        logger.info("url => " + url);

        TNonblockingServer.Args tArgs = null;
        String iFace = "$Iface";
        String processor = "$Processor";
        String typeName = type.getName();
        if (typeName.endsWith(iFace)) {
            String processorClsName = typeName.substring(0, typeName.indexOf(iFace)) + processor;
            logger.info("processorClsName => " + processorClsName);
            try {
                Class<?> clazz = Class.forName(processorClsName);
                Constructor<?> constructor = clazz.getConstructor(type);
                try {
                    if (tprocessor == null) tprocessor = new TMultiplexedProcessor();
                    tprocessor.registerProcessor(typeName.substring(0, typeName.indexOf(iFace)), (TProcessor) constructor.newInstance(impl));
                    if (transport == null) transport = new TNonblockingServerSocket(url.getPort());
                    tArgs = new TNonblockingServer.Args(transport);
                    tArgs.processor(tprocessor);
                    tArgs.transportFactory(new TFramedTransport.Factory());
                    tArgs.protocolFactory(new TCompactProtocol.Factory());
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                    throw new RpcException("Fail to create thrift server(" + url + ") : " + e.getMessage(), e);
                }
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                throw new RpcException("Fail to create thrift server(" + url + ") : " + e.getMessage(), e);
            }
        }

        if (tArgs == null) {
            logger.error("Fail to create thrift server(" + url + ") due to null args");
            throw new RpcException("Fail to create thrift server(" + url + ") due to null args");
        }
        final TServer thriftServer = new TNonblockingServer(tArgs);
        thriftServer.setServerEventHandler(eventHandler);

        new Thread(new Runnable() {
            public void run() {
                logger.info("Start Thrift Server");
                thriftServer.serve();
                logger.info("Thrift server started.");
            }
        }).start();

        return new Runnable() {
            public void run() {
                try {
                    logger.info("Close Thrift Server");
                    thriftServer.stop();
                } catch (Throwable e) {
                    logger.warn(e.getMessage(), e);
                }
            }
        };
    }

    public void destroy() {

        super.destroy();

    } // ~ end of method destroy
    
    @SuppressWarnings("unchecked")
	@Override
    protected synchronized <T> T doRefer(Class<T> type, URL url) throws RpcException {

        logger.info("type => " + type.getName());
        logger.info("url => " + url);

        try {
            TSocket tSocket;
            TProtocol protocol;
            T thriftClient = null;
            String iFace = "$Iface";
            String client = "$Client";

            String typeName = type.getName();
            if (typeName.endsWith(iFace)) {
                String clientClsName = typeName.substring(0, typeName.indexOf(iFace)) + client;
                Class<?> clazz = Class.forName(clientClsName);
                Constructor<?> constructor = clazz.getConstructor(TProtocol.class);
                try {
                    tSocket = new TSocket(url.getHost(), url.getPort());
                    String timeout = url.getParameter(Constants.TIMEOUT_KEY);
                    tSocket.setTimeout(timeout == null || timeout.length() == 0 ? 1000 : Integer.parseInt(timeout));
                    if (!"compact".equals(url.getParameter(Constants.CLIENT_KEY))) {
                    	ttransport.put(typeName, (TTransport)tSocket);
                    	protocol = new TBinaryProtocol(ttransport.get(typeName));
                    	typeName = "\\"+typeName.substring(0, typeName.indexOf(iFace)).replace(".", "\\");
                    	protocol = new TMultiplexedProtocol(protocol, typeName);
                    } else {
                    	ttransport.put(typeName, new TFramedTransport(tSocket, 2048));
                    	protocol = new TCompactProtocol(ttransport.get(typeName));
                    	protocol = new TMultiplexedProtocol(protocol, typeName.substring(0, typeName.indexOf(iFace)));
                    }
                    thriftClient = (T) constructor.newInstance(protocol);
                    logger.info("thrift client opened for service(" + url + ")");
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                    throw new RpcException("Fail to create remoting client:" + e.getMessage(), e);
                }
            }
            return thriftClient;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new RpcException("Fail to create remoting client for service(" + url + "): " + e.getMessage(), e);
        }
    }

}
