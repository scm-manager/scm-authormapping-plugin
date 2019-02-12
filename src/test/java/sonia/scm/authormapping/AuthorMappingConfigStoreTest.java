package sonia.scm.authormapping;

import org.apache.commons.collections.map.SingletonMap;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import sonia.scm.repository.Person;
import sonia.scm.repository.PersonTestData;
import sonia.scm.repository.Repository;
import sonia.scm.repository.RepositoryTestData;
import sonia.scm.store.ConfigurationStore;
import sonia.scm.store.ConfigurationStoreFactory;
import sonia.scm.store.InMemoryConfigurationStoreFactory;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AuthorMappingConfigStoreTest {

    ConfigurationStore<MappingConfiguration> configurationStore;

    AuthorMappingConfigStore authorMappingConfigStore;

    @Before
    public void setUp() {
        configurationStore = mock(ConfigurationStore.class);
        ConfigurationStoreFactory storeFactory = new InMemoryConfigurationStoreFactory(configurationStore);
        authorMappingConfigStore = new AuthorMappingConfigStore(storeFactory);
    }

    @Test
    public void shouldReturnExistingConfig() {
        Repository heartOfGold = RepositoryTestData.createHeartOfGold();
        MappingConfiguration mappingConfiguration = createTestConfiguration();
        when(configurationStore.get()).thenReturn(mappingConfiguration);

        authorMappingConfigStore.getConfiguration(heartOfGold);

        verify(configurationStore).get();
    }

    @Test
    public void shouldSaveConfig() {
        Repository heartOfGold = RepositoryTestData.createHeartOfGold();
        MappingConfiguration testConfiguration = createTestConfiguration();
        authorMappingConfigStore.storeConfiguration(testConfiguration, heartOfGold);
        verify(configurationStore).set(testConfiguration);
    }

    private MappingConfiguration createTestConfiguration() {
        Person trillian = PersonTestData.TILLIAN;
        return new MappingConfiguration(false, new SingletonMap("trish", trillian));
    }
}