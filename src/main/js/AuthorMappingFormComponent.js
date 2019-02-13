// @flow
import React from "react";
import {
  InputField,
  SubmitButton,
  validation
} from "@scm-manager/ui-components";
import { translate } from "react-i18next";

type Props = {
  author?: string,
  mappedName?: string,
  mappedMail?: string,
  onSubmit: (author: string, mappedName: string, mappedMail: string) => void,

  t: string => string
};
type State = {
  author: string,
  mappedName: string,
  mappedMail: string,
  valid: boolean
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
          />
          <InputField
            onChange={this.onChange}
            validationError={false}
            label={t("scm-authormapping-plugin.config.form.mappedName")}
            value={this.state.mappedName}
            name="mappedName"
          />
          <InputField
            onChange={this.onChange}
            validationError={
              this.state.mappedMail !== "" &&
              !validation.isMailValid(this.state.mappedMail)
            }
            type="email"
            label={t("scm-authormapping-plugin.config.form.mappedMail")}
            value={this.state.mappedMail}
            name="mappedMail"
          />
          <SubmitButton
            label={t("scm-authormapping-plugin.config.form.add")}
            action={this.onAdd}
            disabled={!this.state.valid}
          />
        </form>
    );
  }

  validateState = () => {
    const { author, mappedName, mappedMail } = this.state;
    const valid =
      !!author && !!mappedName && !!mappedMail && validation.isMailValid(mappedMail);

    this.setState({ ...this.state, valid });
  };

  onChange = (value: string, name: string) => {
    this.setState({ ...this.state, [name]: value }, () => this.validateState());
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

export default translate("plugins")(AuthorMappingFormComponent);
