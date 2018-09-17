/**
 * Copyright (c) 2015-present, Facebook, Inc.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 *
 */
'use strict';

const RCTDeviceEventEmitter = require('react-native').DeviceEventEmitter;
const RCTSettingsManager = require('react-native').NativeModules.RNSettings;

const invariant = require('invariant');

const subscriptions = [];

const Settings = {
  _settings: RCTSettingsManager.settings,

  get(key) {
    return this._settings[key];
  },

  set(settings) {
    this._settings = Object.assign(this._settings, settings);
    RCTSettingsManager.setValues(settings);
  },

  watchKeys(keys, callback) {
    if (typeof keys === 'string') {
      keys = [keys];
    }

    invariant(
      Array.isArray(keys),
      'keys should be a string or array of strings'
    );

    const sid = subscriptions.length;
    subscriptions.push({keys: keys, callback: callback});
    return sid;
  },

  clearWatch(watchId) {
    if (watchId < subscriptions.length) {
      subscriptions[watchId] = {keys: [], callback: null};
    }
  },

  _sendObservations(body) {
    Object.keys(body).forEach((key) => {
      subscriptions.forEach((sub) => {
        if (sub.callback && sub.keys.indexOf(key) !== -1) {
          sub.callback();
        }
      });
    });
  },
};

RCTDeviceEventEmitter.addListener(
  'settingsUpdated',
  Settings._sendObservations.bind(Settings)
);

module.exports = Settings;
