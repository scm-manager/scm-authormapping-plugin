// @flow

import { ConfigurationBinder as cfgBinder } from "@scm-manager/ui-components";
import ConfigurationContainer from "./ConfigurationContainer";

cfgBinder.bindRepositorySetting(
  "/authormapping",
  "scm-authormapping-plugin.nav-link",
  "self", // TODO: Add correct link
  ConfigurationContainer
);
