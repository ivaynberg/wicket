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
package org.apache.wicket.pageStore;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.wicket.Application;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.pageStore.PageWindowManager.PageWindow;
import org.apache.wicket.protocol.http.WebApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A data store implementation which stores the data on disk (in a file system)
 */
public class DiskDataStore implements IDataStore
{
	private static final Logger log = LoggerFactory.getLogger(DiskDataStore.class);

	private static final String INDEX_FILE_NAME = "DiskDataStoreIndex";

	private final String applicationName;

	private final int maxSizePerPageSession;

	private final FileChannelPool fileChannelPool;

	private final File fileStoreFolder;

	private final ConcurrentMap<String, SessionEntry> sessionEntryMap = new ConcurrentHashMap<String, SessionEntry>();

	/**
	 * Construct.
	 * 
	 * @param applicationName
	 * @param fileStoreFolder
	 * @param maxSizePerSession
	 * @param fileChannelPoolCapacity
	 */
	public DiskDataStore(final String applicationName, final File fileStoreFolder,
		final int maxSizePerSession, final int fileChannelPoolCapacity)
	{
		this.applicationName = applicationName;
		this.fileStoreFolder = fileStoreFolder;
		maxSizePerPageSession = maxSizePerSession;


		try
		{
			fileChannelPool = new FileChannelPool(fileChannelPoolCapacity);

			this.fileStoreFolder.mkdirs();
			loadIndex();
		}
		catch (SecurityException e)
		{
			throw new WicketRuntimeException(
				// TODO improve the message by explaining where in the API the disk store can be
				// changed
				"SecurityException occurred while creating DiskDataStore. Consider using a non-disk based IDataStore implementation.",
				e);
		}
	}

	/**
	 * Construct.
	 * 
	 * @param applicationName
	 * @param maxSizePerSession
	 * @param fileChannelPoolCapacity
	 */
	public DiskDataStore(final String applicationName, final int maxSizePerSession,
		final int fileChannelPoolCapacity)
	{
		this(applicationName, getDefaultFileStoreFolder(), maxSizePerSession,
			fileChannelPoolCapacity);
	}

	/**
	 * @see org.apache.wicket.pageStore.IDataStore#destroy()
	 */
	public void destroy()
	{
		saveIndex();
		fileChannelPool.destroy();
	}

	/**
	 * @see org.apache.wicket.pageStore.IDataStore#getData(java.lang.String, int)
	 */
	public byte[] getData(final String sessionId, final int id)
	{
		SessionEntry sessionEntry = getSessionEntry(sessionId, false);
		if (sessionEntry == null)
		{
			return null;
		}

		return sessionEntry.loadPage(id);
	}

	/**
	 * @see org.apache.wicket.pageStore.IDataStore#isReplicated()
	 */
	public boolean isReplicated()
	{
		return false;
	}

	/**
	 * @see org.apache.wicket.pageStore.IDataStore#removeData(java.lang.String, int)
	 */
	public void removeData(final String sessionId, final int id)
	{
		SessionEntry sessionEntry = getSessionEntry(sessionId, false);
		if (sessionEntry != null)
		{
			sessionEntry.removePage(id);
		}
	}

	/**
	 * @see org.apache.wicket.pageStore.IDataStore#removeData(java.lang.String)
	 */
	public void removeData(final String sessionId)
	{
		SessionEntry sessionEntry = getSessionEntry(sessionId, false);
		if (sessionEntry != null)
		{
			synchronized (sessionEntry)
			{
				sessionEntryMap.remove(sessionEntry.sessionId);
				sessionEntry.unbind();
			}
		}
	}

	/**
	 * @see org.apache.wicket.pageStore.IDataStore#storeData(java.lang.String, int, byte[])
	 */
	public void storeData(final String sessionId, final int id, final byte[] data)
	{
		SessionEntry sessionEntry = getSessionEntry(sessionId, true);
		if (sessionEntry != null)
		{
			sessionEntry.savePage(id, data);
		}
	}

	/**
	 * 
	 * @param sessionId
	 * @param create
	 * @return the session entry
	 */
	private SessionEntry getSessionEntry(final String sessionId, final boolean create)
	{
		if (!create)
		{
			return sessionEntryMap.get(sessionId);
		}

		SessionEntry entry = new SessionEntry(this, sessionId);
		SessionEntry existing = sessionEntryMap.putIfAbsent(sessionId, entry);
		return existing != null ? existing : entry;
	}

	/**
	 * Load the index
	 */
	@SuppressWarnings("unchecked")
	private void loadIndex()
	{
		File storeFolder = getStoreFolder();
		File index = new File(storeFolder, INDEX_FILE_NAME);
		if (index.exists() && index.length() > 0)
		{
			try
			{
				InputStream stream = new FileInputStream(index);
				ObjectInputStream ois = new ObjectInputStream(stream);
				Map<String, SessionEntry> map = (Map<String, SessionEntry>)ois.readObject();
				sessionEntryMap.clear();
				sessionEntryMap.putAll(map);

				for (Entry<String, SessionEntry> entry : sessionEntryMap.entrySet())
				{
					// initialize the diskPageStore reference
					SessionEntry sessionEntry = entry.getValue();
					sessionEntry.diskDataStore = this;
				}
				stream.close();
			}
			catch (Exception e)
			{
				log.error("Couldn't load DiskDataStore index from file " + index + ".", e);
			}
		}
		index.delete();
	}

	/**
	 * 
	 */
	private void saveIndex()
	{
		File storeFolder = getStoreFolder();
		if (storeFolder.exists())
		{
			File index = new File(storeFolder, INDEX_FILE_NAME);
			index.delete();
			try
			{
				OutputStream stream = new FileOutputStream(index);
				ObjectOutputStream oos = new ObjectOutputStream(stream);
				Map<String, SessionEntry> map = new HashMap<String, SessionEntry>(
					sessionEntryMap.size());
				for (Entry<String, SessionEntry> e : sessionEntryMap.entrySet())
				{
					if (e.getValue().unbound == false)
					{
						map.put(e.getKey(), e.getValue());
					}
				}
				oos.writeObject(map);
				stream.close();
			}
			catch (Exception e)
			{
				log.error("Couldn't write DiskDataStore index to file " + index + ".", e);
			}
		}
	}

	/**
	 * 
	 */
	protected static class SessionEntry implements Serializable
	{
		private static final long serialVersionUID = 1L;

		private final String sessionId;
		private transient DiskDataStore diskDataStore;
		private String fileName;
		private PageWindowManager manager;
		private boolean unbound = false;

		protected SessionEntry(DiskDataStore diskDataStore, String sessionId)
		{
			this.diskDataStore = diskDataStore;
			this.sessionId = sessionId;
		}

		private PageWindowManager getManager()
		{
			if (manager == null)
			{
				manager = new PageWindowManager(diskDataStore.maxSizePerPageSession);
			}
			return manager;
		}

		private String getFileName()
		{
			if (fileName == null)
			{
				fileName = diskDataStore.getSessionFileName(sessionId, true);
			}
			return fileName;
		}

		/**
		 * @return session id
		 */
		public String getSessionId()
		{
			return sessionId;
		}

		/**
		 * Saves the serialized page to appropriate file.
		 * 
		 * @param pageId
		 * @param data
		 */
		public synchronized void savePage(int pageId, byte data[])
		{
			if (unbound)
			{
				return;
			}
			// only save page that has some data
			if (data != null)
			{
				// allocate window for page
				PageWindow window = getManager().createPageWindow(pageId, data.length);

				// take the filechannel from the pool
				FileChannel channel = diskDataStore.fileChannelPool.getFileChannel(getFileName(),
					true);
				try
				{
					// write the content
					channel.write(ByteBuffer.wrap(data), window.getFilePartOffset());
				}
				catch (IOException e)
				{
					log.error("Error writing to a channel " + channel, e);
				}
				finally
				{
					// return the "borrowed" file channel
					diskDataStore.fileChannelPool.returnFileChannel(channel);
				}
			}
		}

		/**
		 * Removes the page from pagemap file.
		 * 
		 * @param pageMapName
		 * @param pageId
		 */
		public synchronized void removePage(int pageId)
		{
			if (unbound)
			{
				return;
			}
			getManager().removePage(pageId);
		}

		/**
		 * Loads the part of pagemap file specified by the given PageWindow.
		 * 
		 * @param window
		 * @param pageMapFileName
		 * @return serialized page data
		 */
		public byte[] loadPage(PageWindow window)
		{
			byte[] result = null;
			FileChannel channel = diskDataStore.fileChannelPool.getFileChannel(getFileName(), false);
			if (channel != null)
			{
				ByteBuffer buffer = ByteBuffer.allocate(window.getFilePartSize());
				try
				{
					channel.read(buffer, window.getFilePartOffset());
					if (buffer.hasArray())
					{
						result = buffer.array();
					}
				}
				catch (IOException e)
				{
					log.error("Error reading from file channel " + channel, e);
				}
				finally
				{
					diskDataStore.fileChannelPool.returnFileChannel(channel);
				}
			}
			return result;
		}

		/**
		 * Loads the specified page data.
		 * 
		 * @param pageMapName
		 * @param id
		 * @return page data or null if the page is no longer in pagemap file
		 */
		public synchronized byte[] loadPage(int id)
		{
			if (unbound)
			{
				return null;
			}
			byte[] result = null;
			PageWindow window = getManager().getPageWindow(id);
			if (window != null)
			{
				result = loadPage(window);
			}
			return result;
		}

		/**
		 * Deletes all files for this session.
		 */
		public synchronized void unbind()
		{
			diskDataStore.fileChannelPool.closeAndDeleteFileChannel(getFileName());
			File sessionFolder = diskDataStore.getSessionFolder(sessionId, false);
			if (sessionFolder.exists())
			{
				sessionFolder.delete();
			}
			unbound = true;
		}
	}

	/**
	 * Returns the file name for specified session. If the session folder (folder that contains the
	 * file) does not exist and createSessionFolder is true, the folder will be created.
	 * 
	 * @param sessionId
	 * @param pageMapName
	 * @param createSessionFolder
	 * @return file name for pagemap
	 */
	private String getSessionFileName(String sessionId, boolean createSessionFolder)
	{
		File sessionFolder = getSessionFolder(sessionId, createSessionFolder);
		return new File(sessionFolder, "data").getAbsolutePath();
	}

	/**
	 * 
	 * @return folder
	 */
	private static File getDefaultFileStoreFolder()
	{
		File dir = null;

		if (Application.exists())
		{
			dir = (File)((WebApplication)Application.get()).getServletContext().getAttribute(
				"javax.servlet.context.tempdir");
		}

		if (dir != null)
		{
			return dir;
		}

		try
		{
			return File.createTempFile("file-prefix", null).getParentFile();
		}
		catch (IOException e)
		{
			throw new WicketRuntimeException(e);
		}
	}

	/**
	 * 
	 * @return folder
	 */
	private File getStoreFolder()
	{
		return new File(fileStoreFolder, applicationName + "-filestore");
	}

	/**
	 * Returns the folder for the specified sessions. If the folder doesn't exist and the create
	 * flag is set, the folder will be created.
	 * 
	 * @param sessionId
	 * @param create
	 * @return folder used to store session data
	 */
	protected File getSessionFolder(String sessionId, final boolean create)
	{
		File storeFolder = getStoreFolder();

		sessionId = sessionId.replace('*', '_');
		sessionId = sessionId.replace('/', '_');
		sessionId = sessionId.replace(':', '_');

		File sessionFolder = new File(storeFolder, sessionId);
		if (create && sessionFolder.exists() == false)
		{
			mkdirs(sessionFolder);
		}
		return sessionFolder;
	}

	/**
	 * Utility method for creating a directory
	 * 
	 * @param file
	 */
	private void mkdirs(File file)
	{
		// for some reason, simple file.mkdirs sometimes fails under heavy load
		for (int j = 0; j < 5; ++j)
		{
			for (int i = 0; i < 10; ++i)
			{
				if (file.mkdirs())
				{
					return;
				}
			}
			try
			{
				Thread.sleep(100);
			}
			catch (InterruptedException ignore)
			{
			}
		}
		log.error("Failed to make directory " + file);
	}
}
