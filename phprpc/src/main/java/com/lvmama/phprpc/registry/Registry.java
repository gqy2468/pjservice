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
package com.lvmama.phprpc.registry;

import com.lvmama.phprpc.common.Node;
import com.lvmama.phprpc.common.URL;

/**
 * Registry. (SPI, Prototype, ThreadSafe)
 * 
 * @see com.lvmama.phprpc.registry.RegistryFactory#getRegistry(URL)
 * @see com.lvmama.phprpc.registry.support.AbstractRegistry
 * @author william.liangf
 */
public interface Registry extends Node, RegistryService {
}