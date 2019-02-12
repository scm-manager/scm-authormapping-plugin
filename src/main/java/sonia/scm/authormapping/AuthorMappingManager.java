package sonia.scm.authormapping;

import com.google.inject.Inject;
import sonia.scm.repository.Repository;

import java.util.HashMap;

public class AuthorMappingManager {
    private AuthorMappingConfigStore configStore;

    @Inject
    public AuthorMappingManager(AuthorMappingConfigStore configStore) {
        this.configStore = configStore;
    }

    public void saveConfiguration(MappingConfiguration configuration, Repository repository) {
        configStore.storeConfiguration(configuration, repository);
    }

    public MappingConfiguration getConfiguration(Repository repository) {
        MappingConfiguration configuration = configStore.getConfiguration(repository);
        if (configuration == null) {
            return new MappingConfiguration(true, new HashMap<>());
        }
        return configuration;
    }
}
