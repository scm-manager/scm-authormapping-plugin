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
