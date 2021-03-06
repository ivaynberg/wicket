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
// introduce a namespace, just to be nice
if (typeof (Wicket.CheckboxSelector.Checkboxes) == "undefined") {
	Wicket.CheckboxSelector.Checkboxes = {};
	/**
	 * Returns a closure that finds all checkboxes with the given IDs.
	 * 
	 * @param parentChoiceId
	 *            An array containing the markup IDs of all checkboxes this
	 *            selector should control.
	 */
	Wicket.CheckboxSelector.Checkboxes.findCheckboxesFunction = function(checkBoxIDs) {
		return function() {
			var result = new Array();
			for (i in checkBoxIDs) {
				var checkBox = Wicket.$(checkBoxIDs[i]);
				result.push(checkBox)
			}
			return result;
		}
	};
}
