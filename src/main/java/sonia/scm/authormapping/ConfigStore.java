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

import com.google.inject.Inject;
import com.google.inject.Singleton;
import sonia.scm.repository.Repository;
import sonia.scm.store.ConfigurationStore;
import sonia.scm.store.ConfigurationStoreFactory;

@Singleton
public class ConfigStore {
  public static final String NAME = "authormapping";
  private ConfigurationStoreFactory storeFactory;

  @Inject
  public ConfigStore(ConfigurationStoreFactory configurationStoreFactory) {
      this.storeFactory = configurationStoreFactory;
  }

  public void storeConfiguration(MappingConfiguration configuration, Repository repository) {
      createStore(repository).set(configuration);
  }

  public void storeConfiguration(MappingConfiguration configuration, String repositoryId) {
    createStore(repositoryId).set(configuration);
  }

  public MappingConfiguration getConfiguration(Repository repository) {
      return createStore(repository).get();
  }

  public MappingConfiguration getConfiguration(String repositoryId) {
    return createStore(repositoryId).get();
  }

  private ConfigurationStore<MappingConfiguration> createStore(String repositoryId) {
      return storeFactory.withType(MappingConfiguration.class).withName(NAME).forRepository(repositoryId).build();
  }

  private ConfigurationStore<MappingConfiguration> createStore(Repository repository) {
    return storeFactory.withType(MappingConfiguration.class).withName(NAME).forRepository(repository).build();
  }
}
