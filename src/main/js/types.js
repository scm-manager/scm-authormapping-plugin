// @flow
import type { Links } from "@scm-manager/ui-types"
export type Person = {
  name: string,
  mail: string
};

export type SingleMapping = { [string]: Person };

export type AuthorMappingConfiguration = {
  enableAutoMapping: boolean,
  manualMapping: SingleMapping,
  _links: Links
};

export type AuthorMapping = {
  author: string,
  mappedName: string,
  mappedMail: string
};
