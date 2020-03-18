/**
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

import com.google.inject.Inject;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import sonia.scm.ContextEntry;
import sonia.scm.api.v2.resources.ErrorDto;
import sonia.scm.repository.NamespaceAndName;
import sonia.scm.repository.Repository;
import sonia.scm.repository.RepositoryManager;
import sonia.scm.web.VndMediaType;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static sonia.scm.NotFoundException.notFound;

@OpenAPIDefinition(tags = {
  @Tag(name = "AuthorMapping Plugin", description = "AuthorMapping plugin provided endpoints")
})
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
    @Operation(
      summary = "Get authormapping configuration",
      description = "Returns the authormapping configuration.",
      tags = "AuthorMapping Plugin",
      operationId = "authormapping_get_config"
    )
    @ApiResponse(
      responseCode = "200",
      description = "success",
      content = @Content(
        mediaType = MediaType.APPLICATION_JSON,
        schema = @Schema(implementation = MappingConfigurationDto.class)
      )
    )
    @ApiResponse(responseCode = "401", description = "not authenticated / invalid credentials")
    @ApiResponse(responseCode = "403", description = "not authorized /  the current user does not have the \"authormapping\" privilege")
    @ApiResponse(
      responseCode = "500",
      description = "internal server error",
      content = @Content(
        mediaType = VndMediaType.ERROR_TYPE,
        schema = @Schema(implementation = ErrorDto.class)
      )
    )
    public Response getConfiguration(@PathParam("namespace") String namespace, @PathParam("name") String name) {

        Repository repository = loadRepository(namespace, name);
        PermissionCheck.check(repository);

        MappingConfiguration configuration = mappingManager.getConfiguration(repository);
        return Response.ok(mapper.map(configuration, repository)).build();
    }

    @PUT
    @Path("/{namespace}/{name}")
    @Consumes({MediaType.APPLICATION_JSON})
    @Operation(
      summary = "Update authormapping configuration",
      description = "Modifies the authormapping configuration.",
      tags = "AuthorMapping Plugin",
      operationId = "authormapping_update_config"
    )
    @ApiResponse(responseCode = "204", description = "update success")
    @ApiResponse(responseCode = "401", description = "not authenticated / invalid credentials")
    @ApiResponse(responseCode = "403", description = "not authorized /  the current user does not have the \"authormapping\" privilege")
    @ApiResponse(
      responseCode = "500",
      description = "internal server error",
      content = @Content(
        mediaType = VndMediaType.ERROR_TYPE,
        schema = @Schema(implementation = ErrorDto.class)
      )
    )
    public Response updateConfiguration(@PathParam("namespace") String namespace, @PathParam("name") String name, MappingConfigurationDto dto) {
        Repository repository = loadRepository(namespace, name);
        PermissionCheck.check(repository);
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
