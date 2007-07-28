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
package org.apache.wicket.extensions.markup.html.form.palette.component;

import java.util.Iterator;

import org.apache.wicket.extensions.markup.html.form.palette.Palette;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.util.value.IValueMap;


/**
 * select box containg all available choices of the palette
 * 
 * @author Igor Vaynberg ( ivaynberg )
 * 
 */
public class Choices extends AbstractOptions
{
	private static final long serialVersionUID = 1L;

	/**
	 * @param id component id
	 * @param palette parent palette
	 */
	public Choices(String id, Palette palette)
	{
		super(id, palette);
	}


	protected void onComponentTag(ComponentTag tag)
	{
		super.onComponentTag(tag);
		IValueMap attrs = tag.getAttributes();
		String onFocus = getPalette().getChoicesOnFocusJS();
		if (onFocus != null)
		{
			attrs.put("onFocus", onFocus);
		}
		
		tag.getAttributes().put("ondblclick", getPalette().getAddOnClickJS()); 
	}

	protected Iterator getOptionsIterator()
	{
		return getPalette().getUnselectedChoices();
	}

}