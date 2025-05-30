package com.kilowatt.WattVM.Instructions;

import com.kilowatt.Errors.WattRuntimeError;
import com.kilowatt.WattVM.*;
import com.kilowatt.WattVM.Codegen.VmCodeDumper;
import com.kilowatt.WattVM.Entities.VmInstance;
import com.kilowatt.WattVM.Entities.VmNull;
import com.kilowatt.WattVM.Entities.VmType;
import com.kilowatt.WattVM.Entities.VmUnit;
import com.kilowatt.WattVM.Entities.VmTable;
import lombok.Getter;

/*
Кодишен VM
 */
@SuppressWarnings({"ConstantValue", "ClassCanBeRecord"})
@Getter
public class VmInstructionCondOp implements VmInstruction {
    // адресс
    private final VmAddress address;
    // оператор
    private final String operator;

    public VmInstructionCondOp(VmAddress address, String operator) {
        this.address = address;
        this.operator = operator;
    }

    @Override
    public void run(WattVM vm, VmTable<String, Object> table) {
        Object r = vm.pop(address);
        Object l = vm.pop(address);
        switch (operator) {
            case "==" -> vm.push(equal(address, l, r));
            case "!=" -> vm.push(!equal(address, l, r));
            case "<" -> {
                if (l instanceof Number lNumber && r instanceof Number rNumber) {
                    vm.push(isLessThan(lNumber, rNumber));
                } else {
                    throw new WattRuntimeError(address,
                        "not a number: " + (l instanceof Number ? r : l),
                        "check types.");
                }
            }
            case ">" -> {
                if (l instanceof Number lNumber && r instanceof Number rNumber) {
                    vm.push(isGreaterThan(lNumber, rNumber));
                } else {
                    throw new WattRuntimeError(address,
                        "not a number: " + (l instanceof Number ? r : l),
                        "check types.");
                }
            }
            case "<=" -> {
                if (l instanceof Number lNumber && r instanceof Number rNumber) {
                    vm.push(isLessOrEqual(lNumber, rNumber));
                } else {
                    throw new WattRuntimeError(address,
                        "not a number: " + (l instanceof Number ? r : l),
                        "check types.");
                }
            }
            case ">=" -> {
                if (l instanceof Number lNumber && r instanceof Number rNumber) {
                    vm.push(isGreaterOrEqual(lNumber, rNumber));
                } else {
                    throw new WattRuntimeError(address,
                        "not a number: " + (l instanceof Number ? r : l),
                        "check types.");
                }
            }
            default -> throw new WattRuntimeError(address,
                "invalid cond. op: " + operator,
                "available op-s: ==,!=,>,>=,<=,<");
        }
    }

    @Override
    public void print(int indent) {
        VmCodeDumper.dumpLine(indent, "COND_OP("+operator+")");
    }

    // равны ли два объекта
    public static boolean equal(VmAddress address, Object l, Object r) {
        if (l instanceof String left && r instanceof String right) {
            return left.equals(right);
        }
        else if (l.getClass() == VmNull.class && r.getClass() != VmNull.class) {
            return false;
        }
        else if (l.getClass() != VmNull.class && r.getClass() == VmNull.class) {
            return false;
        }
        else if (l.getClass() == VmNull.class && r.getClass() == VmNull.class) {
            return true;
        }
        else if (l instanceof VmType left && r instanceof VmType right) {
            return left == right;
        }
        else if (l instanceof VmInstance left && r instanceof VmInstance right) {
            return left == right;
        }
        else if (l instanceof VmUnit left && r instanceof VmUnit right) {
            return left == right;
        }
        else if (l instanceof Boolean left && r instanceof Boolean right) {
            return left == right;
        }
        else if (l instanceof Number a && r instanceof Number b) {
            return compare(address, a, b) == 0;
        }
        else {
            return l == r;
        }
    }

    private static int compare(VmAddress address, Number a, Number b) {
        if (a instanceof Double || b instanceof Double) {
            return Double.compare(a.doubleValue(), b.doubleValue());
        } else if (a instanceof Float || b instanceof Float) {
            return Float.compare(a.floatValue(), b.floatValue());
        } else if (a instanceof Long || b instanceof Long) {
            return Long.compare(a.longValue(), b.longValue());
        } else if (a instanceof Integer || b instanceof Integer) {
            return Integer.compare(a.intValue(), b.intValue());
        } else {
            throw new WattRuntimeError(address,
                    "not a number: " + (a != null ? a : b),
                    "check types.");
        }
    }

    private boolean isGreaterThan(Number a, Number b) {
        return compare(address, a, b) > 0;
    }

    private boolean isGreaterOrEqual(Number a, Number b) {
        return compare(address, a, b) >= 0;
    }

    private boolean isLessOrEqual(Number a, Number b) {
        return compare(address, a, b) <= 0;
    }

    private boolean isLessThan(Number a, Number b) {
        return compare(address, a, b) < 0;
    }

    @Override
    public String toString() {
        return "CONDITIONAL_OP(" + operator + ")";
    }
}
