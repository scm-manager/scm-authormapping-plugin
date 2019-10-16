// @flow
import React from "react";
import { translate } from "react-i18next";
import { Configuration, Subtitle } from "@scm-manager/ui-components";
import AuthorMappingConfigurationForm from "./AuthorMappingConfigurationForm";

type Props = {
  link: string,
  t: string => string
};

class ConfigurationContainer extends React.Component<Props> {
  render() {
    const { link, t } = this.props;
    return (
      <>
        <Subtitle subtitle={t("scm-authormapping-plugin.config.title")} />
        <Configuration
          link={link}
          render={props => <AuthorMappingConfigurationForm {...props} />}
        />
      </>
    );
  }
}

export default translate("plugins")(ConfigurationContainer);
