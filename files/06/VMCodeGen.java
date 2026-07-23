/* To fix the static problem
 * 1. delete static from hashtable
 * 2. change push
 * 3. change pop
 */

import java.util.Hashtable;

public class VMCodeGen {
    static int EQ = 0;
    static int GT = 1;
    static int LT = 2;

    private Out output;
    private String filename;
    private String outputName = "sample.asm";
    private int eqCounter, gtCounter, ltCounter;
    private Hashtable<String, String> ramName;

    public VMCodeGen(String out) {
        outputName = out;
        output = new Out(outputName);
        ramName = new Hashtable<String, String>();
        ramName.put("local", "LCL\n");
        ramName.put("argument", "ARG");
        ramName.put("this", "THIS");
        ramName.put("that", "THAT");
        ramName.put("temp", "5");
    }

    public void setFileName(String fn) {
        filename = fn;
    }

    // Arithmetics

    public void writeArithmetic(String command) {
        if (command.matches("^add.*")) { writeAddCommand(); }
        else if (command.matches("^sub.*")) { writeSubCommand(); }
        else if (command.matches("^neg.*")) { writeNegCommand(); }
        else if (command.matches("^eq.*")) { writeEqCommand(); }
        else if (command.matches("^gt.*")) { writeGtCommand(); }
        else if (command.matches("^lt.*")) { writeLtCommand(); }
        else if (command.matches("^and.*")) { writeAndCommand(); }
        else if (command.matches("^or.*")) { writeOrCommand(); }
        else if (command.matches("^not.*")) { writeNotCommand(); }
        else { return; }
    }

    private void writeAddCommand() {
        writeBasicArithCommand("M=D+M");
    }

    private void writeSubCommand() {
        writeBasicArithCommand("M=M-D");
    }

    private void writeAndCommand() {
        writeBasicArithCommand("M=D&M");
    }

    private void writeOrCommand() {
        writeBasicArithCommand("M=D|M");
    }

    private void writeBasicArithCommand(String insert) {
        output.println("@SP");
        output.println("A=M-1");
        output.println("D=M");
        output.println("A=A-1");
        output.println(insert);
        output.println("D=A+1");
        output.println("@SP");
        output.println("M=D");
    }

    private void writeNotCommand() {
        output.println("@SP");
        output.println("A=M-1");
        output.println("M=!M");
    }

    private void writeNegCommand() {
        output.println("@SP");
        output.println("A=M-1");
        output.println("D=M");
        output.println("@0");
        output.println("D=A-D");
        output.println("@SP");
        output.println("A=M-1");
        output.println("M=D");
    }

    // Eq, Gt and Lt
    private void writeEqCommand() {
        writeLogicCommand(EQ);
    }

    private void writeGtCommand() {
        writeLogicCommand(GT);
    }

    private void writeLtCommand() {
        writeLogicCommand(LT);
    }

    private void writeLogicCommand(int logic) {
        String trueTag = "TRUE_", continueTag = "CON_", command = null;
        if (logic == EQ) {
            trueTag += ("EQ_" + Integer.toString(eqCounter));
            continueTag += ("EQ_" + Integer.toString(eqCounter++));
            command = "D;JEQ";
        } else if (logic == GT) {
            trueTag += ("GT_" + Integer.toString(gtCounter));
            continueTag += ("GT_" + Integer.toString(gtCounter++));
            command = "D;JGT";
        } else if (logic == LT) {
            trueTag += ("LT_" + Integer.toString(ltCounter));
            continueTag += ("LT_" + Integer.toString(ltCounter++));
            command = "D;JLT";
        }
        writeBasicLogicCommand(trueTag, continueTag, command);
    }

    void writeBasicLogicCommand(String trueTag, String continueTag,
                                String command) {
        output.println("@SP");
        output.println("A=M-1");
        output.println("D=M");
        output.println("A=A-1");
        output.println("D=M-D");
        output.println("@R13");
        output.println("M=D"); // R13 has the stack substraction
        output.println("@SP");
        output.println("D=M-1");
        output.println("@R14");
        output.println("M=D"); // R14 has the stack address
        output.println("@0");
        output.println("D=A");
        output.println("@R14");
        output.println("A=M-1");
        output.println("M=D");
        output.println("@R13");
        output.println("D=M");
        output.println("@" + trueTag);
        output.println(command);
        output.println("@" + continueTag);
        output.println("0;JMP");
        output.println("(" + trueTag + ")");
        output.println("@0");
        output.println("D=!A");
        output.println("@R14");
        output.println("A=M-1");
        output.println("M=D");
        output.println("(" + continueTag + ")");
        output.println("@R14");
        output.println("D=M");
        output.println("@SP");
        output.println("M=D");
    }

    // Push and Pop
    public void writePushPop(int command, String segment, int index) {
        if (segment.equals("pointer")) {
            if (index == 0) { segment = "THIS"; }
            if (index == 1) { segment = "THAT"; }
            writePointerPushPopCommand(command, segment);
            return;
        }

        if (ramName.containsKey(segment)) {
            segment = ramName.get(segment);
        }

        if (command == CT.C_PUSH) { writePushCommand(segment, index); }
        else if (command == CT.C_POP) { writePopCommand(segment, index); }
        else { return; }
    }

    void writePointerPushPopCommand(int command, String segment) {
        if (command == CT.C_PUSH) {
            output.printf("@%s\n", segment);
            output.println("D=M");
            output.println("@SP");
            output.println("A=M");
            output.println("M=D");
            output.println("D=A+1");
            output.println("@SP");
            output.println("M=D");
        } else if (command == CT.C_POP) {
            output.println("@SP");
            output.println("A=M-1");
            output.println("D=M");
            output.printf("@%s\n",segment);
            output.println("M=D");
            output.println("@SP");
            output.println("D=M-1");
            output.println("@SP");
            output.println("M=D");
        }
    }

    private void writePushCommand(String segment, int index) {
        if (segment.equals("constant")) {
            output.printf("@%d\n",index);
            output.println("D=A");
            output.println("@SP");
            output.println("A=M");
            output.println("M=D");
            output.println("D=A+1");
            output.println("@SP");
            output.println("M=D");
            return;
        }
        if (segment.equals("static")) {
            segment = filename + "." + Integer.toString(index);
        }
        output.printf("@%s\n", segment);
        if (segment.equals("5")) { 
            output.println("D=A");
        } else {
            output.println("D=M");
        }
        if (!segment.contains(filename)) {
            output.printf("@%d\n", index);
            output.println("A=D+A");
            output.println("D=M");
        }
        output.println("@SP");
        output.println("A=M");
        output.println("M=D");
        output.println("D=A+1");
        output.println("@SP");
        output.println("M=D");
    }

    private void writePopCommand(String segment, int index) {
        if (segment.equals("static")) {
            segment = filename + "." + Integer.toString(index);
        }
        output.printf("@%s\n", segment); // locate segment register A
        if (segment.equals("5") || segment.contains(filename)) {
            output.println("D=A");
        } else {
            output.println("D=M"); // read in segments address D
        }
        if (!segment.contains(filename)) {
            output.printf("@%d\n", index); // A has the index
            output.println("D=D+A"); // D has the right address
        }
        output.println("@R13"); // A has temp register
        output.println("M=D"); // temp = right address
        output.println("@SP"); // A has the stack pointer register
        output.println("A=M"); // A has the sp memory address
        output.println("A=A-1"); // A has the right address
        output.println("D=M"); // D has the data needed to be popped
        output.println("@R13"); // A has R13 register
        output.println("A=M"); // A has R13's stored memory address
        output.println("M=D"); // save the value to the right segment
        output.println("@SP"); // load SP again
        output.println("A=M"); // SP address again
        output.println("A=A-1"); // last item on the stack's address
        output.println("D=A"); // D has the SP address now
        output.println("@SP"); // a has the SP register
        output.println("M=D"); // sp register stores the right address
    }

    public static void main(String[] args) {
        String test = "Static.3";
        System.out.println(test.contains("Static"));
    }
}
