package com.kilowatt.WattVM;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Адрес ВМ
 */
@Getter
@AllArgsConstructor
public class VmAddress {
    private final String fileName;
    private final int line;
}
