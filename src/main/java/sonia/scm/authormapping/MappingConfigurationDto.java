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

import de.otto.edison.hal.HalRepresentation;
import de.otto.edison.hal.Links;
import lombok.Data;
import lombok.NoArgsConstructor;
import sonia.scm.api.v2.resources.PersonDto;

import java.util.Map;

@NoArgsConstructor
@Data
public class MappingConfigurationDto extends HalRepresentation {
    private boolean enableAutoMapping;
    private Map<String, PersonDto> manualMapping;

    @Override
    @SuppressWarnings("squid:S1185") // We want to have this method available in this package
    protected HalRepresentation add(Links links) {
        return super.add(links);
    }
}
