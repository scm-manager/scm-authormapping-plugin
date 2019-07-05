package sonia.scm.authormapping.update;

import com.google.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sonia.scm.authormapping.MappingConfiguration;
import sonia.scm.migration.UpdateStep;
import sonia.scm.plugin.Extension;
import sonia.scm.repository.Person;
import sonia.scm.store.ConfigurationStore;
import sonia.scm.store.ConfigurationStoreFactory;
import sonia.scm.update.V1Properties;
import sonia.scm.update.V1PropertyDAO;
import sonia.scm.version.Version;

import java.util.HashMap;
import java.util.Map;

import static sonia.scm.update.V1PropertyReader.REPOSITORY_PROPERTY_READER;
import static sonia.scm.version.Version.*;

@Extension
public class AuthorMappingV2ConfigMigrationUpdateStep implements UpdateStep {

  public static final String STORE_NAME = "authormapping";

  private static final Logger LOG = LoggerFactory.getLogger(AuthorMappingV2ConfigMigrationUpdateStep.class);

  private final V1PropertyDAO v1PropertyDAO;
  private final ConfigurationStoreFactory storeFactory;

  @Inject
  public AuthorMappingV2ConfigMigrationUpdateStep(V1PropertyDAO v1PropertyDAO, ConfigurationStoreFactory storeFactory) {
    this.v1PropertyDAO = v1PropertyDAO;
    this.storeFactory = storeFactory;
  }

  @Override
  public void doUpdate() {
    v1PropertyDAO
      .getProperties(REPOSITORY_PROPERTY_READER)
      .havingAnyOf("sonia.authormapping.manual-mapping")
      .forEachEntry((key, properties) ->  storeConfiguration(buildConfig(key, properties), key));
  }

  private MappingConfiguration buildConfig(String repositoryId, V1Properties properties) {
    LOG.debug("migrating repository specific redmine configuration for repository id {}", repositoryId);

    String[] splittedProperties = properties.get("sonia.authormapping.manual-mapping").split(",");
    String username = splittedProperties[0];
    Person person = new Person(splittedProperties[1], splittedProperties[2]);
    Map<String, Person> mappedUser = new HashMap<>();
    mappedUser.put(username, person);

    return new MappingConfiguration(
      properties.getBoolean("sonia.authormapping.enableAutoMapping").get(),
      mappedUser
    );
  }

  private void storeConfiguration(MappingConfiguration configuration, String repositoryId) {
    createStore(repositoryId).set(configuration);
  }

  private ConfigurationStore<MappingConfiguration> createStore(String repositoryId) {
    return storeFactory.withType(MappingConfiguration.class).withName(STORE_NAME).forRepository(repositoryId).build();
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
