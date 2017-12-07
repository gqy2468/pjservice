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

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.config.TypedStringValue;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.beans.factory.support.ManagedMap;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.lvmama.phprpc.common.utils.ReflectUtils;
import com.lvmama.phprpc.common.utils.StringUtils;
import com.lvmama.phprpc.config.RegistryConfig;
import com.lvmama.phprpc.config.spring.ServiceBean;

/**
 * AbstractBeanDefinitionParser
 * 
 * @author flashguo
 * @export
 */
public class PhprpcBeanDefinitionParser implements BeanDefinitionParser {
	
    private final Class<?> beanClass;
    
    private final boolean required;

    public PhprpcBeanDefinitionParser(Class<?> beanClass, boolean required) {
        this.beanClass = beanClass;
        this.required = required;
    }

    public BeanDefinition parse(Element element, ParserContext parserContext) {
        return parse(element, parserContext, beanClass, required);
    }
    
    private static BeanDefinition parse(Element element, ParserContext parserContext, Class<?> beanClass, boolean required) {
    	RootBeanDefinition beanDefinition = new RootBeanDefinition();
        beanDefinition.setBeanClass(beanClass);
        beanDefinition.setLazyInit(false);
        String id = element.getAttribute("id");
        if ((id == null || id.length() == 0) && required) {
        	String generatedBeanName = element.getAttribute("name");
        	if (generatedBeanName == null || generatedBeanName.length() == 0) {
        		generatedBeanName = element.getAttribute("interface");
        	}
        	if (generatedBeanName == null || generatedBeanName.length() == 0) {
        		generatedBeanName = beanClass.getName();
        	}
            id = generatedBeanName; 
            int counter = 2;
            while(parserContext.getRegistry().containsBeanDefinition(id)) {
                id = generatedBeanName + (counter ++);
            }
        }
        if (id != null && id.length() > 0) {
            if (parserContext.getRegistry().containsBeanDefinition(id))  {
        		throw new IllegalStateException("Duplicate spring bean id " + id);
        	}
            parserContext.getRegistry().registerBeanDefinition(id, beanDefinition);
            beanDefinition.getPropertyValues().addPropertyValue("id", id);
        }
        if (ServiceBean.class.equals(beanClass)) {
            String className = element.getAttribute("class");
            if(className != null && className.length() > 0) {
                RootBeanDefinition classDefinition = new RootBeanDefinition();
                classDefinition.setBeanClass(ReflectUtils.forName(className));
                classDefinition.setLazyInit(false);
                parseProperties(element.getChildNodes(), classDefinition);
                beanDefinition.getPropertyValues().addPropertyValue("ref", new BeanDefinitionHolder(classDefinition, id + "Impl"));
            }
        }
        Set<String> props = new HashSet<String>();
        ManagedMap<String, TypedStringValue> parameters = null;
        for (Method setter : beanClass.getMethods()) {
            String name = setter.getName();
            if (name.length() > 3 && name.startsWith("set")
                    && Modifier.isPublic(setter.getModifiers())
                    && setter.getParameterTypes().length == 1) {
                Class<?> type = setter.getParameterTypes()[0];
                String property = StringUtils.camelToSplitName(name.substring(3, 4).toLowerCase() + name.substring(4), "-");
                props.add(property);
                Method getter = null;
                try {
                    getter = beanClass.getMethod("get" + name.substring(3), new Class<?>[0]);
                } catch (NoSuchMethodException e) {
                    try {
                        getter = beanClass.getMethod("is" + name.substring(3), new Class<?>[0]);
                    } catch (NoSuchMethodException e2) {
                    }
                }
                if (getter == null 
                        || ! Modifier.isPublic(getter.getModifiers())
                        || ! type.equals(getter.getReturnType())) {
                    continue;
                }
                String value = element.getAttribute(property);
                if (value != null) {
                	value = value.trim();
                	if (value.length() > 0) {
                		if ("registry".equals(property) && RegistryConfig.NO_AVAILABLE.equalsIgnoreCase(value)) {
                        	RegistryConfig registryConfig = new RegistryConfig();
                        	registryConfig.setAddress(RegistryConfig.NO_AVAILABLE);
                        	beanDefinition.getPropertyValues().addPropertyValue(property, registryConfig);
                        } else if ("registry".equals(property) && value.indexOf(',') != -1) {
                			parseMultiRef("registries", value, beanDefinition, parserContext);
                        } else {
                            Object reference;
                            if (isPrimitive(type)) {
                                reference = value;
                            } else {
                                if ("ref".equals(property) && parserContext.getRegistry().containsBeanDefinition(value)) {
                                    BeanDefinition refBean = parserContext.getRegistry().getBeanDefinition(value);
                                    if (! refBean.isSingleton()) {
                                        throw new IllegalStateException("The exported service ref " + value + " must be singleton! Please set the " + value + " bean scope to singleton, eg: <bean id=\"" + value+ "\" scope=\"singleton\" ...>");
                                    }
                                }
                                reference = new RuntimeBeanReference(value);
                            }
	                        beanDefinition.getPropertyValues().addPropertyValue(property, reference);
                        }
                	}
                }
            }
        }
        NamedNodeMap attributes = element.getAttributes();
        int len = attributes.getLength();
        for (int i = 0; i < len; i++) {
            Node node = attributes.item(i);
            String name = node.getLocalName();
            if (! props.contains(name)) {
                if (parameters == null) {
                    parameters = new ManagedMap<String, TypedStringValue>();
                }
                String value = node.getNodeValue();
                parameters.put(name, new TypedStringValue(value, String.class));
            }
        }
        if (parameters != null) {
            beanDefinition.getPropertyValues().addPropertyValue("parameters", parameters);
        }
    	return beanDefinition;
    }
 
    private static boolean isPrimitive(Class<?> cls) {
        return cls.isPrimitive() || cls == Boolean.class || cls == Byte.class
                || cls == Character.class || cls == Short.class || cls == Integer.class
                || cls == Long.class || cls == Float.class || cls == Double.class
                || cls == String.class || cls == Date.class || cls == Class.class;
    }
    
    private static void parseMultiRef(String property, String value, RootBeanDefinition beanDefinition,
            ParserContext parserContext) {
    	String[] values = value.split("\\s*[,]+\\s*");
		ManagedList<RuntimeBeanReference> list = null;
        for (int i = 0; i < values.length; i++) {
            String v = values[i];
            if (v != null && v.length() > 0) {
            	if (list == null) {
                    list = new ManagedList<RuntimeBeanReference>();
                }
            	list.add(new RuntimeBeanReference(v));
            }
        }
        beanDefinition.getPropertyValues().addPropertyValue(property, list);
    }

    private static void parseProperties(NodeList nodeList, RootBeanDefinition beanDefinition) {
        if (nodeList != null && nodeList.getLength() > 0) {
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                if (node instanceof Element) {
                    if ("property".equals(node.getNodeName())
                            || "property".equals(node.getLocalName())) {
                        String name = ((Element) node).getAttribute("name");
                        if (name != null && name.length() > 0) {
                            String value = ((Element) node).getAttribute("value");
                            String ref = ((Element) node).getAttribute("ref");
                            if (value != null && value.length() > 0) {
                                beanDefinition.getPropertyValues().addPropertyValue(name, value);
                            } else if (ref != null && ref.length() > 0) {
                                beanDefinition.getPropertyValues().addPropertyValue(name, new RuntimeBeanReference(ref));
                            } else {
                                throw new UnsupportedOperationException("Unsupported <property name=\"" + name + "\"> sub tag, Only supported <property name=\"" + name + "\" ref=\"...\" /> or <property name=\"" + name + "\" value=\"...\" />");
                            }
                        }
                    }
                }
            }
        }
}

}