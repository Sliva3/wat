package com.kilowatt.Errors;

import lombok.AllArgsConstructor;
import lombok.Getter;

/*
Ошбика по семантике
 */
@Getter
@AllArgsConstructor
public class WattSemanticError extends WattError {
    private final int line;
    private final String filename;
    private final String message;
    private final String hint;

    @Override
    public void panic() {
        System.out.print(WattColors.ANSI_RED);
        System.out.println("╭ ⚡ semantic error.");
        System.out.println("│ err: " + this.message);
        System.out.println("│ at: " + filename + "::" + line);
        System.out.println("│ 💡 " + hint);
        System.out.println("╰");
        System.out.print(WattColors.ANSI_RESET);
        System.exit(errorCode());
    }

    @Override
    public int errorCode() {
        return 5;
    }

    @Override
    public String message() {
        return message;
    }

    @Override
    public int address() {
        return line;
    }
}
