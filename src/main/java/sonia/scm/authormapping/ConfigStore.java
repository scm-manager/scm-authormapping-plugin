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
