// @flow
import React from 'react';
import { Configuration, Subtitle } from "@scm-manager/ui-components";
import AuthorMappingConfigurationForm from "./AuthorMappingConfigurationForm";
import { translate } from "react-i18next"

type Props = {
    link: string,
    t: string => string
}

class ConfigurationContainer extends React.Component<Props> {
    constructor(props: Props) {
        super(props);
    }

    render() {
        const { link, t } = this.props;

        return (
            <div>
                <Subtitle subtitle={t("scm-authormapping-plugin.config.title")} />
                <Configuration
                    link={link}
                    render={props => <AuthorMappingConfigurationForm {...props} />}
                />
            </div>
        );
    }
}

export default translate("plugins")(ConfigurationContainer);