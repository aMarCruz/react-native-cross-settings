[![npm Version][npm-image]][npm-url]
[![License][license-image]][license-url]

# react-native-cross-settings

React Native `Settings` module for both Android & iOS.

\* For React Native 0.50+

## Installation

```bash
$ yarn add react-native-cross-settings
# ...or
$ npm install react-native-cross-settings -S
# then...
$ react-native link react-native-cross-settings
```

## Usage

```js
import Settings from 'react-native-cross-settings'

// Set a listener
const watchId = Settings.watchKeys('strvar', () => {
  console.log('strvar changed.')
})

console.log('restored setting:', Settings.get('strvar', '?'))
// => "?"

// Store a value
Settings.set('strvar', 'First setting')
console.log('new setting:', Settings.get('strvar', '?'))
// => "First setting"

// Remove value
Settings.set('strvar', null)
console.log('new setting:', Settings.get('strvar', '?'))
// => "?"

// Store another value, this will be preserved across sessions.
Settings.set('strvar', null)
console.log('new setting:', Settings.get('strvar', '?'))
// => "?"

Settings.clearWatch(watchId)
```



## API

See React Native [Settings](https://facebook.github.io/react-native/docs/settings.html) page, the API is the same.

### Methods

- **get()**<br>
  `static get(key: string) => number | string | value | null`

- **set()**<br>
  `static set(settings: { [key: string]: number | string | boolean | null } ) => void`

- **watchKeys()**<br>
  `static watchKeys(keys: string | string[], callback: () => any) => any`

- **clearWatch()**<br>
  `static clearWatch(watchId: any) => void`

### NOTE

In Android, valid value types to store are `boolean`, `string`, and `number`.

If you pass `null` as value, the key is removed.

## License

The [MIT License](LICENCE) (MIT)

[npm-image]:      https://img.shields.io/npm/v/react-native-cross-settings.svg
[npm-url]:        https://www.npmjs.com/package/react-native-cross-settings
[license-image]:  https://img.shields.io/npm/l/express.svg
[license-url]:    https://github.com/aMarCruz/react-native-cross-settings/blob/master/LICENSE
