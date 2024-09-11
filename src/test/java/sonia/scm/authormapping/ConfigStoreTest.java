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

import static org.assertj.core.api.Assertions.*;

@RunWith(MockitoJUnitRunner.class)
public class ConfigStoreTest {

    ConfigurationStore<MappingConfiguration> configurationStore;

    ConfigStore configStore;

  Repository heartOfGold = RepositoryTestData.createHeartOfGold();

  @Before
    public void setUp() {
        ConfigurationStoreFactory storeFactory = new InMemoryConfigurationStoreFactory();
        configurationStore = storeFactory.withType(MappingConfiguration.class).withName(ConfigStore.NAME).forRepository(heartOfGold.getId()).build();
        configStore = new ConfigStore(storeFactory);
    }

    @Test
    public void shouldReturnExistingConfig() {
        MappingConfiguration mappingConfiguration = createTestConfiguration();
        configurationStore.set(mappingConfiguration);

        assertThat(configStore.getConfiguration(heartOfGold)).isSameAs(mappingConfiguration);
    }

    @Test
    public void shouldSaveConfig() {
        MappingConfiguration testConfiguration = createTestConfiguration();
        configStore.storeConfiguration(testConfiguration, heartOfGold);

        assertThat(configurationStore.get()).isSameAs(testConfiguration);
    }

    private MappingConfiguration createTestConfiguration() {
        Person trillian = PersonTestData.TILLIAN;
        return new MappingConfiguration(false, new SingletonMap("trish", trillian));
    }
}
