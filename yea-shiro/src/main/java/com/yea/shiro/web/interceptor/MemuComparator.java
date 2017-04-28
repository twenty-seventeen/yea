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
package com.yea.shiro.web.interceptor;

import java.util.Comparator;

import com.yea.core.base.model.BaseModel;

class MemuComparator<Menu> extends BaseModel implements Comparator<Menu>{
	private static final long serialVersionUID = 1L;

	public int compare(Menu menu1, Menu menu2) {
		if (((com.yea.core.shiro.model.Menu) menu1).getMenuSequence() == null) {
			return -1;
		} else if (((com.yea.core.shiro.model.Menu) menu2).getMenuSequence() == null) {
			return 1;
		} else if (((com.yea.core.shiro.model.Menu) menu1).getMenuSequence() > ((com.yea.core.shiro.model.Menu) menu2)
				.getMenuSequence()) {
			return 1;
		} else {
			return -1;
		}
	}
}
