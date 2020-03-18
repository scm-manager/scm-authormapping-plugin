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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sonia.scm.repository.Changeset;
import sonia.scm.repository.Person;
import sonia.scm.util.Util;

//~--- JDK imports ------------------------------------------------------------

import java.io.UnsupportedEncodingException;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author Sebastian Sdorra
 */
public class MappingPreProcessor {


    public static final String DIGEST_ENCODING = "CP1252";
    public static final String DIGEST_TYPE = "MD5";
    public static final String PROPERTY_GRAVATAR = "gravatar-hash";

    private static final Logger logger =
            LoggerFactory.getLogger(MappingPreProcessor.class);

    private MappingResolver resolver;


    public MappingPreProcessor() {
    }

    public MappingPreProcessor(MappingResolver resolver) {
        this.resolver = resolver;
    }


    protected void appendGravatarProperty(Changeset changeset, Person author) {
        String gravatarHash = changeset.getProperty(PROPERTY_GRAVATAR);

        if (Util.isNotEmpty(gravatarHash)) {
            setGravatarHash(changeset, author);
        }
    }

    private String hex(byte[] array) {
        StringBuilder sb = new StringBuilder();

        for (byte b : array) {
            sb.append(Integer.toHexString((b & 0xFF) | 0x100), 1, 3);
        }

        return sb.toString();
    }

    private String md5Hex(String message) {
        String out = null;

        if (Util.isNotEmpty(message)) {
            message = message.toLowerCase().trim();

            try {
                MessageDigest md = MessageDigest.getInstance(DIGEST_TYPE);

                out = hex(md.digest(message.getBytes(DIGEST_ENCODING)));
            } catch (NoSuchAlgorithmException | UnsupportedEncodingException ex) {
                logger.error(ex.getMessage(), ex);
            }
        }

        return out;
    }


    Person resolve(Person person) {
        if (person == null) {
            person = new Person();
        }

        String name = person.getName();

        if (Util.isNotEmpty(name)) {
            person = resolver.resolve(name, person.getMail());
        } else if (logger.isWarnEnabled()) {
            logger.warn("person object has no name");
        }

        return person;
    }

    private void setGravatarHash(Changeset changeset, Person person) {
        String value = person.getMail();

        if (Util.isEmpty(value)) {
            value = person.getName();
        }

        if (Util.isNotEmpty(value)) {
            value = md5Hex(value);
            changeset.setProperty(PROPERTY_GRAVATAR, value);
        }
    }

}
