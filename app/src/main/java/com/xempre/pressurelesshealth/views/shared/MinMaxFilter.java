package com.xempre.pressurelesshealth.views.shared;

import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;

/**
 * Created by npatel on 4/5/2016.
 */
public class MinMaxFilter implements InputFilter {

    private Number mIntMin, mIntMax;

    public MinMaxFilter(Number minValue, Number maxValue) {
        this.mIntMin = minValue;
        this.mIntMax = maxValue;
    }

    public MinMaxFilter(String minValue, String maxValue) {
        this.mIntMin = Float.valueOf(minValue);
        this.mIntMax = Float.valueOf(maxValue);
    }

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        try {
            Number input = Float.valueOf(dest.toString() + source.toString());
            if (isInRange(mIntMin, mIntMax, input))
                return null;
        } catch (NumberFormatException nfe) {
            Log.d("NBUMBER", nfe.toString());
        }
        return "";
    }

    private boolean isInRange(Number a, Number b, Number c) {
        float value1 = a.floatValue();
        float value2 = b.floatValue();
        float value3 = c.floatValue();
        return value2 > value1 ? value3 >= value1 && value3 <= value2 : value3 >= value2 && value3 <= value1;
    }
}
