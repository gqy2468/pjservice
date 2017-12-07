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
package com.lvmama.phprpc.rpc;

import com.lvmama.phprpc.common.Node;

/**
 * Invoker. (API/SPI, Prototype, ThreadSafe)
 * 
 * @see com.lvmama.phprpc.rpc.Protocol#refer(Class, com.lvmama.phprpc.common.URL)
 * @see com.lvmama.phprpc.rpc.InvokerListener
 * @see com.lvmama.phprpc.rpc.protocol.AbstractInvoker
 * @author william.liangf
 */
public interface Invoker<T> extends Node {

    /**
     * get service interface.
     * 
     * @return service interface.
     */
    Class<T> getInterface();

    /**
     * invoke.
     * 
     * @param invocation
     * @return result
     * @throws RpcException
     */
    Result invoke(Invocation invocation) throws RpcException;

}