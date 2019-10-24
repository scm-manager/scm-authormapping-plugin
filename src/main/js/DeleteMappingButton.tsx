import React from "react";
import { DeleteButton } from "@scm-manager/ui-components";
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
      <>
        <DeleteButton label={this.props.label} action={this.onClick} />
      </>
    );
  }

  onClick = (event: Event) => {
    event.preventDefault();
    this.props.onDelete(this.props.mapping);
  };
}

export default DeleteMappingButton;
