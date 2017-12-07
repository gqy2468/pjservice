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
package com.lvmama.phprpc.monitor;

import com.lvmama.phprpc.common.URL;
import com.lvmama.phprpc.common.extension.Adaptive;
import com.lvmama.phprpc.common.extension.SPI;

/**
 * MonitorFactory. (SPI, Singleton, ThreadSafe)
 * 
 * @author william.liangf
 */
@SPI("thrift")
public interface MonitorFactory {
    
    /**
     * Create monitor.
     * 
     * @param url
     * @return monitor
     */
    @Adaptive("protocol")
    Monitor getMonitor(URL url);

}