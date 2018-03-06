declare module "react-native-cross-settings" {

  export type SettingsTypes = string | number | boolean | null;

  export interface SettingsStatic {
    get(key: string): SettingsTypes;
    set(settings: { [key: string]: SettingsTypes }): void;
    watchKeys(keys: string | string[], callback: () => any): any
    clearWatch(watchId: any): void;
  }

  const Settings: SettingsStatic;
  export default Settings;

}
