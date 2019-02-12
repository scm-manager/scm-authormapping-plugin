package sonia.scm.authormapping;

import lombok.NoArgsConstructor;
import org.mapstruct.Mapper;
import sonia.scm.api.v2.resources.BaseMapper;

@Mapper
@NoArgsConstructor
public abstract class MappingConfigurationMapper extends BaseMapper<MappingConfiguration, MappingConfigurationDto> {

    @Override
    public abstract MappingConfigurationDto map(MappingConfiguration configuration);

    public abstract MappingConfiguration map(MappingConfigurationDto dto);
}
