/**
 * Copyright (c) 2010, Sebastian Sdorra
 * All rights reserved.
 * <p>
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * <p>
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * 3. Neither the name of SCM-Manager; nor the names of its
 * contributors may be used to endorse or promote products derived from this
 * software without specific prior written permission.
 * <p>
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * <p>
 * http://bitbucket.org/sdorra/scm-manager
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
