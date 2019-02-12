// @flow
import React from "react";
import type { AuthorMappingConfiguration, Person } from "./types";

type Props = {
  initialConfiguration: AuthorMappingConfiguration
};
type State = {};

class AuthorMappingConfigurationForm extends React.Component<Props, State> {
  render() {
    return <>{this.renderConfig()}</>;
  }

  renderConfig = () => {
    const { initialConfiguration } = this.props;
    if (!initialConfiguration) {
      return null;
    }

    const { manualMapping } = initialConfiguration;
    return (
      <table className="ard-table table is-hoverable is-fullwidth">
        <thead>
          <th>Username</th>
          <th>Mapped Username</th>
          <th>Mapped Email</th>
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
    return (
      <tr>
        <td>{name}</td>
        <td>{value.name}</td>
        <td>{value.mail}</td>
      </tr>
    );
  };
}

export default AuthorMappingConfigurationForm;
