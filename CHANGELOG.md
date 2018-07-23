## react-native-cross-settings changes

### 2018-03-19 v1.0.1
- Add missing google repository (for development).

### 2018-03-19 v1.0.0

- Flow types added (not sure if correctly).
- Better support for `long` & `double` values (the range of `long` is still limited by the RN Bridge).
- Remove react-native from peerDependencies, since this library must work in versions prior to 0.50.
- The default `buildToolsVersion` was changed 26.0.3 and `compileSdkVersion`/`targetSdkVersion` to 26.

### 2018-03-19 v0.2.0

- Changed minSdkVersion version to 16 - Thanks to @wayne1203
- The preferences file is openning only when used.
