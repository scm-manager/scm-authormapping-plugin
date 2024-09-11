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

import sonia.scm.cache.Cache;
import sonia.scm.repository.Person;
import sonia.scm.user.UserManager;
import sonia.scm.web.security.AdministrationContext;

/**
 *
 * @author Sebastian Sdorra
 */
public class MappingResolver {

    private static final Logger logger =
            LoggerFactory.getLogger(MappingResolver.class);


    private AdministrationContext adminContext;
    private Cache<String, Person> cache;
    private MappingConfiguration configuration;
    private UserManager userManager;

    public MappingResolver(AdministrationContext adminContext,
                           UserManager userManager, Cache<String, Person> cache,
                           MappingConfiguration configuration) {
        this.adminContext = adminContext;
        this.userManager = userManager;
        this.cache = cache;
        this.configuration = configuration;
    }

    /**
     * Resolve the author name of the user in the following order:
     * - Mapping configuration
     * - cache (sonia.cache.authormapping)
     * - scm-manager user database
     */
    public Person resolve(String name, String mail) {
        Person person = configuration.getMapping(name);

        if (person == null) {
            if (configuration.isEnableAutoMapping()) {
                person = getPersonFromDatabase(name, mail);
            }

            if (person == null) {
                person = new Person(name, mail);
            }
        }

        return person;
    }

    private Person getPersonFromDatabase(String name, String mail) {
        Person person;
        Person cachedPerson = cache.get(name);

        if (cachedPerson != null) {
            if (logger.isTraceEnabled()) {
                logger.trace("fetch person {} from cache", name);
            }

            person = cachedPerson;
        } else {
            person = new Person(name, mail);

            if (logger.isTraceEnabled()) {
                logger.trace("fetch person {} from scm database", name);
            }

            adminContext.runAsAdmin(new MappingPrivilegedAction(userManager, person));
            cache.put(name, person);
        }

        return person;
    }

}
