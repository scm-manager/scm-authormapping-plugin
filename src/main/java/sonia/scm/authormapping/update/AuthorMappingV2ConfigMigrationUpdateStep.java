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
