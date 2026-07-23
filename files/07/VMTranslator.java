import java.io.File;

public class VMTranslator {
    public static void writeAssemblyCode(String file, VMCodeGen codeGen) {
        VMParser parser = new VMParser(file);
        String _file = file.split("[.]")[0];
        codeGen.setFileName(_file);
        while (parser.hasMoreCommands()) {
            parser.advance();
            //codeGen.writeDebug();
            int currentCT = parser.commandType();
            if (currentCT == CT.C_ARITHMETIC) {
                String command = parser.arg1();
                codeGen.writeArithmetic(command);
            } else if (currentCT == CT.C_PUSH || currentCT == CT.C_POP) {
                String command = parser.arg1();
                int index = parser.arg2();
                codeGen.writePushPop(currentCT, command, index);
            } else if (currentCT == CT.C_FUNCTION) {
                String fName = parser.arg1();
                int numArgs = parser.arg2();
                codeGen.writeFunction(fName, numArgs);
            } else if (currentCT == CT.C_RETURN) {
                codeGen.writeReturn();
            } else if (currentCT == CT.C_CALL) {
                String fName = parser.arg1();
                int numArgs = parser.arg2();
                codeGen.writeCall(fName, numArgs);
            } else if (currentCT == CT.C_LABEL) {
                String label = parser.arg1();
                codeGen.writeLabel(label);
            } else if (currentCT == CT.C_GOTO) {
                String label = parser.arg1();
                codeGen.writeGoto(label);
            } else if (currentCT == CT.C_IF) {
                String label = parser.arg1();
                codeGen.writeIf(label);
            } else {
                System.out.println("ill-formatted vm code");
            }
        }
    }

    public static void main(String[] args) {
        String a = "sys.vm";
        System.out.println(a.matches("^.*vm$"));
        String filename = args[0];
        File file = new File(filename);
        String[] children = null;
        String outputName = null; 

        if (file.isFile()) {
            if (filename.contains(".vm")) {
                children = new String[1];
                children[0] = filename;
                outputName = filename.split("[.]")[0] + ".asm";
            } else {
                System.out.println("Not a vm file");
                return;
            }
        } else if (file.isDirectory()) {
            String[] outputNames = file.getAbsolutePath().split("/");
            outputName = outputNames[outputNames.length - 2] + ".asm";
            children = file.list();
        } else {
            System.out.println(outputName);
            outputName = null;
            System.out.println("Ilegal name");
            return;
        }

        boolean containsVM = false;
        for (String child : children) {
            if (child.contains(".vm")) {
                containsVM = true;
                break;
            }
        }

        if (!containsVM) {
            System.out.println("No vm file in this directory");
            return;
        }
        
        int firstfile = -1;
        
        for (int i = 0; i < children.length; i++) {
            String fn = children[i];
            if (fn.equals("Sys.vm")) { firstfile = i; }
        }        
        

        if (firstfile != -1) {
            String temp = children[0];
            children[0] = children[firstfile];
            children[firstfile] = temp;
        } 

        VMCodeGen codeGen = new VMCodeGen(outputName);
        for (String child : children) {
            if (child.matches("^.*vm$")) {
                System.out.printf("Writing %s\n", child);
                writeAssemblyCode(child, codeGen);
            }
        }
    }
}
