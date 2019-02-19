package sonia.scm.authormapping;

import sonia.scm.api.v2.resources.Enrich;
import sonia.scm.api.v2.resources.HalAppender;
import sonia.scm.api.v2.resources.HalEnricher;
import sonia.scm.api.v2.resources.HalEnricherContext;
import sonia.scm.api.v2.resources.LinkBuilder;
import sonia.scm.api.v2.resources.ScmPathInfoStore;
import sonia.scm.plugin.Extension;
import sonia.scm.repository.Repository;

import javax.inject.Inject;
import javax.inject.Provider;

@Extension
@Enrich(Repository.class)
public class AuthorMappingRepositoryHalEnricher implements HalEnricher {
    private Provider<ScmPathInfoStore> scmPathInfoStoreProvider;

    @Inject
    public AuthorMappingRepositoryHalEnricher(Provider<ScmPathInfoStore> scmPathInfoStoreProvider) {
        this.scmPathInfoStoreProvider = scmPathInfoStoreProvider;
    }

    @Override
    public void enrich(HalEnricherContext context, HalAppender appender) {
        Repository repository = context.oneRequireByType(Repository.class);
        if (PermissionCheck.isPermitted(repository)) {
          String link = new LinkBuilder(scmPathInfoStoreProvider.get().get(), ConfigurationResource.class)
            .method("getConfiguration")
            .parameters(repository.getNamespace(), repository.getName())
            .href();
          appender.appendLink("authorMappingConfig", link);
        }
    }
}
