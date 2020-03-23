/*
 * MIT License
 *
 * Copyright (c) 2020-present Cloudogu GmbH and Contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
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

    if (logger.isDebugEnabled())
    {
      logger.debug("search scm user with name {}", name);
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
