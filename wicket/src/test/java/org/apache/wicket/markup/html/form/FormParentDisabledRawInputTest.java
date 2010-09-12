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
package org.apache.wicket.markup.html.form;

import junit.framework.TestCase;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.util.tester.WicketTester;

public class FormParentDisabledRawInputTest extends TestCase
{
	private WicketTester tester;

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		tester = new WicketTester();
	}

	public static class TestPage extends WebPage
	{
		public boolean property = true;
		public boolean enabled = true;

		public TestPage()
		{
			WebMarkupContainer container = new WebMarkupContainer("container")
			{
				@Override
				public boolean isEnabled()
				{
					return enabled;
				};
			};
			Form<?> form = new Form<Void>("form");
			container.add(form);
			form.add(new CheckBox("check", new PropertyModel<Boolean>(this, "property")));
			add(container);
		}
	}

	public void testDisabledParent() throws Exception
	{
		TestPage page = new TestPage();
		page.enabled = false;
		tester.startPage(page);
		tester.assertContains("checked=\"checked\"");
		tester.assertContains("disabled=\"disabled\"");
		Component check = tester.getComponentFromLastRenderedPage("container:form:check");
		assertTrue(check.isEnabled());
		assertFalse(check.isEnabledInHierarchy());

		// nothing should change with a submit that changes no values
		tester.newFormTester("container:form").submit();
		check = tester.getComponentFromLastRenderedPage("container:form:check");
		assertTrue(check.isEnabled());
		assertFalse(check.isEnabledInHierarchy());
		tester.assertContains("disabled=\"disabled\"");
		tester.assertContains("checked=\"checked\"");
	}
}
