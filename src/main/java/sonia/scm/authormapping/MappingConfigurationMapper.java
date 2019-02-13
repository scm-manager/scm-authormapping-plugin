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
import sonia.scm.repository.RepositoryPermission;
import sonia.scm.repository.RepositoryPermissions;

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
        if (RepositoryPermissions.modify(repository).isPermitted()) {
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
