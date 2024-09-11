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

import com.google.inject.Inject;
import com.google.inject.Singleton;

import sonia.scm.cache.CacheManager;
import sonia.scm.plugin.Extension;
import sonia.scm.repository.BlameLinePreProcessor;
import sonia.scm.repository.BlameLinePreProcessorFactory;
import sonia.scm.repository.Repository;
import sonia.scm.user.UserManager;
import sonia.scm.web.security.AdministrationContext;

/**
 * @author Sebastian Sdorra
 */
@Singleton
@Extension
public class MappingBlameLinePreProcessorFactory
        extends AbstractMappingPreProcessorFactory
        implements BlameLinePreProcessorFactory {


    @Inject
    public MappingBlameLinePreProcessorFactory(
            AdministrationContext adminContext, UserManager userManager,
            CacheManager cacheManager, AuthorMappingManager mappingManager) {
        super(adminContext, userManager, cacheManager, mappingManager);
    }


    @Override
    public BlameLinePreProcessor createPreProcessor(Repository repository) {
        return new AuthorMappingBlameLinePreProcessor(createMappingResolver(repository));
    }
}
