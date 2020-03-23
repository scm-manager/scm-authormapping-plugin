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

import com.google.common.base.Strings;
import com.google.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sonia.scm.authormapping.ConfigStore;
import sonia.scm.authormapping.MappingConfiguration;
import sonia.scm.migration.UpdateStep;
import sonia.scm.plugin.Extension;
import sonia.scm.repository.Person;
import sonia.scm.update.V1Properties;
import sonia.scm.update.V1PropertyDAO;
import sonia.scm.version.Version;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static java.util.Optional.empty;
import static java.util.Optional.of;
import static sonia.scm.update.V1PropertyReader.REPOSITORY_PROPERTY_READER;
import static sonia.scm.version.Version.*;

@Extension
public class AuthorMappingV2ConfigMigrationUpdateStep implements UpdateStep {

  private static final Logger LOG = LoggerFactory.getLogger(AuthorMappingV2ConfigMigrationUpdateStep.class);

  private final V1PropertyDAO v1PropertyDAO;
  private final ConfigStore configStore;

  @Inject
  public AuthorMappingV2ConfigMigrationUpdateStep(V1PropertyDAO v1PropertyDAO, ConfigStore configStore) {
    this.v1PropertyDAO = v1PropertyDAO;
    this.configStore = configStore;
  }

  @Override
  public void doUpdate() {
    v1PropertyDAO
      .getProperties(REPOSITORY_PROPERTY_READER)
      .havingAnyOf("sonia.authormapping.manual-mapping")
      .forEachEntry((key, properties) -> {
        buildConfig(key, properties).ifPresent(mappingConfiguration ->
          configStore.storeConfiguration(mappingConfiguration, key));
      });
  }

  private Optional<MappingConfiguration> buildConfig(String repositoryId, V1Properties properties) {
    LOG.debug("migrating repository specific authormapping configuration for repository id {}", repositoryId);

    String usersProperties = properties.get("sonia.authormapping.manual-mapping");

    if (Strings.isNullOrEmpty(usersProperties)) {
      return empty();
    }
    String[] splittedUsersProperties = usersProperties.split(";");
    Map<String, Person> mappedUser = new HashMap<>();

    for (String userProperties : splittedUsersProperties) {
      String[] splittedUserProperties = userProperties.split(",");
      String username = splittedUserProperties[0];
      Person person = new Person(splittedUserProperties[1], splittedUserProperties[2]);
      mappedUser.put(username, person);
    }

    return of(new MappingConfiguration(
      properties.getBoolean("sonia.authormapping.enableAutoMapping").orElse(true),
      mappedUser
    ));
  }

  @Override
  public Version getTargetVersion() {
    return parse("2.0.0");
  }

  @Override
  public String getAffectedDataType() {
    return "sonia.scm.authormapping.config.repository.xml";
  }
}
