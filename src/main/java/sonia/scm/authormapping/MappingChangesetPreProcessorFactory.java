/**
 * Copyright (c) 2010, Sebastian Sdorra
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 3. Neither the name of SCM-Manager; nor the names of its
 *    contributors may be used to endorse or promote products derived from this
 *    software without specific prior written permission.
 *
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
 *
 * http://bitbucket.org/sdorra/scm-manager
 *
 */



package sonia.scm.authormapping;

//~--- non-JDK imports --------------------------------------------------------

import com.google.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sonia.scm.cache.Cache;
import sonia.scm.cache.CacheManager;
import sonia.scm.plugin.ext.Extension;
import sonia.scm.repository.ChangesetPreProcessor;
import sonia.scm.repository.ChangesetPreProcessorFactory;
import sonia.scm.repository.Person;
import sonia.scm.repository.Repository;
import sonia.scm.user.UserManager;
import sonia.scm.util.AssertUtil;
import sonia.scm.web.security.AdministrationContext;

/**
 *
 * @author Sebastian Sdorra
 */
@Extension
public class MappingChangesetPreProcessorFactory
        implements ChangesetPreProcessorFactory
{

  /** Field description */
  public static final String CACHE_NAME = "sonia.cache.authorname";

  /** Field description */
  public static final String TYPE = "svn";

  /**
   * the logger for AuthorNameChangesetPreProcessorFactory
   */
  private static final Logger logger =
    LoggerFactory.getLogger(MappingChangesetPreProcessorFactory.class);

  //~--- constructors ---------------------------------------------------------

  /**
   * Constructs ...
   *
   *
   * @param adminContext
   * @param userManager
   * @param cacheManager
   */
  @Inject
  public MappingChangesetPreProcessorFactory(
          AdministrationContext adminContext, UserManager userManager,
          CacheManager cacheManager)
  {
    this.adminContext = adminContext;
    this.userManager = userManager;
    this.cache = cacheManager.getCache(String.class, Person.class, CACHE_NAME);
  }

  //~--- methods --------------------------------------------------------------

  /**
   * Method description
   *
   *
   * @param repository
   *
   * @return
   */
  @Override
  public ChangesetPreProcessor createPreProcessor(Repository repository)
  {
    AssertUtil.assertIsNotNull(repository);

    ChangesetPreProcessor cpp = null;

    if (TYPE.equals(repository.getType()))
    {
      if (logger.isTraceEnabled())
      {
        logger.trace(
            "create AuthorNameChangesetPreProcessor for repository {}",
            repository.getName());
      }

      cpp = new MappingChangesetPreProcessor(adminContext, userManager, cache);
    }
    else if (logger.isTraceEnabled())
    {
      logger.trace("skip {} repository, because it is not a svn repository",
                   repository.getName());
    }

    return cpp;
  }

  //~--- fields ---------------------------------------------------------------

  /** Field description */
  private AdministrationContext adminContext;

  /** Field description */
  private Cache<String, Person> cache;

  /** Field description */
  private UserManager userManager;
}