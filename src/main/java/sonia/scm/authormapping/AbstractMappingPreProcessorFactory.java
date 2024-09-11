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

import sonia.scm.cache.Cache;
import sonia.scm.cache.CacheManager;
import sonia.scm.repository.Person;
import sonia.scm.repository.Repository;
import sonia.scm.user.UserManager;
import sonia.scm.util.AssertUtil;
import sonia.scm.web.security.AdministrationContext;

/**
 * @author Sebastian Sdorra
 */
public class AbstractMappingPreProcessorFactory {

    public static final String CACHE_NAME = "sonia.cache.authormapping";

    private AdministrationContext adminContext;
    private Cache<String, Person> cache;
    private UserManager userManager;
    private AuthorMappingManager mappingManager;


    protected AbstractMappingPreProcessorFactory(
            AdministrationContext adminContext, UserManager userManager,
            CacheManager cacheManager, AuthorMappingManager mappingManager) {
        this.adminContext = adminContext;
        this.userManager = userManager;
        this.cache = cacheManager.getCache(CACHE_NAME);
        this.mappingManager = mappingManager;
    }

    public MappingResolver createMappingResolver(Repository repository) {
        AssertUtil.assertIsNotNull(repository);
        MappingConfiguration configuration = mappingManager.getConfiguration(repository);
        return new MappingResolver(adminContext, userManager,
                cache, configuration);
    }

}
