/**
 * Copyright (c) 2010, Sebastian Sdorra All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer. 2. Redistributions in
 * binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other
 * materials provided with the distribution. 3. Neither the name of SCM-Manager;
 * nor the names of its contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * http://bitbucket.org/sdorra/scm-manager
 *
 */



package sonia.scm.authormapping;

//~--- non-JDK imports --------------------------------------------------------

import sonia.scm.PropertiesAware;
import sonia.scm.repository.Person;
import sonia.scm.util.Util;

//~--- JDK imports ------------------------------------------------------------

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

/**
 *
 * @author Sebastian Sdorra
 */
public class MappingConfiguration
{

  /** Field description */
  public static final String PROPERTY_AUTO_MAPPING =
    "sonia.authorname.auto-mapping";

  /** Field description */
  public static final String PROPERTY_MAUNAL_MAPPING =
    "sonia.authorname.manual-mapping";

  //~--- constructors ---------------------------------------------------------

  /**
   * Constructs ...
   *
   *
   * @param properties
   */
  public MappingConfiguration(PropertiesAware properties)
  {
    this.enableAutoMapping = getBooleanProperty(properties,
            PROPERTY_AUTO_MAPPING, true);
    createManualMappingMap(properties);
  }

  /**
   * Constructs ...
   *
   *
   * @param enableAutoMapping
   * @param manualMapping
   */
  public MappingConfiguration(boolean enableAutoMapping,
                              Map<String, Person> manualMapping)
  {
    this.enableAutoMapping = enableAutoMapping;
    this.manualMapping = manualMapping;
  }

  //~--- get methods ----------------------------------------------------------

  /**
   * Method description
   *
   *
   * @param name
   *
   * @return
   */
  public Person getMapping(String name)
  {
    return manualMapping.get(name);
  }

  /**
   * Method description
   *
   *
   * @return
   */
  public boolean isEnableAutoMapping()
  {
    return enableAutoMapping;
  }

  //~--- methods --------------------------------------------------------------

  /**
   * Method description
   *
   *
   * @param entry
   */
  private void appendManualMappingEntry(String entry)
  {
    StringTokenizer tokenizer = new StringTokenizer(entry, ",", false);

    if (tokenizer.hasMoreTokens())
    {
      String key = tokenizer.nextToken();
      String name = null;
      String mail = null;

      if (tokenizer.hasMoreTokens())
      {
        name = tokenizer.nextToken();

        if (tokenizer.hasMoreTokens())
        {
          mail = tokenizer.nextToken();
        }
      }

      if ((key != null) && (name != null))
      {
        Person person = new Person(name);

        if (mail != null)
        {
          person.setMail(mail);
        }

        manualMapping.put(key, person);
      }
    }
  }

  /**
   * Method description
   *
   *
   * @param properties
   */
  private void createManualMappingMap(PropertiesAware properties)
  {
    String value = properties.getProperty(PROPERTY_MAUNAL_MAPPING);

    if (Util.isNotEmpty(value))
    {
      StringTokenizer tokenizer = new StringTokenizer(value, ";", false);

      while (tokenizer.hasMoreTokens())
      {
        String entry = tokenizer.nextToken();

        appendManualMappingEntry(entry);
      }
    }
  }

  //~--- get methods ----------------------------------------------------------

  /**
   * Method description
   *
   *
   * @param properties
   * @param name
   * @param defaultValue
   *
   * @return
   */
  private boolean getBooleanProperty(PropertiesAware properties, String name,
                                     boolean defaultValue)
  {
    boolean result = defaultValue;
    String value = properties.getProperty(name);

    if (Util.isNotEmpty(value))
    {
      result = Boolean.parseBoolean(value);
    }

    return result;
  }

  //~--- fields ---------------------------------------------------------------

  /** Field description */
  private boolean enableAutoMapping = true;

  /** Field description */
  private Map<String, Person> manualMapping = new HashMap<String, Person>();
}
