// @flow
import React from "react";
import type {
  AuthorMapping,
  AuthorMappingConfiguration,
  Person,
  Mapping
} from "./types";
import AuthorMappingFormComponent from "./AuthorMappingFormComponent";
import DeleteMappingButton from "./DeleteMappingButton";
import { translate } from "react-i18next";
import { Checkbox } from "@scm-manager/ui-components";

type Props = {
  initialConfiguration: AuthorMappingConfiguration,
  onConfigurationChange: (AuthorMappingConfiguration, boolean) => void,
  readOnly: boolean,

  t: string => string
};
type State = {
  configuration: AuthorMappingConfiguration
};

class AuthorMappingConfigurationForm extends React.Component<Props, State> {
  constructor(props: Props) {
    super(props);
    this.state = { configuration: props.initialConfiguration };
  }

  isStateValid = () => {
    const { configuration } = this.state;
    const { enableAutoMapping, manualMapping } = configuration;
    return enableAutoMapping !== undefined && manualMapping !== undefined;
  };

  render() {
    const { t } = this.props;
    return (
      <>
        {this.renderTable()}
        <Checkbox
          name="enableAutoMapping"
          label={t("scm-authormapping-plugin.config.form.enableAuto")}
          checked={this.state.configuration.enableAutoMapping}
          onChange={this.enableAutoMappingChanged}
        />
        <AuthorMappingFormComponent onSubmit={this.addMapping} />
      </>
    );
  }

  enableAutoMappingChanged = (value: boolean) => {
    this.setState(
      {
        configuration: {
          ...this.state.configuration,
          enableAutoMapping: value
        }
      },
      this.configChanged()
    );
  };

  configChanged = () => {
    this.props.onConfigurationChange(
      this.state.configuration,
      this.isStateValid()
    );
  };

  renderTable = () => {
    const { t } = this.props;
    const { configuration } = this.state;
    if (!configuration) {
      return null;
    }

    const { manualMapping } = configuration;
    return (
      <table className="ard-table table is-hoverable is-fullwidth">
        <thead>
          <th>{t("scm-authormapping-plugin.config.form.author")}</th>
          <th>{t("scm-authormapping-plugin.config.form.mappedName")}</th>
          <th>{t("scm-authormapping-plugin.config.form.mappedMail")}</th>
          <th />
        </thead>
        <tbody>
          {Object.keys(manualMapping).map(key => {
            return this.renderEntry(key, manualMapping[key]);
          })}
        </tbody>
      </table>
    );
  };

  renderEntry = (name: string, value: Person) => {
    const { t } = this.props;
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
            onDelete={this.removeMapping}
            label={t("scm-authormapping-plugin.config.form.remove")}
          />
        </td>
      </tr>
    );
  };

  removeMapping = (mapping: AuthorMapping) => {
    const currentMapping = this.state.configuration.manualMapping;

    let newMapping = {};
    Object.keys(currentMapping)
      .filter(key => key !== mapping.author)
      .forEach(key => {
        newMapping[key] = currentMapping[key];
      });

    this.updateManualMapping(newMapping);
  };

  addMapping = (author: string, mappedName: string, mappedMail: string) => {
    const currentMapping = this.state.configuration.manualMapping;
    const newMapping = {
      ...currentMapping,
      [author]: {
        name: mappedName,
        mail: mappedMail
      }
    };
    this.updateManualMapping(newMapping);
  };

  updateManualMapping = (newMapping: Mapping) => {
    this.setState(
      {
        configuration: {
          ...this.state.configuration,
          manualMapping: newMapping
        }
      },
      this.configChanged
    );
  };
}

export default translate("plugins")(AuthorMappingConfigurationForm);
