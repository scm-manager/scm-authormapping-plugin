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
