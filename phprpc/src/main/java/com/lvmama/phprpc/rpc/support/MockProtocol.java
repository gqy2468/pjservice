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
package com.lvmama.phprpc.rpc.support;

import com.lvmama.phprpc.common.URL;
import com.lvmama.phprpc.rpc.Exporter;
import com.lvmama.phprpc.rpc.Invoker;
import com.lvmama.phprpc.rpc.RpcException;
import com.lvmama.phprpc.rpc.protocol.AbstractProtocol;

/**
 * MockProtocol 用于在consumer side 通过url及类型生成一个mockInvoker
 * @author chao.liuc
 *
 */
final public class MockProtocol extends AbstractProtocol {

	public int getDefaultPort() {
		return 0;
	}

	public <T> Exporter<T> export(Invoker<T> invoker) throws RpcException {
		throw new UnsupportedOperationException();
	}

	public <T> Invoker<T> refer(Class<T> type, URL url) throws RpcException {
		return new MockInvoker<T>(url);
	}
}
