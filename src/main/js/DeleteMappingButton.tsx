import React from "react";
import { Icon } from "@scm-manager/ui-components";
import { AuthorMapping } from "./types";

type Props = {
  mapping: AuthorMapping;
  onDelete: (p: AuthorMapping) => void;
  label: string;
};

type State = {};

class DeleteMappingButton extends React.Component<Props, State> {
  render() {
    return (
      <a className="level-item" onClick={this.onClick}>
        <span className="icon is-small">
          <Icon name="trash" color="inherit" title={this.props.label} />
        </span>
      </a>
    );
  }

  onClick = (event: Event) => {
    event.preventDefault();
    this.props.onDelete(this.props.mapping);
  };
}

export default DeleteMappingButton;
