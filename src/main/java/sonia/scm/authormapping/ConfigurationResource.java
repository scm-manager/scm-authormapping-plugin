package sonia.scm.authormapping;

import com.google.inject.Inject;
import sonia.scm.ContextEntry;
import sonia.scm.repository.NamespaceAndName;
import sonia.scm.repository.Repository;
import sonia.scm.repository.RepositoryManager;
import sonia.scm.repository.RepositoryPermissions;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static sonia.scm.NotFoundException.notFound;

@Path("v2/authormapping/configuration")
public class ConfigurationResource {

    private RepositoryManager repositoryManager;
    private AuthorMappingManager mappingManager;
    private MappingConfigurationMapper mapper;

    @Inject
    public ConfigurationResource(RepositoryManager repositoryManager, AuthorMappingManager mappingManager, MappingConfigurationMapper mapper) {
        this.repositoryManager = repositoryManager;
        this.mappingManager = mappingManager;
        this.mapper = mapper;
    }

    @GET
    @Path("/{namespace}/{name}")
    @Produces({MediaType.APPLICATION_JSON})
    public Response getConfiguration(@PathParam("namespace") String namespace, @PathParam("name") String name) {

        Repository repository = loadRepository(namespace, name);
        RepositoryPermissions.permissionRead(repository).check();

        MappingConfiguration configuration = mappingManager.getConfiguration(repository);
        return Response.ok(mapper.map(configuration, repository)).build();
    }

    @PUT
    @Path("/{namespace}/{name}")
    @Consumes({MediaType.APPLICATION_JSON})
    public Response updateConfiguration(@PathParam("namespace") String namespace, @PathParam("name") String name, MappingConfigurationDto dto) {


        Repository repository = loadRepository(namespace, name);
        RepositoryPermissions.modify(repository).check();
        mappingManager.saveConfiguration(mapper.map(dto), repository);
        return Response.noContent().build();
    }

    private Repository loadRepository(String namespace, String name) {
        NamespaceAndName namespaceAndName = new NamespaceAndName(namespace, name);
        Repository repository = repositoryManager.get(namespaceAndName);
        if (repository == null) {
            throw notFound(ContextEntry.ContextBuilder.entity(namespaceAndName));
        } return repository;
    }
}
