package com.kilowatt.WattVM.Entities;

import com.kilowatt.WattVM.WattVM;
import com.kilowatt.WattVM.VmAddress;
import com.kilowatt.WattVM.VmFrame;
import lombok.Getter;

/*
Инстанс класса ВМ
 */
@Getter
public class VmInstance implements VmFunctionOwner {
    // скоуп
    private final VmFrame<String, Object> scope = new VmFrame<>();
    // класс
    private final VmType type;
    // адрес
    private final VmAddress addr;

    // конструктор
    public VmInstance(WattVM vm, VmType type, VmAddress addr)  {
        this.type = type;
        this.addr = addr;
        for (int i = type.getConstructor().size()-1; i >= 0; i--) {
            Object arg = vm.pop();
            scope.define(addr, type.getConstructor().get(i), arg);
        }
        scope.setRoot(vm.getGlobals());
        type.getBody().run(vm, scope);
        if (scope.has("init")) {
            call(addr, "init", vm, false);
        }
    }

    /**
     * Вызов функции объекта
     * @param name - имя функции
     * @param vm - ВМ
     */
    public void call(VmAddress inAddr, String name, WattVM vm, boolean shouldPushResult)  {
        // копируем и вызываем функцию
        VmFunction func = (VmFunction) getScope().lookup(inAddr, name);
        func.setDefinedFor(this);
        func.exec(vm, shouldPushResult);
    }

    // в строку

    @Override
    public String toString() {
        return "VmInstance(" +
                "scope=" + scope +
                ", clazz=" + type +
                ", addr=" + addr +
                ')';
    }

    // получение локального скоупа
    @Override
    public VmFrame<String, Object> getLocalScope() {
        return scope;
    }
}
