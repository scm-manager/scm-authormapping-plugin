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

import React from "react";
import { useTranslation } from "react-i18next";
import { Configuration } from "@scm-manager/ui-components";
import { Subtitle } from "@scm-manager/ui-core";
import AuthorMappingConfigurationForm from "./AuthorMappingConfigurationForm";

type Props = {
  link: string;
};

const ConfigurationContainer: React.FC<Props> = ({ link }) => {
  const { t } = useTranslation("plugins");

  return (
    <>
      <Subtitle>{t("scm-authormapping-plugin.config.title")}</Subtitle>
      <Configuration link={link} render={(props) => <AuthorMappingConfigurationForm {...props} />} />
    </>
  );
};

export default ConfigurationContainer;
