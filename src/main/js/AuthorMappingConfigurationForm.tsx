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

import React, { FC, useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { Checkbox } from "@scm-manager/ui-components";
import { AuthorMapping, AuthorMappingConfiguration, Person, Mapping } from "./types";
import AuthorMappingFormComponent from "./AuthorMappingFormComponent";
import DeleteMappingButton from "./DeleteMappingButton";

type Props = {
  initialConfiguration: AuthorMappingConfiguration;
  onConfigurationChange: (p1: AuthorMappingConfiguration, p2: boolean) => void;
};

const AuthorMappingConfigurationForm: FC<Props> = ({ initialConfiguration, onConfigurationChange }) => {
  const [t] = useTranslation("plugins");
  const [configuration, setConfiguration] = useState<AuthorMappingConfiguration>(initialConfiguration);

  useEffect(() => {
    onConfigurationChange(configuration, isStateValid());
  }, [configuration]);

  const isStateValid = () => {
    const { enableAutoMapping, manualMapping } = configuration;
    return enableAutoMapping !== undefined && manualMapping !== undefined;
  };

  const enableAutoMappingChanged = (value: boolean) => {
    setConfiguration({
      ...configuration,
      enableAutoMapping: value
    });
  };

  const renderTable = () => {
    if (!configuration) {
      return null;
    }

    const { manualMapping } = configuration;
    if (manualMapping && Object.keys(manualMapping).length > 0) {
      return (
        <table className="table is-hoverable is-fullwidth">
          <thead>
          <th>{t("scm-authormapping-plugin.config.form.author")}</th>
          <th>{t("scm-authormapping-plugin.config.form.mappedName")}</th>
          <th>{t("scm-authormapping-plugin.config.form.mappedMail")}</th>
          <th />
          </thead>
          <tbody>
          {Object.keys(manualMapping).map(key => {
            return renderEntry(key, manualMapping[key]);
          })}
          </tbody>
        </table>
      );
    }
    return null;
  };

  const renderEntry = (name: string, value: Person) => {
    const mapping = {
      author: name,
      mappedName: value.name,
      mappedMail: value.mail
    };
    return (
      <tr>
        <td>{mapping.author}</td>
        <td>{mapping.mappedName}</td>
        <td>{mapping.mappedMail}</td>
        <td>
          <DeleteMappingButton
            mapping={mapping}
            onDelete={removeMapping}
            label={t("scm-authormapping-plugin.config.form.remove")}
          />
        </td>
      </tr>
    );
  };

  const removeMapping = (mapping: AuthorMapping) => {
    const currentMapping = configuration.manualMapping;

    const newMapping: Mapping = {};
    Object.keys(currentMapping)
      .filter(key => key !== mapping.author)
      .forEach(key => {
        newMapping[key] = currentMapping[key];
      });

    updateManualMapping(newMapping);
  };

  const addMapping = (author: string, mappedName: string, mappedMail: string) => {
    const currentMapping = configuration.manualMapping;
    const newMapping = {
      ...currentMapping,
      [author]: {
        name: mappedName,
        mail: mappedMail
      }
    };
    updateManualMapping(newMapping);
  };

  const updateManualMapping = (newMapping: Mapping) => {
    setConfiguration({
      ...configuration,
      manualMapping: newMapping
    });
  };

  return (
    <>
      {renderTable()}
      <Checkbox
        name="enableAutoMapping"
        label={t("scm-authormapping-plugin.config.form.enableAuto")}
        checked={configuration.enableAutoMapping}
        onChange={enableAutoMappingChanged}
        helpText={t("scm-authormapping-plugin.config.form.enableAutoHelpText")}
      />
      <AuthorMappingFormComponent onSubmit={addMapping} />
    </>
  )
};

export default AuthorMappingConfigurationForm;
