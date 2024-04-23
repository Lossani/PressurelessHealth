package com.xempre.pressurelesshealth.models;

import android.content.Intent;

public class IntentExtra {
    String key;
    Object value;

    public IntentExtra(String key, Object value) {
        this.key = key;
        this.value = value;
    }

    public void setExtraToIntent(Intent intent) {
        if (key != null) {
            if (value instanceof String)
                intent.putExtra(key, (String) value);
            if (value instanceof Integer)
                intent.putExtra(key, (Integer) value);
            if (value instanceof Long)
                intent.putExtra(key, (Long) value);
            if (value instanceof Boolean)
                intent.putExtra(key, (Boolean) value);
        }
    }
}
