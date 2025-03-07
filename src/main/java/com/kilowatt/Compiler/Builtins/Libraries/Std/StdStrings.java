package com.kilowatt.Compiler.Builtins.Libraries.Std;

import com.kilowatt.WattVM.VmAddress;

/*
Стд -> Строки
 */
public class StdStrings {
    public int parse_int(VmAddress addr, String value) {
        return Integer.parseInt(value);
    }

    public float parse_float(VmAddress addr, String value) {
        return Float.parseFloat(value);
    }

    public long parse_long(VmAddress addr, String value) {
        return Long.parseLong(value);
    }
}
