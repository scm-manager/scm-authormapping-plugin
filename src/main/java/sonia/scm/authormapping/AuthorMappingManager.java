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
import sonia.scm.repository.Repository;

import java.util.HashMap;

public class AuthorMappingManager {
    private ConfigStore configStore;

    @Inject
    public AuthorMappingManager(ConfigStore configStore) {
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
