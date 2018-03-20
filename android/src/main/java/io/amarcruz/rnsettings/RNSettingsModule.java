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
    private static final String CHANGED_EVENT = "settings_updated";

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
        final SharedPreferences prefs = getPreferences();
        final Map<String, Object> constants = new HashMap<>();
        final Map<String, ?> settings = prefs.getAll();

        constants.put("CHANGED_EVENT", CHANGED_EVENT);

        if (settings != null) {
            constants.put("settings", makeMap(settings));
        }

        return constants;
    }

    /*
        Supported mapped types: Boolean -> boolean, Float -> number, String -> string
     */
    private static Map<String, Object> makeMap(final Map<String, ?> src) {
        final Map<String, Object> map = new HashMap<>();

        for (Map.Entry<String, ?> entry : src.entrySet()) {
            final String key = entry.getKey();
            final Object value = entry.getValue();

            try {
                if (value != null && (
                    value instanceof Boolean ||
                    value instanceof Integer ||
                    value instanceof Long ||
                    value instanceof Float ||
                    value instanceof String
                )) {
                    map.put(key, value);
                }
            } catch (Exception e) {
                Log.e(TAG, "Reading setting " + key + " generates error.");
                e.printStackTrace();
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
                        // Can be int or double.
                        editor.putFloat(key, (float) map.getDouble(key));
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

    private OnSharedPreferenceChangeListener listener = new OnSharedPreferenceChangeListener() {

        public void onSharedPreferenceChanged(SharedPreferences pref, String key) {
            WritableMap map = Arguments.createMap();
            Boolean ok = true;
            RCTDeviceEventEmitter emitter = mReactContext.getJSModule(RCTDeviceEventEmitter.class);

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
                        Double num = (double) pref.getFloat(key, 0f);
                        map.putDouble(key, num);
                    } catch (ClassCastException e2) {
                        map.putBoolean(key, pref.getBoolean(key, false));
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
