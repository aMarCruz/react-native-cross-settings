package io.amarcruz.rnsettings;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.util.Log;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableMapKeySetIterator;
import com.facebook.react.bridge.ReadableType;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule.RCTDeviceEventEmitter;

import java.util.HashMap;
import java.util.Map;

class RNSettingsModule extends ReactContextBaseJavaModule {

    private static final String TAG = "RNSettings";
    private static final String PREFS_NAME = "RNSettrinsPrefsFile";
    private static final String CHANGED_EVENT = "settingsUpdated";

    private ReactApplicationContext mReactContext;

    RNSettingsModule(ReactApplicationContext reactContext) {
        super(reactContext);

        mReactContext = reactContext;
    }

    @Override
    public void initialize() {
        final SharedPreferences prefs = getPreferences();
        prefs.registerOnSharedPreferenceChangeListener(listener);
    }

    @Override
    public void onCatalystInstanceDestroy() {
        final SharedPreferences prefs = getPreferences();
        prefs.unregisterOnSharedPreferenceChangeListener(listener);
    }

    @Override
    public String getName() {
        return TAG;
    }

    @Override
    public Map<String, Object> getConstants() {
        final Map<String, Object> constants = new HashMap<>();
        final SharedPreferences prefs = getPreferences();

        constants.put("settings", makeMap(prefs.getAll()));

        return constants;
    }

    /*
        Supported mapped types: Boolean -> boolean, Float -> number, String -> string
     */
    private static Map<String, Object> makeMap(final Map<String, ?> src) {
        final Map<String, Object> map = new HashMap<>();

        if (src != null) {
            for (Map.Entry<String, ?> entry : src.entrySet()) {
                final String key = entry.getKey();
                final Object value = entry.getValue();

                if (value == null) {
                    continue;
                }

                try {
                    if (value instanceof Long) {
                        map.put(key, Double.longBitsToDouble((long) value));
                    } else if ((
                        value instanceof Boolean ||
                        value instanceof Integer ||
                        value instanceof Float || /* keep this for compatibility */
                        value instanceof String
                    )) {
                        map.put(key, value);
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Reading setting " + key + " generates error.");
                    e.printStackTrace();
                }
            }
        }

        return map;
    }

    /*
        Stores the values mapped in `map` to Preferences.
     */
    @ReactMethod
    public void setValues(final ReadableMap map) {

        if (map == null) {
            return;
        }

        final ReadableMapKeySetIterator iterator = map.keySetIterator();
        final SharedPreferences prefs = getPreferences();
        final SharedPreferences.Editor editor = prefs.edit();

        try {
            while (iterator.hasNextKey()) {
                String key = iterator.nextKey();
                ReadableType type = map.getType(key);

                switch (type) {
                    case Null:
                        editor.remove(key);
                        break;
                    case Boolean:
                        editor.putBoolean(key, map.getBoolean(key));
                        break;
                    case Number:
                        storeNumber(editor, key, map.getDouble(key));
                        break;
                    case String:
                        editor.putString(key, map.getString(key));
                        break;
                }
            }

            editor.apply();

        } catch (Exception e) {
            Log.e(TAG, "Converting settings generates error.");
            e.printStackTrace();
        }
    }

    /*
      RN ReadableMap has no a `getLong` method, so type Long is used with `doubleToRawLongBits`
      for storing Double values, as SharedPreferences.Editor has no `putDouble`.
    */
    private void storeNumber(final SharedPreferences.Editor editor, final String key, final double number) {
        // Can be long, float, or double.
        if (number == (int) number || Double.isNaN(number)) {
            editor.putInt(key, (int) number);
        } else {
            editor.putLong(key, Double.doubleToRawLongBits(number));
        }
    }

    private OnSharedPreferenceChangeListener listener = new OnSharedPreferenceChangeListener() {

        public void onSharedPreferenceChanged(SharedPreferences pref, String key) {
            WritableMap map = Arguments.createMap();
            RCTDeviceEventEmitter emitter = mReactContext.getJSModule(RCTDeviceEventEmitter.class);
            boolean ok = true;

            if (emitter == null) {
                Log.d(TAG, "Error: Cannot get RCTDeviceEventEmitter instance.");
                return;
            }

            // Sorry, there's only 3 valid types, use brute force
            if (pref.contains(key)) {
                try {
                    String str = pref.getString(key, "");
                    map.putString(key, str);
                } catch (ClassCastException e1) {
                    try {
                        int num = pref.getInt(key, 0);
                        map.putInt(key, num);
                    } catch (ClassCastException e2) {
                        try {
                            map.putBoolean(key, pref.getBoolean(key, false));
                        } catch (ClassCastException e3) {
                            long bits = pref.getLong(key, 0L);
                            map.putDouble(key, Double.longBitsToDouble(bits));
                        }
                    }
                } catch (Exception e) {
                    ok = false;
                    e.printStackTrace();
                }
            } else {
                map.putNull(key);
            }


            if (ok) {
                emitter.emit(CHANGED_EVENT, map);
            } else {
                Log.d(TAG, "Cannot read preference " + key);
            }
        }
    };

    private SharedPreferences getPreferences() {
        return mReactContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }
}
