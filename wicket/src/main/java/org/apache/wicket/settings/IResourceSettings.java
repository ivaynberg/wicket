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
package org.apache.wicket.settings;

import java.util.List;

import org.apache.wicket.IResourceFactory;
import org.apache.wicket.Localizer;
import org.apache.wicket.javascript.IJavascriptCompressor;
import org.apache.wicket.markup.html.IPackageResourceGuard;
import org.apache.wicket.markup.html.PackageResourceGuard;
import org.apache.wicket.model.IModel;
import org.apache.wicket.resource.IPropertiesFactory;
import org.apache.wicket.resource.loader.IStringResourceLoader;
import org.apache.wicket.util.file.IResourceFinder;
import org.apache.wicket.util.resource.locator.IResourceStreamLocator;
import org.apache.wicket.util.time.Duration;
import org.apache.wicket.util.watch.IModificationWatcher;


/**
 * Interface for resource related settings
 * <p>
 * <i>resourcePollFrequency </i> (defaults to no polling frequency) - Frequency at which resources
 * should be polled for changes.
 * <p>
 * <i>resourceFinder </i> (classpath) - Set this to alter the search path for resources.
 * <p>
 * <i>useDefaultOnMissingResource </i> (defaults to true) - Set to true to return a default value if
 * available when a required string resource is not found. If set to false then the
 * throwExceptionOnMissingResource flag is used to determine how to behave. If no default is
 * available then this is the same as if this flag were false
 * <p>
 * <i>A ResourceStreamLocator </i>- An Application's ResourceStreamLocator is used to find resources
 * such as images or markup files. You can supply your own ResourceStreamLocator if your prefer to
 * store your application's resources in a non-standard location (such as a different filesystem
 * location, a particular JAR file or even a database) by overriding the getResourceLocator()
 * method.
 * <p>
 * <i>Resource Factories </i>- Resource factories can be used to create resources dynamically from
 * specially formatted HTML tag attribute values. For more details, see {@link IResourceFactory},
 * {@link org.apache.wicket.markup.html.image.resource.DefaultButtonImageResourceFactory} and
 * especially {@link org.apache.wicket.markup.html.image.resource.LocalizedImageResource}.
 * <p>
 * <i>A Localizer </i> The getLocalizer() method returns an object encapsulating all of the
 * functionality required to access localized resources. For many localization problems, even this
 * will not be required, as there are convenience methods available to all components:
 * {@link org.apache.wicket.Component#getString(String key)} and
 * {@link org.apache.wicket.Component#getString(String key, IModel model)}.
 * <p>
 * <i>stringResourceLoaders </i>- A chain of <code>IStringResourceLoader</code> instances that are
 * searched in order to obtain string resources used during localization. By default the chain is
 * set up to first search for resources against a particular component (e.g. page etc.) and then
 * against the application.
 * </p>
 * 
 * @author Igor Vaynberg (ivaynberg)
 */
public interface IResourceSettings
{
	/**
	 * Adds a resource factory to the list of factories to consult when generating resources
	 * automatically
	 * 
	 * @param name
	 *            The name to give to the factory
	 * @param resourceFactory
	 *            The resource factory to add
	 */
	void addResourceFactory(final String name, final IResourceFactory resourceFactory);

	/**
	 * Convenience method that sets the resource search path to a single folder. use when searching
	 * for resources. By default, the resources are located on the classpath. If you want to
	 * configure other, additional, search paths, you can use this method
	 * 
	 * @param resourceFolder
	 *            The resourceFolder to set
	 */
	void addResourceFolder(final String resourceFolder);

	/**
	 * Add a string resource loader to the chain of loaders.
	 * 
	 * @see #addStringResourceLoader(int, IStringResourceLoader)
	 * @see #getStringResourceLoaders()
	 * 
	 * @param loader
	 *            The loader to be added
	 */
	void addStringResourceLoader(final IStringResourceLoader loader);

	/**
	 * Add a string resource loader to the chain of loaders.
	 * 
	 * @see #addStringResourceLoader(IStringResourceLoader)
	 * @see #getStringResourceLoaders()
	 * 
	 * @param index
	 *            The position within the array to insert the loader
	 * @param loader
	 *            The loader to be added
	 */
	void addStringResourceLoader(final int index, final IStringResourceLoader loader);

	/**
	 * Get the the default cache duration for resources.
	 * <p/>
	 *
	 * @return cache duration (Duration.NONE will be returned if caching is disabled)
	 *
	 * @see org.apache.wicket.util.time.Duration#NONE
	 */
	Duration getDefaultCacheDuration();

	/**
	 * Whether to disable gzip compression for resources. You need this on SAP, which gzips things
	 * twice.
	 * 
	 * @return True if we should disable gzip compression
	 * @since 1.3.0
	 */
	boolean getDisableGZipCompression();

	/**
	 * Get the application's localizer.
	 * 
	 * @see IResourceSettings#addStringResourceLoader(org.apache.wicket.resource.loader.IStringResourceLoader)
	 *      for means of extending the way Wicket resolves keys to localized messages.
	 * 
	 * @return The application wide localizer instance
	 */
	Localizer getLocalizer();

	/**
	 * Gets the {@link PackageResourceGuard package resource guard}.
	 * 
	 * @return The package resource guard
	 */
	IPackageResourceGuard getPackageResourceGuard();

	/**
	 * Get the property factory which will be used to load property files
	 * 
	 * @return PropertiesFactory
	 */
	IPropertiesFactory getPropertiesFactory();

	/**
	 * @param name
	 *            Name of the factory to get
	 * @return The IResourceFactory with the given name.
	 */
	IResourceFactory getResourceFactory(final String name);

	/**
	 * Gets the resource finder to use when searching for resources.
	 * 
	 * @return Returns the resourceFinder.
	 * @see IResourceSettings#setResourceFinder(IResourceFinder)
	 */
	IResourceFinder getResourceFinder();

	/**
	 * @return Returns the resourcePollFrequency.
	 * @see IResourceSettings#setResourcePollFrequency(Duration)
	 */
	Duration getResourcePollFrequency();

	/**
	 * @return Resource locator for this application
	 */
	IResourceStreamLocator getResourceStreamLocator();

	/**
	 * @param start
	 *            boolean if the resource watcher should be started if not already started.
	 * 
	 * @return Resource watcher with polling frequency determined by setting, or null if no polling
	 *         frequency has been set.
	 */
	IModificationWatcher getResourceWatcher(boolean start);

	/**
	 * @see #addStringResourceLoader(IStringResourceLoader)
	 * @see #addStringResourceLoader(int, IStringResourceLoader)
	 * 
	 * @return an unmodifiable list of all available string resource loaders
	 */
	List<IStringResourceLoader> getStringResourceLoaders();

	/**
	 * @see org.apache.wicket.settings.IExceptionSettings#getThrowExceptionOnMissingResource()
	 * 
	 * @return boolean
	 */
	boolean getThrowExceptionOnMissingResource();

	/**
	 * @return Whether to use a default value (if available) when a missing resource is requested
	 */
	boolean getUseDefaultOnMissingResource();

	/**
	 * Set the the default cache duration for resources.
	 * <p/>
	 * Based on RFC-2616 this should not exceed one year. If you set Duration.NONE caching will be disabled.
	 *
	 * @param defaultDuration
	 *            default cache duration in seconds
	 *
	 * @see org.apache.wicket.util.time.Duration#NONE
	 * @see org.apache.wicket.protocol.http.RequestUtils#MAX_CACHE_DURATION
	 */
	void setDefaultCacheDuration(Duration defaultDuration);

	/**
	 * Sets whether to disable gzip compression for resources. You need to set this on some SAP
	 * versions, which gzip things twice.
	 * 
	 * @param disableGZipCompression
	 * @since 1.3.0
	 */
	void setDisableGZipCompression(final boolean disableGZipCompression);

	/**
	 * Sets the localizer which will be used to find property values.
	 * 
	 * @param localizer
	 * @since 1.3.0
	 */
	void setLocalizer(Localizer localizer);

	/**
	 * Sets the {@link PackageResourceGuard package resource guard}.
	 * 
	 * @param packageResourceGuard
	 *            The package resource guard
	 */
	void setPackageResourceGuard(IPackageResourceGuard packageResourceGuard);

	/**
	 * Set the property factory which will be used to load property files
	 * 
	 * @param factory
	 */
	void setPropertiesFactory(IPropertiesFactory factory);

	/**
	 * Sets the finder to use when searching for resources. By default, the resources are located on
	 * the classpath. If you want to configure other, additional, search paths, you can use this
	 * method.
	 * 
	 * @param resourceFinder
	 *            The resourceFinder to set
	 */
	void setResourceFinder(final IResourceFinder resourceFinder);

	/**
	 * Sets the resource polling frequency. This is the duration of time between checks of resource
	 * modification times. If a resource, such as an HTML file, has changed, it will be reloaded.
	 * Default is for no resource polling to occur.
	 * 
	 * @param resourcePollFrequency
	 *            Frequency at which to poll resources
	 * @see IResourceSettings#setResourceFinder(IResourceFinder)
	 */
	void setResourcePollFrequency(final Duration resourcePollFrequency);

	/**
	 * Sets the resource stream locator for this application
	 * 
	 * @param resourceStreamLocator
	 *            new resource stream locator
	 */
	void setResourceStreamLocator(IResourceStreamLocator resourceStreamLocator);

	/**
	 * Sets the resource watcher
	 * 
	 * @param watcher
	 */
	void setResourceWatcher(IModificationWatcher watcher);

	/**
	 * @see org.apache.wicket.settings.IExceptionSettings#setThrowExceptionOnMissingResource(boolean)
	 * 
	 * @param throwExceptionOnMissingResource
	 */
	void setThrowExceptionOnMissingResource(final boolean throwExceptionOnMissingResource);

	/**
	 * @param useDefaultOnMissingResource
	 *            Whether to use a default value (if available) when a missing resource is requested
	 */
	void setUseDefaultOnMissingResource(final boolean useDefaultOnMissingResource);

	/**
	 * Set the javascript compressor implemententation use e.g. by {@link JavascriptPackageResource}
	 * . A typical implementation will remove comments and whitespace. But a no-op implementation is
	 * available as well.
	 * 
	 * @param compressor
	 *            The implementation to be used
	 * @return The old value
	 */
	IJavascriptCompressor setJavascriptCompressor(IJavascriptCompressor compressor);

	/**
	 * Get the javascript compressor to remove comments and whitespace characters from javascripts
	 * 
	 * @return whether the comments and whitespace characters will be stripped from resources served
	 *         through {@link JavascriptPackageResource}. Null is a valid value.
	 */
	IJavascriptCompressor getJavascriptCompressor();

	/**
	 * Placeholder string for '..' within resource urls (which will be crippled by the browser and
	 * not work anymore). Note that by default the placeholder string is empty '' and thus will not
	 * allow to access parent folders. That is by purpose and for security reasons (see
	 * Wicket-1992). In case you really need it, a good value for placeholder would e.g. be "$up$".
	 * Resources additionally are protected by a
	 * {@link org.apache.wicket.markup.html.IPackageResourceGuard IPackageResourceGuard}
	 * implementation such as {@link org.apache.wicket.resource.resourceGuard.PackageResourceGuard
	 * PackageResourceGuard} which you may use or extend based on your needs.
	 * 
	 * @return placeholder
	 */
	String getParentFolderPlaceholder();

	/**
	 * Placeholder string for '..' within resource urls (which will be crippled by the browser and
	 * not work anymore). Note that by default the placeholder string is empty '' and thus will not
	 * allow to access parent folders. That is by purpose and for security reasons (see
	 * Wicket-1992). In case you really need it, a good value for placeholder would e.g. be "$up$".
	 * Resources additionally are protected by a
	 * {@link org.apache.wicket.markup.html.IPackageResourceGuard IPackageResourceGuard}
	 * implementation such as {@link org.apache.wicket.resource.resourceGuard.PackageResourceGuard
	 * PackageResourceGuard} which you may use or extend based on your needs.
	 * 
	 * @see #getParentFolderPlaceholder()
	 * 
	 * @param sequence
	 *            character sequence which must not be ambiguous within urls
	 */
	void setParentFolderPlaceholder(String sequence);

	/**
	 * Control the usage of timestamps on resources
	 * <p/>
	 * Normally the resource names won't change when the resource ifself changes, for example when you add a new
	 * style to your CSS sheet. This can be very annoying as browsers (and proxies) usally cache resources
	 * in their cache based on the filename and therefore won't update. Unless you change the file name of the
	 * resource, force a reload or clear the browser's cache the page will still render with your old CSS.
	 * <p/>
	 * Depending on HTTP response headers like 'Last-Modified' and 'Cache' automatic cache
	 * invalidation can take very, very long or neven happen at all.
	 * <p/>
	 * Enabling timestamps on resources will inject the last modification time of the resource into
	 * the filename (the name will look something like 'style-ts1282915831000.css' where the large number is
	 * the last modified date in milliseconds and '-ts' is a prefix to avoid conflicts with
	 * filenames that already contain a number before their extension.
	 * *
	 * <p/>
	 * Since browsers and proxies use the filename of the resource as a cache key the changed filename will
	 * not hit the cache and the page gets rendered with the changed file.
	 * <p/>
	 * @return <code>true</code> if timestamps are enabled
	 */
	boolean getUseTimestampOnResources();

	/**
	 * Control the usage of timestamps on resources
	 * <p/>
	 * Normally the resource names won't change when the resource ifself changes, for example when you add a new
	 * style to your CSS sheet. This can be very annoying as browsers (and proxies) usally cache resources
	 * in their cache based on the filename and therefore won't update. Unless you change the file name of the
	 * resource, force a reload or clear the browser's cache the page will still render with your old CSS.
	 * <p/>
	 * Depending on HTTP response headers like 'Last-Modified' and 'Cache' automatic cache
	 * invalidation can take very, very long or neven happen at all.
	 * <p/>
	 * Enabling timestamps on resources will inject the last modification time of the resource into
	 * the filename (the name will look something like 'style-ts1282915831000.css' where the large number is
	 * the last modified date in milliseconds and '-ts' is a prefix to avoid conflicts with
	 * filenames that already contain a number before their extension.
	 * *
	 * <p/>
	 * Since browsers and proxies use the filename of the resource as a cache key the changed filename will
	 * not hit the cache and the page gets rendered with the changed file.
	 * <p/>
	 *
	 * @param enable <code>true</code> for using timestamps on resource names
	 */
	void setUseTimestampOnResources(boolean enable);
}
