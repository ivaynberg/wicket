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
package org.apache.wicket.request.resource;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServletResponse;

import org.apache.wicket.Application;
import org.apache.wicket.util.lang.Args;
import org.apache.wicket.util.lang.Bytes;
import org.apache.wicket.util.resource.IResourceStream;
import org.apache.wicket.util.resource.IResourceStreamWriter;
import org.apache.wicket.util.resource.ResourceStreamNotFoundException;
import org.apache.wicket.util.time.Duration;
import org.apache.wicket.util.time.Time;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * TODO javadoc
 */
public class ResourceStreamResource extends AbstractResource
{
	private static final long serialVersionUID = 1L;

	private static final Logger logger = LoggerFactory.getLogger(ResourceStreamResource.class);

	private final IResourceStream stream;
	private String fileName;
	private ContentDisposition contentDisposition = ContentDisposition.INLINE;
	private String textEncoding;
	private String mimeType;

	private Duration cacheDuration;

	/**
	 * Construct.
	 * 
	 * @param stream
	 */
	public ResourceStreamResource(IResourceStream stream)
	{
		Args.notNull(stream, "stream");
		this.stream = stream;
	}

	/**
	 * @param fileName
	 * @return this
	 */
	public ResourceStreamResource setFileName(String fileName)
	{
		this.fileName = fileName;
		return this;
	}

	/**
	 * @param contentDisposition
	 * @return thsi
	 */
	public ResourceStreamResource setContentDisposition(ContentDisposition contentDisposition)
	{
		this.contentDisposition = contentDisposition;
		return this;
	}

	/**
	 * @param textEncoding
	 * @return this
	 */
	public ResourceStreamResource setTextEncoding(String textEncoding)
	{
		this.textEncoding = textEncoding;
		return this;
	}

	/**
	 * @return the duration for which the resource will be cached by the browser
	 */
	public Duration getCacheDuration()
	{
		return cacheDuration;
	}

	/**
	 * @param cacheDuration
	 *            the duration for which the resource will be cached by the browser
	 * @return this component
	 */
	public ResourceStreamResource setCacheDuration(Duration cacheDuration)
	{
		this.cacheDuration = cacheDuration;
		return this;
	}

	@Override
	protected ResourceResponse newResourceResponse(Attributes attributes)
	{
		ResourceResponse data = new ResourceResponse();
		Time lastModifiedTime = stream.lastModifiedTime();
		if (lastModifiedTime != null)
		{
			data.setLastModified(lastModifiedTime);
		}

		// performance check; don't bother to do anything if the resource is still cached by client
		if (data.dataNeedsToBeWritten(attributes))
		{
			InputStream inputStream = null;
			if (stream instanceof IResourceStreamWriter == false)
			{
				try
				{
					inputStream = stream.getInputStream();
				}
				catch (ResourceStreamNotFoundException e)
				{
					data.setError(HttpServletResponse.SC_NOT_FOUND);
					close();
				}
			}

			data.setContentDisposition(contentDisposition);
			Bytes length = stream.length();
			if (length != null)
			{
				data.setContentLength(length.bytes());
			}
			data.setFileName(fileName);

			final String contentType;
			if (fileName != null && Application.exists())
			{
				contentType = Application.get().getMimeType(fileName);
			}
			else
			{
				contentType = stream.getContentType();
			}
			data.setContentType(contentType);
			data.setTextEncoding(textEncoding);

			if (cacheDuration != null)
			{
				data.setCacheDuration(cacheDuration);
			}

			if (stream instanceof IResourceStreamWriter)
			{
				data.setWriteCallback(new WriteCallback()
				{
					@Override
					public void writeData(Attributes attributes)
					{
						((IResourceStreamWriter)stream).write(attributes.getResponse());
						close();
					}
				});
			}
			else
			{
				final InputStream s = inputStream;
				data.setWriteCallback(new WriteCallback()
				{
					@Override
					public void writeData(Attributes attributes)
					{
						try
						{
							writeStream(attributes, s);
						}
						finally
						{
							close();
						}
					}
				});
			}
		}

		return data;
	}

	private void close()
	{
		try
		{
			stream.close();
		}
		catch (IOException e)
		{
			logger.error("Couldn't close ResourceStream", e);
		}
	}
}
