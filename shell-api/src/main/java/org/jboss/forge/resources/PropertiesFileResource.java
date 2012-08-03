package org.jboss.forge.resources;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import javax.inject.Inject;

import org.jboss.forge.project.services.ResourceFactory;
import org.jboss.forge.shell.util.Streams;

/**
 * A property based resource
 *
 * @author george
 *
 */
@ResourceHandles("*.properties")
public class PropertiesFileResource extends FileResource<PropertiesFileResource>
{

   @Inject
   public PropertiesFileResource(final ResourceFactory factory)
   {
      super(factory, null);
      setFlag(ResourceFlag.Leaf);
   }

   public PropertiesFileResource(final ResourceFactory factory, final File file)
   {
      super(factory, file);
      setFlag(ResourceFlag.Leaf);
   }

   @Override
   public Resource<File> createFrom(File file)
   {
      return new PropertiesFileResource(getResourceFactory(), file);
   }

   @Override
   protected List<Resource<?>> doListResources()
   {
      Properties p = getFileContentAsProperties();

      List<Resource<?>> entryResources = new ArrayList<Resource<?>>(p.size());
      for (Entry<Object, Object> entry : p.entrySet())
      {
         EntryResource<Object, Object> entryResource = new EntryResource<Object, Object>(this, entry.getKey(),
                  entry.getValue());
         entryResources.add(entryResource);
      }
      return entryResources;
   }

   /**
    * Adds a property to this resource
    *
    * @param key
    * @param value
    * @return old value or null if none
    */
   public String putProperty(String key, String value)
   {
      Properties props = getFileContentAsProperties();
      String oldValue = (String) props.setProperty(key, value);
      storeProperties(props);
      return oldValue;
   }

   /**
    * Adds all the properties and updates the file
    *
    * @param newProperties
    */
   public void putAllProperties(Map<String, String> newProperties)
   {
      Properties props = getFileContentAsProperties();
      props.putAll(newProperties);
      storeProperties(props);
   }

   /**
    * Replaces all the occurrences
    *
    * @param newProperties
    */
   public void replaceProperties(Map<String, String> newProperties)
   {
      Properties props = new SortedProperties();
      props.putAll(newProperties);
      storeProperties(props);
   }

   /**
    * Removes an entry on this file based on the properties file
    *
    * @param key
    * @return
    */
   public String removeProperty(String key)
   {
      Properties props = getFileContentAsProperties();
      String oldValue = (String) props.remove(key);
      storeProperties(props);
      return oldValue;
   }

   /**
    * Returns the value related to the key in this properties file
    *
    * @param key
    * @return
    */
   public String getProperty(String key)
   {
      Properties props = getFileContentAsProperties();
      return props.getProperty(key);
   }

   /**
    * Returns the keys in this properties file
    *
    * @return
    */
   public Set<String> getKeys()
   {

      return getFileContentAsProperties().stringPropertyNames();
   }

   /**
    * This method should group and order the entries before persisting
    *
    * @param props
    */
   private void storeProperties(Properties props)
   {
      FileOutputStream fos = null;
      try
      {
         fos = new FileOutputStream(getUnderlyingResourceObject());
         props.store(fos, "Generated by Forge i18n Plugin");
      }
      catch (IOException io)
      {
         throw new RuntimeException("Error while storing file", io);
      }
      finally
      {
         Streams.closeQuietly(fos);
      }

   }

   /**
    * Reads the content of the file as a {@link Properties} object
    *
    * @return
    */
   private Properties getFileContentAsProperties()
   {
      Properties p = new SortedProperties();
      InputStream is = null;
      try
      {
         is = getResourceInputStream();
         p.load(is);
      }
      catch (IOException io)
      {
         throw new RuntimeException("Error while loading properties");
      }
      finally
      {
         Streams.closeQuietly(is);
      }
      return p;
   }

   @Override
   public String toString()
   {
      return file.getName();
   }

   /**
    * Returns the keys of this {@link Properties} object in order
    *
    * @author george
    *
    */
   private class SortedProperties extends Properties
   {
      private static final long serialVersionUID = 1L;

      @SuppressWarnings({ "rawtypes", "unchecked" })
      @Override
      public synchronized Enumeration<Object> keys()
      {
         Enumeration<?> keysEnum = super.keys();
         List<String> keyList = new ArrayList<String>();
         while (keysEnum.hasMoreElements())
         {
            keyList.add((String) keysEnum.nextElement());
         }
         Collections.sort(keyList);
         Enumeration e = Collections.enumeration(keyList);
         return e;
      }
   }
}
