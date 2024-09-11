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
