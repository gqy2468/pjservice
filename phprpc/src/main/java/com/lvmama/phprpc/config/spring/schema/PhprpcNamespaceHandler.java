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
package com.lvmama.phprpc.config.spring.schema;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

import com.lvmama.phprpc.config.RegistryConfig;
import com.lvmama.phprpc.config.spring.ReferenceBean;
import com.lvmama.phprpc.config.spring.ServiceBean;
import com.lvmama.phprpc.config.MonitorConfig;
import com.lvmama.phprpc.config.ProtocolConfig;

/**
 * ThriftNamespaceHandler
 * 
 * @author flashguo
 * @export
 */
public class PhprpcNamespaceHandler extends NamespaceHandlerSupport {

	static {
	}

	public void init() {
        registerBeanDefinitionParser("registry", new PhprpcBeanDefinitionParser(RegistryConfig.class, true));
        registerBeanDefinitionParser("monitor", new PhprpcBeanDefinitionParser(MonitorConfig.class, true));
        registerBeanDefinitionParser("service", new PhprpcBeanDefinitionParser(ServiceBean.class, true));
        registerBeanDefinitionParser("protocol", new PhprpcBeanDefinitionParser(ProtocolConfig.class, true));
        registerBeanDefinitionParser("reference", new PhprpcBeanDefinitionParser(ReferenceBean.class, false));
    }

}