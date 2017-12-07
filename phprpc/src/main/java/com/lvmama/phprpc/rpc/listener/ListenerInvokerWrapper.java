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
package com.lvmama.phprpc.rpc.listener;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lvmama.phprpc.common.URL;
import com.lvmama.phprpc.rpc.Invoker;
import com.lvmama.phprpc.rpc.InvokerListener;
import com.lvmama.phprpc.rpc.Invocation;
import com.lvmama.phprpc.rpc.Result;
import com.lvmama.phprpc.rpc.RpcException;

/**
 * ListenerInvoker
 * 
 * @author william.liangf
 */
public class ListenerInvokerWrapper<T> implements Invoker<T> {

    private static final Logger logger = LoggerFactory.getLogger(ListenerInvokerWrapper.class);

    private final Invoker<T> invoker;
    
    private final List<InvokerListener> listeners;

    public ListenerInvokerWrapper(Invoker<T> invoker, List<InvokerListener> listeners){
        if (invoker == null) {
            throw new IllegalArgumentException("invoker == null");
        }
        this.invoker = invoker;
        this.listeners = listeners;
        if (listeners != null && listeners.size() > 0) {
            for (InvokerListener listener : listeners) {
                if (listener != null) {
                    try {
                        listener.referred(invoker);
                    } catch (Throwable t) {
                        logger.error(t.getMessage(), t);
                    }
                }
            }
        }
    }

    public Class<T> getInterface() {
        return invoker.getInterface();
    }

    public URL getUrl() {
        return invoker.getUrl();
    }

    public boolean isAvailable() {
        return invoker.isAvailable();
    }

    public Result invoke(Invocation invocation) throws RpcException {
        return invoker.invoke(invocation);
    }
    
    @Override
    public String toString() {
        return getInterface() + " -> " + getUrl()==null?" ":getUrl().toString();
    }

    public void destroy() {
        try {
            invoker.destroy();
        } finally {
            if (listeners != null && listeners.size() > 0) {
                for (InvokerListener listener : listeners) {
                    if (listener != null) {
                        try {
                            listener.destroyed(invoker);
                        } catch (Throwable t) {
                            logger.error(t.getMessage(), t);
                        }
                    }
                }
            }
        }
    }

}