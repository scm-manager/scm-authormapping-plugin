// @flow

export type Person = {
    name: string,
    mail: string
}

export type AuthorMappingConfiguration = {
    enableAutoMapping: boolean,
    manualMapping: {[string]: Person}
}

