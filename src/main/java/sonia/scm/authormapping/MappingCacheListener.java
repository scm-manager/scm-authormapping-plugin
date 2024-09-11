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

import com.github.legman.Subscribe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sonia.scm.EagerSingleton;
import sonia.scm.cache.Cache;
import sonia.scm.cache.CacheManager;
import sonia.scm.plugin.Extension;
import sonia.scm.repository.Person;
import sonia.scm.user.User;
import sonia.scm.user.UserEvent;

import jakarta.inject.Inject;

/**
 * @author Sebastian Sdorra
 */
@Extension
@EagerSingleton
public class MappingCacheListener {

    private static final Logger logger =
            LoggerFactory.getLogger(MappingCacheListener.class);
    private Cache<String, Person> cache;

    @Inject
    public MappingCacheListener(CacheManager cacheManager) {
        this.cache = cacheManager.getCache(AbstractMappingPreProcessorFactory.CACHE_NAME);
    }

    @Subscribe
    public void onEvent(UserEvent userEvent) {
        User user = userEvent.getItem();

        if (cache != null) {
            cache.clear();

            logger.debug("clear Mapping cache because user {} has changed",
                    user.getName());
        } else {
            logger.warn("Mapping cache is not available");
        }
    }

}
