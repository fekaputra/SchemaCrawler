package sf.util;


import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Configuration properties.
 * 
 * @author Sualeh Fatehi
 */
public class Config
  extends HashMap<String, String>
{

  private static final long serialVersionUID = 8720699738076915453L;

  private static final Logger LOGGER = Logger.getLogger(Config.class.getName());

  /**
   * Loads the SchemaCrawler configuration, and override.
   * 
   * @param configStream
   *        Configuration stream.
   * @return Configuration properties.
   */
  public static Config load(final InputStream configStream)
  {
    Properties configProperties = new Properties();
    if (configStream != null)
    {
      configProperties = loadProperties(configProperties, configStream);
    }
    return new Config(configProperties);
  }

  /**
   * Loads the SchemaCrawler configuration, and override.
   * 
   * @param configFilenames
   *        Configuration file name.
   * @return Configuration properties.
   */
  public static Config load(final String... configFilenames)
  {
    Properties configProperties = new Properties();
    if (configFilenames != null)
    {
      for (final String configFilename: configFilenames)
      {
        if (!Utilities.isBlank(configFilename))
        {
          configProperties = loadProperties(configProperties,
                                            new File(configFilename));
        }
      }
    }
    return new Config(configProperties);
  }

  /**
   * Loads a properties file.
   * 
   * @param properties
   *        Properties object.
   * @param propertiesFileName
   *        Properties file name.
   * @return Properties
   */
  private static Properties loadProperties(final Properties properties,
                                           final File propertiesFile)
  {
    try
    {
      if (propertiesFile.exists())
      {
        final InputStream propertiesStream = new BufferedInputStream(new FileInputStream(propertiesFile));
        loadProperties(properties, propertiesStream);
      }
      else
      {
        LOGGER.log(Level.CONFIG, "Cannot load properties file "
                                 + propertiesFile);
      }
    }
    catch (final FileNotFoundException e)
    {
      LOGGER.log(Level.WARNING,
                 "Cannot find properties file " + propertiesFile,
                 e);
    }
    return properties;
  }

  /**
   * Loads a properties file.
   * 
   * @param properties
   *        Properties object.
   * @param propertiesFileName
   *        Properties file name.
   * @return Properties
   */
  private static Properties loadProperties(final Properties properties,
                                           final InputStream propertiesStream)
  {
    try
    {
      properties.load(propertiesStream);
      propertiesStream.close();
    }
    catch (final IOException e)
    {
      LOGGER.log(Level.WARNING, "Error loading properties", e);
    }
    finally
    {
      try
      {
        if (propertiesStream != null)
        {
          propertiesStream.close();
        }
      }
      catch (final IOException e)
      {
        LOGGER.log(Level.WARNING, "Error closing stream", e);
      }
    }
    return properties;
  }

  /**
   * Creates an empty config.
   */
  public Config()
  {
    super();
  }

  /**
   * Clones a config.
   * 
   * @param config
   *        Config to clone
   */
  public Config(final Map<String, String> config)
  {
    super(config);
  }

  /**
   * Copies properties into a map.
   * 
   * @param properties
   *        Properties to copy
   */
  public Config(final Properties properties)
  {
    super(Utilities.propertiesMap(properties));
  }

  /**
   * Gets the value of a property as a boolean.
   * 
   * @param propertyName
   *        Property name
   * @return Boolean value
   */
  public boolean getBooleanValue(final String propertyName)
  {
    return Boolean.parseBoolean(getStringValue(propertyName, "false"));
  }

  /**
   * Gets the value of a property as an integer.
   * 
   * @param propertyName
   *        Property name
   * @return Integer value
   */
  public int getIntegerValue(final String propertyName)
  {
    return Integer.parseInt(getStringValue(propertyName, null));
  }

  /**
   * Gets the value of a property as a string.
   * 
   * @param propertyName
   *        Property name
   * @param defaultValue
   *        Default value
   * @return String value
   */
  public String getStringValue(final String propertyName,
                               final String defaultValue)
  {
    String value = get(propertyName);
    if (value == null)
    {
      value = defaultValue;
    }
    return value;
  }

  /**
   * Gets a sub-group of properties - those that start with a given
   * prefix. The prefix is removed in the result.
   * 
   * @param prefix
   *        Prefix to group by.
   * @return Partitioned properties.
   */
  public Config partition(final String prefix)
  {
    if (prefix == null || prefix.length() == 0)
    {
      return this;
    }

    final String dottedPrefix = prefix + ".";
    final Config partition = new Config();

    for (final Map.Entry<String, String> entry: entrySet())
    {
      final String key = entry.getKey();
      if (key.startsWith(dottedPrefix))
      {
        final String unprefixed = key.substring(dottedPrefix.length());
        partition.put(unprefixed, entry.getValue());
      }
    }

    return partition;
  }

  /**
   * Returns the configuration into properties.
   * 
   * @return Properties
   */
  public Properties toProperties()
  {
    final Properties properties = new Properties();
    for (final Entry<String, String> entry: entrySet())
    {
      properties.setProperty(entry.getKey(), entry.getValue());
    }
    return properties;
  }

}
