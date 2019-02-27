// @flow
import type { Links } from "@scm-manager/ui-types"
export type Person = {
  name: string,
  mail: string
};

export type Mapping = { [string]: Person };

export type AuthorMappingConfiguration = {
  enableAutoMapping: boolean,
  manualMapping: Mapping,
  _links: Links
};

export type AuthorMapping = {
  author: string,
  mappedName: string,
  mappedMail: string
};
