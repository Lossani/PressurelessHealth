package com.xempre.pressurelesshealth.api;

import java.util.Dictionary;
import java.util.Map;

public interface GoogleFitCallback {
    void fnCallback(Map<String, Number> measurements);
}
