/**
 * 
 */
package wicket.resource;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

import wicket.markup.html.WebResource;
import wicket.util.resource.IResourceStream;
import wicket.util.resource.ResourceStreamNotFoundException;
import wicket.util.time.Duration;
import wicket.util.time.Time;

/**
 * @author jcompagner
 *
 */
public abstract class DynamicByteArrayResource extends WebResource
{
	/** The time this image resource was last modified */
	private Time lastModifiedTime;

	/** The maximum duration a resource can be idle before its cache is flushed */
	private Duration cacheTimeout = Duration.NONE;

	/** The content type of this byte array */
	private String contentType;
	
	private Locale locale;
	
	/**
	 * @param contentType
	 */
	public DynamicByteArrayResource(String contentType)
	{
		this.contentType = contentType;
	}

	/**
	 * @param contentType The Content type of this resource.
	 * @param locale  The Locale for which this resource is meant for.
	 */
	public DynamicByteArrayResource(String contentType, Locale locale)
	{
		this.contentType = contentType;
		this.locale = locale;
	}
	
	/**
	 * @return Gets the image resource to attach to the component.
	 */
	public IResourceStream getResourceStream()
	{
		return new IResourceStream()
		{
			/** Transient input stream to resource */
			private transient InputStream inputStream = null;
			
			/** Transient byte array of the resources, will always be deleted in the close*/
			private transient byte[] data = null; 

			/**
			 * @see wicket.util.resource.IResourceStream#close()
			 */
			public void close() throws IOException
			{
				if (inputStream != null)
				{
					inputStream.close();
					inputStream = null;
				}
				data = null;
			}

			/**
			 * @see wicket.util.resource.IResourceStream#getContentType()
			 */
			public String getContentType()
			{
				return contentType;
			}

			/**
			 * @see wicket.util.resource.IResourceStream#getInputStream()
			 */
			public InputStream getInputStream() throws ResourceStreamNotFoundException
			{
				if(data == null)
				{
					data = getData();
				}
				if (inputStream == null)
				{
					inputStream = new ByteArrayInputStream(data);
				}
				return inputStream;
			}

			/**
			 * @see wicket.util.watch.IModifiable#lastModifiedTime()
			 */
			public Time lastModifiedTime()
			{
				return DynamicByteArrayResource.this.lastModifiedTime();
			}

			public long length()
			{
				if(data == null)
				{
					data = getData();
				}
				return (data != null) ? data.length : 0;
			}
			
			public Locale getLocale()
			{
				return locale;
			}
			
			public void setLocale(Locale loc)
			{
				DynamicByteArrayResource.this.locale = loc;
			}
		};
	}

	/**
	 * @return The last time this image resource was modified
	 */
	public final Time lastModifiedTime()
	{
		return lastModifiedTime;
	}

	/**
	 * @param lastModifiedTime 
	 */
	public final void setLastModifiedTime(Time lastModifiedTime)
	{
		this.lastModifiedTime = lastModifiedTime;
	}


	/**
	 * @return The content type of the byte array
	 */
	public final String getContentType()
	{
		return this.contentType;
	}

	/**
	 * @param contentType The content type of the byte array
	 */
	public final void setContentType(String contentType)
	{
		this.contentType = contentType;
	}
	
	
	/**
	 * Set the maximum duration the resource can be idle before its cache is flushed.
	 * The cache might get flushed sooner if the JVM is low on memory.
	 * 
	 * @param value The cache timout 
	 */
	public final void setCacheTimeout(Duration value)
	{
		cacheTimeout = value;
	}

	/**
	 * Returns the maximum duration the resource can be idle before its cache is flushed.
	 * 
	 * @return The cache timeout 
	 */
	public final Duration getCacheTimeout()
	{
		return cacheTimeout;
	}

	/**
	 * Get byte array for our dynamic resource. If the subclass
	 * regenerates the data, it should set the lastModifiedTime when it does so.
	 * This ensures that image caching works correctly.
	 *
	 * @return The byte array for this dynamic resource.
	 */
	protected abstract byte[] getData();
}