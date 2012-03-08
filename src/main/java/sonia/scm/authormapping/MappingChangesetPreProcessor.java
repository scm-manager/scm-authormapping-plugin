/**
 * Copyright (c) 2010, Sebastian Sdorra
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 3. Neither the name of SCM-Manager; nor the names of its
 *    contributors may be used to endorse or promote products derived from this
 *    software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * http://bitbucket.org/sdorra/scm-manager
 *
 */



package sonia.scm.authormapping;

//~--- non-JDK imports --------------------------------------------------------

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sonia.scm.cache.Cache;
import sonia.scm.repository.Changeset;
import sonia.scm.repository.ChangesetPreProcessor;
import sonia.scm.repository.Person;
import sonia.scm.user.UserManager;
import sonia.scm.util.AssertUtil;
import sonia.scm.util.Util;
import sonia.scm.web.security.AdministrationContext;

//~--- JDK imports ------------------------------------------------------------

import java.io.UnsupportedEncodingException;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 *
 * @author Sebastian Sdorra
 */
public class MappingChangesetPreProcessor implements ChangesetPreProcessor
{

  /** Field description */
  public static final String DIGEST_ENCODING = "CP1252";

  /** Field description */
  public static final String DIGEST_TYPE = "MD5";

  /** Field description */
  public static final String PROPERTY_GRAVATAR = "gravatar-hash";

  /** the logger for MappingChangesetPreProcessor */
  private static final Logger logger =
    LoggerFactory.getLogger(MappingChangesetPreProcessor.class);

  //~--- constructors ---------------------------------------------------------

  /**
   * Constructs ...
   *
   *
   * @param adminContext
   * @param userManager
   * @param cache
   */
  public MappingChangesetPreProcessor(AdministrationContext adminContext,
          UserManager userManager, Cache<String, Person> cache)
  {
    this.adminContext = adminContext;
    this.userManager = userManager;
    this.cache = cache;
  }

  //~--- methods --------------------------------------------------------------

  /**
   * Method description
   *
   *
   * @param changeset
   */
  @Override
  public void process(Changeset changeset)
  {
    AssertUtil.assertIsNotNull(changeset);

    Person person = changeset.getAuthor();

    if (person == null)
    {
      person = new Person();
    }

    String name = person.getName();

    if (Util.isNotEmpty(name))
    {
      Person cachedPerson = cache.get(name);

      if (cachedPerson != null)
      {
        if (logger.isTraceEnabled())
        {
          logger.trace("fetch person {} from cache", name);
        }

        person = cachedPerson;
      }
      else
      {
        if (logger.isTraceEnabled())
        {
          logger.trace("fetch person {} from scm database", name);
        }

        adminContext.runAsAdmin(new MappingPrivilegedAction(userManager,
                person));
        cache.put(name, person);
      }

      changeset.setAuthor(person);

      String gravatarHash = changeset.getProperty(PROPERTY_GRAVATAR);

      if (Util.isNotEmpty(gravatarHash))
      {
        setGravatarHash(changeset, person);
      }
    }
    else if (logger.isWarnEnabled())
    {
      logger.warn("person object of changeset {} has no name",
                  changeset.getId());
    }
  }

  /**
   * Method description
   *
   *
   * @param array
   *
   * @return
   */
  private String hex(byte[] array)
  {
    StringBuilder sb = new StringBuilder();

    for (int i = 0; i < array.length; ++i)
    {
      sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1, 3));
    }

    return sb.toString();
  }

  /**
   * Method description
   *
   *
   * @param message
   *
   * @return
   */
  private String md5Hex(String message)
  {
    String out = null;

    if (Util.isNotEmpty(message))
    {
      message = message.toLowerCase().trim();

      try
      {
        MessageDigest md = MessageDigest.getInstance(DIGEST_TYPE);

        out = hex(md.digest(message.getBytes(DIGEST_ENCODING)));
      }
      catch (NoSuchAlgorithmException ex)
      {
        logger.error(ex.getMessage(), ex);
      }
      catch (UnsupportedEncodingException ex)
      {
        logger.error(ex.getMessage(), ex);
      }
    }

    return out;
  }

  //~--- set methods ----------------------------------------------------------

  /**
   * Method description
   *
   *
   * @param changeset
   * @param person
   */
  private void setGravatarHash(Changeset changeset, Person person)
  {
    String value = person.getMail();

    if (Util.isEmpty(value))
    {
      value = person.getName();
    }

    if (Util.isNotEmpty(value))
    {
      value = md5Hex(value);
      changeset.setProperty(PROPERTY_GRAVATAR, value);
    }
  }

  //~--- fields ---------------------------------------------------------------

  /** Field description */
  private AdministrationContext adminContext;

  /** Field description */
  private Cache<String, Person> cache;

  /** Field description */
  private UserManager userManager;
}
