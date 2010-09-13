/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
 
/*
 * Wicket ClientSide Validation Support 
 *
 * @author Igor Vaynberg
 */
 
if (Function.prototype.bind == null) {
	Function.prototype.bind = function(object) {
		var __method = this;
		return function() {
			return __method.apply(object, arguments);
		}
	}
}

if (typeof(Wicket) == "undefined")
	Wicket = { };

if (typeof(Wicket.$) == "undefined") {
	Wicket.$ = function(arg) {
		if (arg == null || typeof(arg) == "undefined") {
			return null;
		}
		if (arguments.length > 1) {
			var e=[];
			for (var i=0; i<arguments.length; i++) {
				e.push(Wicket.$(arguments[i]));
			}
			return e;
		} else if (typeof arg == 'string') {
			return document.getElementById(arg);
		} else {
			return arg;
		}
	}
}

Wicket.Class = {
	create: function() {
		return function() {
			this.initialize.apply(this, arguments);
		}
	}
}

Wicket.Validation = {
	definitions: {},

	listener: function(element, message) { alert(message); },
	
	define: function(name, rule) {
		Wicket.Validation.definitions[name]=rule;
	},
	
	validate: function(element, ruleName, args) {
		var element=Wicket.$(element);

		var rule=Wicket.Validation.definitions[ruleName];
		
		var errors=rule.validate(element, args);
		
		if (errors!=null&&errors.length>0) {
			for (var i in errors) {
				Wicket.Validation.error(element, errors[i]);
			}
		}
	},
	
	error:function (element, message) { alert(message); }
}
