# react-native-cross-settings

[![npm Version][npm-image]][npm-url]
[![License][license-image]][license-url]

React Native `Settings` module for both Android & iOS.

If this library has helped you, don't forget to give it a star :star2:

## IMPORTANT

v2.0 works with Gradle 4.4, the default in React Native 0.57. If you are using Gradle 3.x please use react-native-cross-settings 1.0.2


## Setup

```bash
yarn add react-native-cross-settings
react-native link react-native-cross-settings
```

## Usage

```js
import Settings from 'react-native-cross-settings';

// Set a listener. It will be called for *each* value that has changed.
const watchId = Settings.watchKeys('strvar', () => {
  console.log('strvar changed.');
});

// If you never saved a value in "strvar", this is undefined.
console.log('restored setting:', Settings.get('strvar'));
// => undefined

// Store a value (only string, number, or boolean)
Settings.set({ strvar: 'First setting' });
console.log('new setting:', Settings.get('strvar'));
// => "First setting"

// You cann't remove a value, but you can set it to null.
// Next time your App start, the value will be undefined.
Settings.set({ strvar: null });
console.log('new setting:', Settings.get('strvar'));
// => null

// Store a new value, this will be preserved across sessions.
Settings.set({ strvar: 'final value' });

// => Don't forget to remove the listener
Settings.clearWatch(watchId)
```

## API

See React Native [Settings](https://facebook.github.io/react-native/docs/settings.html) page, the API is the same.

### Methods

- **get()**

  ```typescript
  static get(key: string) => number | string | value | null
  ```

- **set()**

  ```typescript
  static set(settings: { [key: string]: number | string | boolean | null } ) => void
  ```

- **watchKeys()**

  ```typescript
  static watchKeys(keys: string | string[], callback: () => any) => number
  ```

- **clearWatch()**

  ```typescript
  static clearWatch(watchId: number) => void
  ```

### NOTE

In Android, valid value types to store are `boolean`, `string`, and `number`.

If you pass `null` as value, the key will be removed in the next session.

If you want to save other types use the appropriate conversion:

```js
// Storing a Date object:
Settings.set({ myDate: new Date().toJSON() })
// Retrieve
const myDate = new Date(Settings.get('myDate'))

// Storing an array
Settings.set({ myArray: JSON.stringify([1,2,3]) })
// Retrieve
const myArray = JSON.parse(Settings.get('myArray') || '[]')
```


## Support my Work

I'm a full-stack developer with more than 20 year of experience and I try to share most of my work for free and help others, but this takes a significant amount of time and effort so, if you like my work, please consider...

[![Buy me a Coffee][bmc-image]][bmc-url]

Feedback, PRs stars, and smiles are also welcome :)

Thanks for your support!


## License

The [MIT License](LICENSE) (MIT)

[npm-image]:      https://img.shields.io/npm/v/react-native-cross-settings.svg
[npm-url]:        https://www.npmjs.com/package/react-native-cross-settings
[license-image]:  https://img.shields.io/npm/l/express.svg
[license-url]:    https://github.com/aMarCruz/react-native-cross-settings/blob/master/LICENSE
[bmc-image]:      https://www.buymeacoffee.com/assets/img/custom_images/orange_img.png
[bmc-url]:        https://www.buymeacoffee.com/aMarCruz
