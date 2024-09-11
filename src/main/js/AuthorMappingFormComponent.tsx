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
import { withTranslation, WithTranslation } from "react-i18next";
import { InputField, SubmitButton, validation, Level } from "@scm-manager/ui-components";

type Props = WithTranslation & {
  author?: string;
  mappedName?: string;
  mappedMail?: string;
  onSubmit: (author: string, mappedName: string, mappedMail: string) => void;
};

type State = {
  author: string;
  mappedName: string;
  mappedMail: string;
  valid: boolean;
};

class AuthorMappingFormComponent extends React.Component<Props, State> {
  constructor(props: Props) {
    super(props);
    const { author, mappedName, mappedMail } = props;
    this.state = {
      author: author ? author : "",
      mappedName: mappedName ? mappedName : "",
      mappedMail: mappedMail ? mappedMail : "",
      valid: false
    };
  }
  render() {
    const { t } = this.props;
    return (
      <form>
        <InputField
          onChange={this.onChange}
          validationError={false}
          label={t("scm-authormapping-plugin.config.form.author")}
          value={this.state.author}
          name="author"
          helpText={t("scm-authormapping-plugin.config.form.author-helptext")}
        />
        <InputField
          onChange={this.onChange}
          validationError={false}
          label={t("scm-authormapping-plugin.config.form.mappedName")}
          value={this.state.mappedName}
          name="mappedName"
          helpText={t("scm-authormapping-plugin.config.form.mappedName-helptext")}
        />
        <InputField
          onChange={this.onChange}
          validationError={this.state.mappedMail !== "" && !validation.isMailValid(this.state.mappedMail)}
          type="email"
          label={t("scm-authormapping-plugin.config.form.mappedMail")}
          value={this.state.mappedMail}
          name="mappedMail"
          helpText={t("scm-authormapping-plugin.config.form.mappedMail-helptext")}
        />
        <Level
          right={
            <SubmitButton
              label={t("scm-authormapping-plugin.config.form.add")}
              action={this.onAdd}
              disabled={!this.state.valid}
            />
          }
        />
      </form>
    );
  }

  validateState = () => {
    const { author, mappedName, mappedMail } = this.state;
    const valid = !!author && !!mappedName && !!mappedMail && validation.isMailValid(mappedMail);

    this.setState({
      ...this.state,
      valid
    });
  };

  onChange = (value: string, name: string) => {
    this.setState(
      {
        ...this.state,
        [name]: value
      },
      () => this.validateState()
    );
  };

  onAdd = (event: Event) => {
    event.preventDefault();
    const { author, mappedName, mappedMail } = this.state;
    this.props.onSubmit(author, mappedName, mappedMail);
    this.setState({
      ...this.state,
      author: "",
      mappedName: "",
      mappedMail: "",
      valid: false
    });
  };
}

export default withTranslation("plugins")(AuthorMappingFormComponent);
