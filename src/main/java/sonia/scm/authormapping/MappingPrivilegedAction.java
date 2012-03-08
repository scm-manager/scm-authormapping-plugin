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

import sonia.scm.repository.Person;
import sonia.scm.search.SearchRequest;
import sonia.scm.user.User;
import sonia.scm.user.UserManager;
import sonia.scm.web.security.PrivilegedAction;

//~--- JDK imports ------------------------------------------------------------

import java.util.Collection;

/**
 *
 * @author Sebastian Sdorra
 */
public class MappingPrivilegedAction implements PrivilegedAction
{

  /**
   * the logger for MappingPrivilegedAction
   */
  private static final Logger logger =
    LoggerFactory.getLogger(MappingPrivilegedAction.class);

  //~--- constructors ---------------------------------------------------------

  /**
   * Constructs ...
   *
   *
   * @param userManager
   * @param person
   */
  public MappingPrivilegedAction(UserManager userManager, Person person)
  {
    this.userManager = userManager;
    this.person = person;
  }

  //~--- methods --------------------------------------------------------------

  /**
   * Method description
   *
   */
  @Override
  public void run()
  {
    String name = person.getName();

    if (logger.isTraceEnabled())
    {
      logger.trace("search scm user with name {}", name);
    }

    User user = findUser(person);

    if (user != null)
    {
      person.setName(user.getDisplayName());
      person.setMail(user.getMail());

      if (logger.isDebugEnabled())
      {
        logger.debug("found person '{}' for username {}", person, name);
      }
    }
    else if (logger.isTraceEnabled())
    {
      logger.trace("could not find user with username {}", name);
    }
  }

  /**
   * Method description
   *
   *
   * @param person
   *
   * @return
   */
  private User findUser(Person person)
  {
    User user = null;
    Collection<User> users =
      userManager.search(new SearchRequest(person.getName(), true));

    if (users != null)
    {
      for (User u : users)
      {
        if (person.getName().equalsIgnoreCase(u.getName())
            || person.getName().equalsIgnoreCase(u.getDisplayName()))
        {
          user = u;

          break;
        }
      }
    }

    return user;
  }

  //~--- fields ---------------------------------------------------------------

  /** Field description */
  private Person person;

  /** Field description */
  private UserManager userManager;
}
