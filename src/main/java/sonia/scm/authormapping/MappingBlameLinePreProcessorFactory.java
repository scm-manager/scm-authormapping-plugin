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
