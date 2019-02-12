package sonia.scm.authormapping;

import com.google.inject.AbstractModule;
import org.mapstruct.factory.Mappers;
import sonia.scm.plugin.Extension;

@Extension
public class Module extends AbstractModule {
    @Override
    protected void configure() {
        bind(MappingConfigurationMapper.class).to(Mappers.getMapper(MappingConfigurationMapper.class).getClass());
    }
}
