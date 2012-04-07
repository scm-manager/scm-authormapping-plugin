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
import sonia.scm.repository.Person;
import sonia.scm.user.UserManager;
import sonia.scm.web.security.AdministrationContext;

/**
 *
 * @author Sebastian Sdorra
 */
public class MappingResolver
{

  /**
   * the logger for MappingResolver
   */
  private static final Logger logger =
    LoggerFactory.getLogger(MappingResolver.class);

  //~--- constructors ---------------------------------------------------------

  /**
   * Constructs ...
   *
   *
   * @param adminContext
   * @param userManager
   * @param cache
   * @param configuration
   */
  public MappingResolver(AdministrationContext adminContext,
                         UserManager userManager, Cache<String, Person> cache,
                         MappingConfiguration configuration)
  {
    this.adminContext = adminContext;
    this.userManager = userManager;
    this.cache = cache;
    this.configuration = configuration;
  }

  //~--- methods --------------------------------------------------------------

  /**
   * Resolve the author name of the user in the following order:
   * - mapping configuration
   * - cache (sonia.cache.authormapping)
   * - scm-manager user database
   *
   * @param name
   * @param mail
   *
   * @return
   */
  public Person resolve(String name, String mail)
  {
    Person person = configuration.getMapping(name);

    if (person == null)
    {
      if (configuration.isEnableAutoMapping())
      {
        person = getPersonFromDatabase(name, mail);
      }

      if (person == null)
      {
        person = new Person(name, mail);
      }
    }

    return person;
  }

  //~--- get methods ----------------------------------------------------------

  /**
   * Method description
   *
   *
   * @param name
   * @param mail
   *
   * @return
   */
  private Person getPersonFromDatabase(String name, String mail)
  {
    Person person = null;
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
      person = new Person(name, mail);

      if (logger.isTraceEnabled())
      {
        logger.trace("fetch person {} from scm database", name);
      }

      adminContext.runAsAdmin(new MappingPrivilegedAction(userManager, person));
      cache.put(name, person);
    }

    return person;
  }

  //~--- fields ---------------------------------------------------------------

  /** Field description */
  private AdministrationContext adminContext;

  /** Field description */
  private Cache<String, Person> cache;

  /** Field description */
  private MappingConfiguration configuration;

  /** Field description */
  private UserManager userManager;
}
