/*
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
