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
package com.lvmama.phprpc.rpc.cluster.support;

import java.util.List;

import com.lvmama.phprpc.rpc.Invocation;
import com.lvmama.phprpc.rpc.Invoker;
import com.lvmama.phprpc.rpc.Result;
import com.lvmama.phprpc.rpc.RpcException;
import com.lvmama.phprpc.rpc.cluster.Cluster;
import com.lvmama.phprpc.rpc.cluster.Directory;
import com.lvmama.phprpc.rpc.cluster.LoadBalance;

/**
 * AvailableCluster
 * 
 * @author william.liangf
 */
public class AvailableCluster implements Cluster {
    
    public static final String NAME = "available";

    public <T> Invoker<T> join(Directory<T> directory) throws RpcException {
        
        return new AbstractClusterInvoker<T>(directory) {
            public Result doInvoke(Invocation invocation, List<Invoker<T>> invokers, LoadBalance loadbalance) throws RpcException {
                for (Invoker<T> invoker : invokers) {
                    if (invoker.isAvailable()) {
                        return invoker.invoke(invocation);
                    }
                }
                throw new RpcException("No provider available in " + invokers);
            }
        };
        
    }

}