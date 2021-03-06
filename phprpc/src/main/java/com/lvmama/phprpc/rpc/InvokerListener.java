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

import com.lvmama.phprpc.common.URL;
import com.lvmama.phprpc.common.extension.SPI;

/**
 * InvokerListener. (SPI, Singleton, ThreadSafe)
 * 
 * @author william.liangf
 */
@SPI
public interface InvokerListener {

    /**
     * The invoker referred
     * 
     * @see com.lvmama.phprpc.rpc.Protocol#refer(Class, URL)
     * @param invoker
     * @throws RpcException
     */
    void referred(Invoker<?> invoker) throws RpcException;

    /**
     * The invoker destroyed.
     * 
     * @see com.lvmama.phprpc.rpc.Invoker#destroy()
     * @param invoker
     */
    void destroyed(Invoker<?> invoker);

}