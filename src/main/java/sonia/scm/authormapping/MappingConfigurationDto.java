package sonia.scm.authormapping;

import de.otto.edison.hal.HalRepresentation;
import lombok.Data;
import lombok.NoArgsConstructor;
import sonia.scm.api.v2.resources.PersonDto;

import java.util.Map;

@NoArgsConstructor
@Data
public class MappingConfigurationDto extends HalRepresentation {
    private boolean enableAutoMapping;
    private Map<String, PersonDto> manualMapping;
}
