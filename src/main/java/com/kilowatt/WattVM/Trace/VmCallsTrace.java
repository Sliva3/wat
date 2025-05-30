package com.kilowatt.WattVM.Trace;

import com.kilowatt.WattVM.Reflection.VmCallInfo;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/*
История вызовов, aka трэйс стэка вызовов
 */
@Getter
public class VmCallsTrace {
    // история
    private final List<VmCallInfo> callsHistory = new ArrayList<>();
    // максимальный размер истории
    private final int maxSize = 6;

    // добавление элемента
    public void add(VmCallInfo info) {
        // если переполнение - удаляем первый элемент
        if (callsHistory.size() + 1 > maxSize) {
            callsHistory.removeFirst();
        }
        // добавляем в стэк вызовов
        callsHistory.add(info);
    }
}

