/**
 * Copyright (c) 2015-present, Facebook, Inc.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 *
 * @providesModule Settings
 * @flow
 */
'use strict';

const DeviceEventEmitter = require('react-native').DeviceEventEmitter;
const RNSettings = require('react-native').NativeModules.RNSettings;

const invariant = require('invariant');

const subscriptions = []

const Settings = {
  _settings: RNSettings.settings || {},

  /**
   * @param {string} key -
   */
  get(key) {
    return this._settings[key];
  },

  /**
   * @param {object} settings -
   */
  set(settings) {
    this._settings = Object.assign(this._settings, settings);
    RNSettings.setValues(settings);
  },

  /**
   * Watcher
   * @param {atring | string[]} keys -
   * @param {Function} callback -
   * @returns {number}
   */
  watchKeys(keys, callback) {
    if (typeof keys === 'string') {
      keys = [keys];
    }

    invariant(
      Array.isArray(keys),
      'keys should be a string or array of strings'
    );

    const sid = subscriptions.length;
    subscriptions.push({ keys: keys, callback: callback });
    return sid;
  },

  /**
   * Unsubscribe.
   * @param {number} watchId -
   */
  clearWatch(watchId) {
    if (watchId < subscriptions.length) {
      subscriptions[watchId] = {keys: [], callback: null};
    }
  },

  /**
   * Android `SharedSettings` only emit event for changed values.
   * @param {object} body - { key: value }
   */
  _sendObservations(body) {
    Object.keys(body).forEach((key) => {
      this._settings[key] = body[key];

      subscriptions.forEach((sub) => {
        if (sub.keys.indexOf(key) !== -1 && sub.callback) {
          sub.callback();
        }
      });
    });
  },
};

DeviceEventEmitter.addListener(
  RNSettings.CHANGED_EVENT,
  Settings._sendObservations.bind(Settings)
);

module.exports = Settings;
