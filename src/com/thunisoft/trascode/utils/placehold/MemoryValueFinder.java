package com.thunisoft.trascode.utils.placehold;

import java.util.HashMap;
import java.util.Map;

public class MemoryValueFinder extends HashMap<String, String> implements IValueFinder {
    /**   */
    private static final long serialVersionUID = 1L;

    public MemoryValueFinder(Map<String, String> values) {
        super(values);
    }

    public MemoryValueFinder() {}


    @Override
    public String get(String key) {
        return super.get(key);
    }

}
