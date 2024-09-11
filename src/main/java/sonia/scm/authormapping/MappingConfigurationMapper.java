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

import de.otto.edison.hal.Link;
import de.otto.edison.hal.Links;
import lombok.NoArgsConstructor;
import org.mapstruct.AfterMapping;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import sonia.scm.api.v2.resources.BaseMapper;
import sonia.scm.api.v2.resources.LinkBuilder;
import sonia.scm.api.v2.resources.ScmPathInfoStore;
import sonia.scm.repository.Repository;

import jakarta.inject.Inject;

import static de.otto.edison.hal.Links.linkingTo;

@Mapper
@NoArgsConstructor
public abstract class MappingConfigurationMapper extends BaseMapper<MappingConfiguration, MappingConfigurationDto> {


    @Inject
    ScmPathInfoStore scmPathInfoStore;

    public abstract MappingConfigurationDto map(MappingConfiguration configuration, @Context Repository repository);

    public abstract MappingConfiguration map(MappingConfigurationDto dto);

    @AfterMapping
    public void addLinks(MappingConfiguration source, @MappingTarget MappingConfigurationDto target, @Context Repository repository) {
        Links.Builder linksBuilder = linkingTo().self(self(repository));
        if (PermissionCheck.isPermitted(repository)) {
            linksBuilder.single(Link.link("update", update(repository)));

        }
        target.add(linksBuilder.build());
    }

    private String self(Repository repository) {
        LinkBuilder linkBuilder = new LinkBuilder(scmPathInfoStore.get(), ConfigurationResource.class);
        return linkBuilder.method("getConfiguration").parameters(repository.getNamespace(), repository.getName()).href();
    }

    private String update(Repository repository) {
        LinkBuilder linkBuilder = new LinkBuilder(scmPathInfoStore.get(), ConfigurationResource.class);
        return linkBuilder.method("updateConfiguration").parameters(repository.getNamespace(), repository.getName()).href();
    }

}
