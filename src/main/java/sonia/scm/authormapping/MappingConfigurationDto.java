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
