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

import javax.inject.Inject;

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
