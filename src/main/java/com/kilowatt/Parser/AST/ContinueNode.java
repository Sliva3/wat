package com.kilowatt.Parser.AST;

import com.kilowatt.Compiler.WattCompiler;
import com.kilowatt.Errors.WattSemanticError;
import com.kilowatt.Semantic.SemanticAnalyzer;
import com.kilowatt.WattVM.Instructions.VmInstructionLoopEnd;
import com.kilowatt.WattVM.VmAddress;
import com.kilowatt.Lexer.Token;
import lombok.AllArgsConstructor;
import lombok.Getter;

/*
Следующая итерация цикла
 */
@Getter
@AllArgsConstructor
public class ContinueNode implements Node {
    private final Token location;

    @Override
    public void compile() {
        WattCompiler.code.visitInstruction(
            new VmInstructionLoopEnd(
                location.asAddress(),
                true
            )
        );
    }

    @Override
    public void analyze(SemanticAnalyzer analyzer) {
        if (!analyzer.hasTop(WhileNode.class) &&
                !analyzer.hasTop(ForNode.class)) {
            throw new WattSemanticError(
                location.asAddress(),
                "couldn't use continue outside a loop",
                "check your code."
            );
        }
    }
}
