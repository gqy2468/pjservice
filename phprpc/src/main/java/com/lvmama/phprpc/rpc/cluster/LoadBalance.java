/*
 * Copyright 1999-2011 Alibaba Group.
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
package com.lvmama.phprpc.rpc.cluster;

import java.util.List;

import com.lvmama.phprpc.common.URL;
import com.lvmama.phprpc.common.extension.Adaptive;
import com.lvmama.phprpc.common.extension.SPI;
import com.lvmama.phprpc.rpc.Invocation;
import com.lvmama.phprpc.rpc.Invoker;
import com.lvmama.phprpc.rpc.RpcException;
import com.lvmama.phprpc.rpc.cluster.loadbalance.RandomLoadBalance;

/**
 * LoadBalance. (SPI, Singleton, ThreadSafe)
 * 
 * <a href="http://en.wikipedia.org/wiki/Load_balancing_(computing)">Load-Balancing</a>
 * 
 * @see com.lvmama.phprpc.rpc.cluster.Cluster#join(Directory)
 * @author qian.lei
 * @author william.liangf
 */
@SPI(RandomLoadBalance.NAME)
public interface LoadBalance {

	/**
	 * select one invoker in list.
	 * 
	 * @param invokers invokers.
	 * @param url refer url
	 * @param invocation invocation.
	 * @return selected invoker.
	 */
    @Adaptive("loadbalance")
	<T> Invoker<T> select(List<Invoker<T>> invokers, URL url, Invocation invocation) throws RpcException;

}