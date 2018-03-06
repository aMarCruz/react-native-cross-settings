const Platform = require('react-native').Platform

Object.defineProperty(exports, "__esModule", { value: true })

exports.default = Platform.OS === 'android'
  ? require('./settings-android.js')
  : require('react-native').Settings
