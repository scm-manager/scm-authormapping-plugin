/*
 * Copyright (c) 2020 - present Cloudogu GmbH
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, version 3.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see https://www.gnu.org/licenses/.
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
