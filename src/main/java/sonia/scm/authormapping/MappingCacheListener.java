/**
 * Copyright (c) 2010, Sebastian Sdorra
 * All rights reserved.
 * <p>
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * <p>
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * 3. Neither the name of SCM-Manager; nor the names of its
 * contributors may be used to endorse or promote products derived from this
 * software without specific prior written permission.
 * <p>
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
 * <p>
 * http://bitbucket.org/sdorra/scm-manager
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

import javax.inject.Inject;

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
