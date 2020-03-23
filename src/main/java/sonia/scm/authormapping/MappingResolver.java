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
