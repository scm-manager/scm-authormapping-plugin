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
package sonia.scm.authormapping.update;

import com.google.common.collect.ImmutableMap;
import org.junit.Before;
import org.junit.Test;
import sonia.scm.authormapping.ConfigStore;
import sonia.scm.authormapping.MappingConfiguration;
import sonia.scm.repository.Person;
import sonia.scm.store.ConfigurationStoreFactory;
import sonia.scm.store.InMemoryConfigurationStoreFactory;
import sonia.scm.update.V1PropertyDaoTestUtil;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class AuthorMappingV2ConfigMigrationUpdateStepTest {

  private final static String REPO_NAME = "repo";

  private V1PropertyDaoTestUtil testUtil = new V1PropertyDaoTestUtil();

  private ConfigurationStoreFactory storeFactory = new InMemoryConfigurationStoreFactory();

  private ConfigStore configStore;

  private AuthorMappingV2ConfigMigrationUpdateStep updateStep;

  @Before
  public void init() {
    configStore = new ConfigStore(storeFactory);
    updateStep = new AuthorMappingV2ConfigMigrationUpdateStep(testUtil.getPropertyDAO(), configStore);
  }

  @Test
  public void shouldMigrateRepositoryConfigWithMultipleMappings() {
    Map<String, String> mockedValues =
      ImmutableMap.of(
        "sonia.authormapping.manual-mapping", "undefined,Third One,third@one.de;second,Second One,second@one.de;first,First One,first@one.de;",
        "sonia.authormapping.enableAutoMapping","false"
      );

    testUtil.mockRepositoryProperties(new V1PropertyDaoTestUtil.PropertiesForRepository(REPO_NAME, mockedValues));

    updateStep.doUpdate();

    MappingConfiguration secondMapping = createMappingObject(false, "second", "Second One", "second@one.de");
    MappingConfiguration thirdMapping = createMappingObject(false, "undefined", "Third One", "third@one.de");

    assertThat(configStore.getConfiguration(REPO_NAME).isEnableAutoMapping()).isFalse();
    assertThat(configStore.getConfiguration(REPO_NAME).getManualMapping().values()).contains(secondMapping.getMapping("second"));
    assertThat(configStore.getConfiguration(REPO_NAME).getManualMapping().values()).contains(thirdMapping.getMapping("undefined"));
    assertThat(configStore.getConfiguration(REPO_NAME).getManualMapping().values().size()).isEqualTo(3);
  }

  @Test
  public void shouldEnableAutoMappingAfterMigrationIfNotSet() {
    Map<String, String> mockedValues =
      ImmutableMap.of(
        "sonia.authormapping.manual-mapping", "undefined,Third One,third@one.de;second,Second One,second@one.de;first,First One,first@one.de;"
      );

    testUtil.mockRepositoryProperties(new V1PropertyDaoTestUtil.PropertiesForRepository(REPO_NAME, mockedValues));

    updateStep.doUpdate();

    assertThat(configStore.getConfiguration(REPO_NAME).isEnableAutoMapping()).isTrue();
  }

  @Test
  public void skipRepositoriesWithoutAuthorMappingConfig() {
    Map<String, String> mockedValues =
      ImmutableMap.of(
        "any", "value"
      );

    testUtil.mockRepositoryProperties(new V1PropertyDaoTestUtil.PropertiesForRepository(REPO_NAME, mockedValues));

    updateStep.doUpdate();

    assertThat(configStore.getConfiguration(REPO_NAME)).isNull();
  }

  private MappingConfiguration createMappingObject(boolean enableAutoMapping, String user, String displayName, String email) {
    Person person = new Person(displayName, email);
    Map<String, Person> personMap = new HashMap<>();
    personMap.put(user, person);
   return new MappingConfiguration(enableAutoMapping, personMap);
  }
}
