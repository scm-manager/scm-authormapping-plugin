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
