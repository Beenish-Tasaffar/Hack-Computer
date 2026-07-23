
import java.io.File;

public class VMTranslator {
    public static void writeAssemblyCode(String file, VMCodeGen codeGen) {
        VMParser parser = new VMParser(file);
        String _file = file.split("[.]")[0];
        codeGen.setFileName(_file);
        while (parser.hasMoreCommands()) {
            parser.advance();
            int currentCT = parser.commandType();
            if (currentCT == CT.C_ARITHMETIC) {
                String command = parser.arg1();
                codeGen.writeArithmetic(command);
            } else if (currentCT == CT.C_PUSH || currentCT == CT.C_POP) {
                String command = parser.arg1();
                int index = parser.arg2();
                codeGen.writePushPop(currentCT, command, index);
            }
        }
    }

    public static void main(String[] args) {
        String filename = args[0];
        File file = new File(filename);
        String[] children = null;
        String outputName = "combine.asm";

        if (file.isFile()) {
            if (filename.contains(".vm")) {
                children = new String[1];
                children[0] = filename;
                outputName = filename.split("[.]")[0] + ".asm";
            } else {
                System.out.println("Not a vm file");
                return;
            }
        }
        else if (file.isDirectory()) {
            children = file.list();
        } else {
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

        VMCodeGen codeGen = new VMCodeGen(outputName);
        for (String child : children) {
            if (child.contains(".vm")) {
                System.out.printf("Writing %s\n", child);
                writeAssemblyCode(child, codeGen);
            }
        }
    }
}
