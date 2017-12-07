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

import java.util.Map;

/**
 * RPC invoke result. (API, Prototype, NonThreadSafe)
 * 
 * @serial Don't change the class name and package name.
 * @see com.lvmama.phprpc.rpc.Invoker#invoke(Invocation)
 * @see com.lvmama.phprpc.rpc.RpcResult
 * @author qianlei
 * @author william.liangf
 */
public interface Result {

	/**
	 * Get invoke result.
	 * 
	 * @return result. if no result return null.
	 */
	Object getValue();

	/**
	 * Get exception.
	 * 
	 * @return exception. if no exception return null.
	 */
	Throwable getException();

    /**
     * Has exception.
     * 
     * @return has exception.
     */
    boolean hasException();

    /**
     * Recreate.
     * 
     * <code>
     * if (hasException()) {
     *     throw getException();
     * } else {
     *     return getValue();
     * }
     * </code>
     * 
     * @return result.
     * @throws if has exception throw it.
     */
    Object recreate() throws Throwable;

    /**
     * @deprecated Replace to getValue()
     * @see com.lvmama.phprpc.rpc.Result#getValue()
     */
    @Deprecated
    Object getResult();


    /**
     * get attachments.
     *
     * @return attachments.
     */
    Map<String, String> getAttachments();

    /**
     * get attachment by key.
     *
     * @return attachment value.
     */
    String getAttachment(String key);

    /**
     * get attachment by key with default value.
     *
     * @return attachment value.
     */
    String getAttachment(String key, String defaultValue);

}