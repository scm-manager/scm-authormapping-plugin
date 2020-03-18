/**
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
