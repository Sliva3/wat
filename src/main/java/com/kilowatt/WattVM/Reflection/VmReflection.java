package com.kilowatt.WattVM.Reflection;

import com.kilowatt.Compiler.Builtins.Libraries.Collections.WattList;
import com.kilowatt.Compiler.Builtins.Libraries.Std.Fs.FsPath;
import com.kilowatt.Errors.WattParseError;
import com.kilowatt.Errors.WattRuntimeError;
import com.kilowatt.WattVM.WattVM;
import com.kilowatt.WattVM.VmAddress;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.SneakyThrows;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;

/*
Рефлексия в ВМ
 */
@SuppressWarnings("rawtypes")
@Getter
@AllArgsConstructor
public class VmReflection {
    // вм
    private final WattVM vm;

    // создание экземпляра jvm-класса
    @SneakyThrows
    public Object reflect(String name, WattList args) {
        // адрес
        VmAddress address = vm.getCallsHistory().getLast().getAddress();
        // ищем класс
        try {
            // класс
            Class<?> clazz = VmJvmClasses.lookup(address, name);
            // колличество аргументов
            int argsAmount = args.size();
            // ищем конструктор
            Constructor constructor = findConstructor(
                address,
                clazz,
                argsAmount,
                args.getList().stream()
                    .map(Object::getClass)
                    .toArray(Class[]::new)
            );
            // создаём экземпляр
            return constructor.newInstance(args.getList().toArray());
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
            // ошибки
            Throwable cause = e instanceof InvocationTargetException ? e.getCause() : e;
            if (cause instanceof WattRuntimeError
                    || cause instanceof WattParseError) {
                throw cause;
            } else {
                throw new WattRuntimeError(address,
                        "error in jvm constructor: " + e.getMessage(), "check your code.");
            }
        }
    }

    // загрузка класса
    public void define(FsPath path, String className) {
        // загрузка
        try {
            // загружаем класс
            // чтение файла
            byte[] classData = Files.readAllBytes(path.getPath());
            // дефайн класса
            Class<?> clazz = VmJvmClasses.classLoader.define(className, classData);
            // дефайним класс
            VmJvmClasses.define(clazz);
        } catch (IOException e) {
            // адрес
            VmAddress address = vm.getCallsHistory().getLast().getAddress();
            // оишбка
            throw new WattRuntimeError(
                address,
                "couldn't define class " + className + ", " + e.getMessage(),
                "check your code."
            );
        }
    }

    /*
    Сравнение аргументов
     */
    private boolean checkParameters(Class<?>[] expected, Class<?>[] passed) {
        for (int i = 0; i < expected.length; i++) {
            if (!expected[i].isAssignableFrom(passed[i])) {
                return false;
            }
        }

        return true;
    }

    /*
    Поиск конструктора
     */
    public Constructor findConstructor(VmAddress address, Class<?> clazz,
           int argsAmount, Class<?>[] parameterTypes) {
        // ищем конструктор
        for (Constructor c : clazz.getConstructors()) {
            if (c.getParameterCount() == argsAmount &&
                checkParameters(c.getParameterTypes(), parameterTypes)
            ) {
                return c;
            }
        }
        // ищем сырой конструктор
        return findRawConstructor(address, clazz, argsAmount);
    }

    /*
    Поиск raw конструктора
     */
    public Constructor findRawConstructor(VmAddress address, Class<?> clazz,
           int argsAmount) {
        // ищем конструктор
        for (Constructor c : clazz.getConstructors()) {
            if (c.getParameterCount() == argsAmount) {
                return c;
            }
        }
        // в ином случае, ошибка
        throw new WattRuntimeError(
                address,
                "constructor with args ("
                        + argsAmount + ") for: " +
                        clazz.getSimpleName() + " is not found.",
                clazz.getSimpleName()
        );
    }

    /*
    Поиск метода
     */
    public Method findMethod(VmAddress address, Class<?> clazz, String name,
           int argsAmount, Class<?>[] parameterTypes) {
        // ищем конструктор
        for (Method m : clazz.getMethods()) {
            if (m.getName().equals(name) &&
                    m.getParameterCount() == argsAmount &&
                    checkParameters(m.getParameterTypes(), parameterTypes)
            ) {
                return m;
            }
        }
        // ищем сырой конструктор
        return findRawMethod(address, clazz, name, argsAmount);
    }

    /*
    Поиск raw конструктора
     */
    public Method findRawMethod(VmAddress address, Class<?> clazz,
           String name, int argsAmount) {
        // ищем конструктор
        for (Method m : clazz.getMethods()) {
            if (m.getName().equals(name) &&
                    m.getParameterCount() == argsAmount) {
                return m;
            }
        }
        // в ином случае, ошибка
        throw new WattRuntimeError(
            address,
            "fn " + name + "("
                    + argsAmount + ") in " +
                    clazz.getSimpleName() + " is not found.",
            "did you type wrong name?"
        );
    }

    /*
    Компиляция .java в .class
     */
    public String compile(FsPath in, FsPath out) {
        // поток для ошибок
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        // опции компиляции
        String[] options = new String[]{
            in.toString(),
            "-d",
            out.toString()
        };
        // компилятор
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        int result = compiler.run(null, null, stream, options);
        // возвращаем ошибку, если она есть
        if (result != 0) {
            return stream.toString();
        } else {
            return null;
        }
    }
}
