/**
 * Copyright 2017 伊永飞
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.yea.core.remote.struct;

import com.yea.core.base.model.BaseModel;

public class CallAct extends BaseModel {
	private static final long serialVersionUID = 1L;
	
	private String actName;//服务端对应的Act定义BeanName，必须
	private String callbackName;//本地对应回调用的ActBeanName，非必须，本地需要异步回调时设置
	
	public String getActName() {
		return actName;
	}
	public void setActName(String actName) {
		this.actName = actName;
	}
	public String getCallbackName() {
		return callbackName;
	}
	public void setCallbackName(String callbackName) {
		this.callbackName = callbackName;
	}
}
