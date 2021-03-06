/*
 * Copyright 2013 the original author or authors.
 * Copyright 2013 SorcerSoft.org.
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

package sorcer.co.tuple;

import sorcer.service.Path;

import java.io.Serializable;


public class OutType extends Path implements Serializable {

	private static final long serialVersionUID = 1L;

	public OutType() { }

	public OutType(Object type) {
		this.info = type;
		this.type = Type.OUT;
	}

	public OutType(String path, Object type) {
		this.path = path;
		this.info = type;
		this.type = Type.OUT;
	}
	
	public String path() {
		return path;
	}
}