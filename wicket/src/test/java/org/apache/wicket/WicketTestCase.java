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
package org.apache.wicket;

import java.io.IOException;

import junit.framework.TestCase;

import org.apache.wicket.behavior.AbstractAjaxBehavior;
import org.apache.wicket.markup.IMarkupFragment;
import org.apache.wicket.markupFragments.MyPage;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.tester.DiffUtil;
import org.apache.wicket.util.tester.WicketTester;

/**
 * Base class for tests which require comparing wicket response with a file.
 * <p>
 * To create/replace the expected result file with the new content, define the system property like
 * -Dwicket.replace.expected.results=true
 */
public abstract class WicketTestCase extends TestCase
{
	/** */
	public WicketTester tester;

	/**
	 * Construct.
	 */
	public WicketTestCase()
	{
	}

	/**
	 * Construct.
	 * 
	 * @param name
	 */
	public WicketTestCase(String name)
	{
		super(name);
	}

	/**
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception
	{
		tester = new WicketTester();
	}

	/**
	 * @see junit.framework.TestCase#tearDown()
	 */
	@Override
	protected void tearDown() throws Exception
	{
		tester.destroy();
	}

	/**
	 * Use <code>-Dwicket.replace.expected.results=true</code> to automatically replace the expected
	 * output file.
	 * 
	 * @param <T>
	 * 
	 * @param pageClass
	 * @param filename
	 * @throws Exception
	 */
	protected <T extends Page> void executeTest(final Class<T> pageClass, final String filename)
		throws Exception
	{
		tester.executeTest(getClass(), pageClass, filename);
	}

	/**
	 * Use <code>-Dwicket.replace.expected.results=true</code> to automatically replace the expected
	 * output file.
	 * 
	 * @param page
	 * @param filename
	 * @throws Exception
	 */
	protected void executeTest(final Page page, final String filename) throws Exception
	{
		tester.executeTest(getClass(), page, filename);
	}

	/**
	 * Use <code>-Dwicket.replace.expected.results=true</code> to automatically replace the expected
	 * output file.
	 * 
	 * @param <T>
	 * 
	 * @param pageClass
	 * @param parameters
	 * @param filename
	 * @throws Exception
	 */
	protected <T extends Page> void executeTest(final Class<T> pageClass,
		PageParameters parameters, final String filename) throws Exception
	{
		tester.executeTest(getClass(), pageClass, parameters, filename);
	}

	/**
	 * 
	 * @param component
	 * @param filename
	 * @throws Exception
	 */
	protected void executeListener(final Component component, final String filename)
		throws Exception
	{
		tester.executeListener(getClass(), component, filename);
	}

	/**
	 * 
	 * @param behavior
	 * @param filename
	 * @throws Exception
	 */
	protected void executeBehavior(final AbstractAjaxBehavior behavior, final String filename)
		throws Exception
	{
		tester.executeBehavior(getClass(), behavior, filename);
	}

	/**
	 * Returns the current Maven build directory taken from the <tt>basedir</tt> system property, or
	 * null if not set
	 * 
	 * @return path with a trailing slash
	 */
	public String getBasedir()
	{
		return WicketTester.getBasedir();
	}

	/**
	 * Compare the markup provided with the file content
	 * 
	 * @param markup
	 * @param filename
	 * @throws IOException
	 */
	public final void compareMarkupWithFile(IMarkupFragment markup, String filename)
		throws IOException
	{
		String doc = markup.toString(true);
		DiffUtil.validatePage(doc, MyPage.class, filename, true);
	}

	/**
	 * Compare the markup provided with the String
	 * 
	 * @param markup
	 * @param testMarkup
	 * @throws IOException
	 */
	public final void compareMarkupWithString(IMarkupFragment markup, String testMarkup)
		throws IOException
	{
		testMarkup = testMarkup.replaceAll("\r", "");
		testMarkup = testMarkup.replaceAll("\n", "");
		testMarkup = testMarkup.replaceAll("\t", "");

		String doc = markup.toString(true);
		doc = doc.replaceAll("\n", "");
		doc = doc.replaceAll("\r", "");
		doc = doc.replaceAll("\t", "");
		assertEquals(doc, testMarkup);
	}
}
