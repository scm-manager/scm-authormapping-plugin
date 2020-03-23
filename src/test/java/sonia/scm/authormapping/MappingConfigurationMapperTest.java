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


import com.github.sdorra.shiro.ShiroRule;
import com.github.sdorra.shiro.SubjectAware;
import org.apache.commons.collections.map.SingletonMap;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import sonia.scm.api.v2.resources.ScmPathInfoStore;
import sonia.scm.repository.Person;
import sonia.scm.repository.PersonTestData;
import sonia.scm.repository.RepositoryTestData;

import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MappingConfigurationMapperTest {


    private URI baseUri = URI.create("http://example.com/base/");
    private URI expectedBaseUri;

    @Rule
    public ShiroRule shiro = new ShiroRule();

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private ScmPathInfoStore scmPathInfoStore;

    @InjectMocks
    MappingConfigurationMapperImpl mapper;


    @Before
    public void init() {
        when(scmPathInfoStore.get().getApiRestUri()).thenReturn(baseUri);
        expectedBaseUri = baseUri.resolve("v2/authormapping/configuration/");
    }


    @Test
    @SubjectAware(username = "trillian",
            password = "secret",
            configuration = "classpath:sonia/scm/authormapping/shiro.ini"
    )
    public void shouldAddHalLinks() {
        MappingConfigurationDto dto = mapper.map(createConfiguration(), RepositoryTestData.createHeartOfGold());
        assertEquals(expectedBaseUri.toString() + "hitchhiker/HeartOfGold", dto.getLinks().getLinkBy("self").get().getHref());
        assertEquals(expectedBaseUri.toString() + "hitchhiker/HeartOfGold", dto.getLinks().getLinkBy("update").get().getHref());
    }

    @Test
    @SubjectAware(username = "unpriv",
            password = "secret",
            configuration = "classpath:sonia/scm/authormapping/shiro.ini"
    )
    public void shouldNotAddUpdateLinkIfNotPermitted() {
        MappingConfigurationDto dto = mapper.map(createConfiguration(), RepositoryTestData.createHeartOfGold());
        assertEquals(expectedBaseUri.toString() + "hitchhiker/HeartOfGold", dto.getLinks().getLinkBy("self").get().getHref());
        assertThat(dto.getLinks().getLinkBy("update")).isEmpty();
    }

    private MappingConfiguration createConfiguration() {
        Person trillian = PersonTestData.TILLIAN;
        return new MappingConfiguration(true, new SingletonMap("Trillian", trillian));
    }
}