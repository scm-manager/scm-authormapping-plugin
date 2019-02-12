package sonia.scm.authormapping;

import sonia.scm.repository.Changeset;
import sonia.scm.repository.ChangesetPreProcessor;
import sonia.scm.repository.Person;
import sonia.scm.util.AssertUtil;

public class AuthorMappingChangesetPreProcessor extends MappingPreProcessor implements ChangesetPreProcessor {

    public AuthorMappingChangesetPreProcessor(MappingResolver resolver) {
        super(resolver);
    }

    @Override
    public void process(Changeset changeset)
    {
        AssertUtil.assertIsNotNull(changeset);

        Person person = changeset.getAuthor();

        person = resolve(person);
        changeset.setAuthor(person);

        // append gravatar hash to fix plugin order
        super.appendGravatarProperty(changeset, person);
    }
}
