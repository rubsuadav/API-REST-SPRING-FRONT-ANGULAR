declare interface Env {
  // add the environment variables here using NG_APP_ prefix
}

declare interface ImportMeta {
  readonly env: Env;
}
