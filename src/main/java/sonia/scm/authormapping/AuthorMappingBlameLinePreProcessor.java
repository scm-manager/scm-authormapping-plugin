/*
 * Copyright (c) 2020 - present Cloudogu GmbH
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, version 3.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see https://www.gnu.org/licenses/.
 */

package sonia.scm.authormapping;

import sonia.scm.repository.BlameLine;
import sonia.scm.repository.BlameLinePreProcessor;
import sonia.scm.repository.Person;
import sonia.scm.util.AssertUtil;

public class AuthorMappingBlameLinePreProcessor extends MappingPreProcessor implements BlameLinePreProcessor {
    public AuthorMappingBlameLinePreProcessor(MappingResolver resolver) {
        super(resolver);
    }

    @Override
    public void process(BlameLine blameLine)
    {
        AssertUtil.assertIsNotNull(blameLine);

        Person person = blameLine.getAuthor();

        person = super.resolve(person);
        blameLine.setAuthor(person);
    }
}
