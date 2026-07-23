public class VMParser {
    static String a_pattern = "^(add|sub|neg|eq|gt|lt|and|or|not).*";
    static String push_pattern = "^push.*";
    static String pop_pattern = "^pop.*";
    static String label_pattern = "^label.*";
    static String goto_pattern = "^goto.*";
    static String if_pattern = "^if.*";
    static String function_pattern = "^function.*";
    static String return_pattern = "^return.*";
    static String call_pattern = "^call.*";

    private In input;
    private String command;

    public VMParser(String filename) {
        input = new In(filename);
    }

    public boolean hasMoreCommands() {
        if (input.hasNextLine()) {
            return true;
        } else {
            input.close();
            return false;
        }
    }

    public void advance() {
        String currentLine;
        do {
            currentLine = input.readLine().trim();
        } while (currentLine.equals("") ||
                 currentLine.substring(0,2).equals("//"));
        String[] parts = currentLine.split("//");
        command = parts[0];

    }

    public int commandType() {
        if (command.matches(a_pattern)) { return CT.C_ARITHMETIC; }
        else if (command.matches(push_pattern)) { return CT.C_PUSH; }
        else if (command.matches(pop_pattern)) { return CT.C_POP; }
        else if (command.matches(label_pattern)) { return CT.C_LABEL; }
        else if (command.matches(goto_pattern)) { return CT.C_GOTO; }
        else if (command.matches(if_pattern)) { return CT.C_IF; }
        else if (command.matches(function_pattern)) { return CT.C_FUNCTION; }
        else if (command.matches(return_pattern)) { return CT.C_RETURN; }
        else if (command.matches(call_pattern)) { return CT.C_CALL; }
        else { return CT.C_ERROR; }
    }

    public String arg1() {
        String result = null;
        int type = commandType();
        if (type == CT.C_ARITHMETIC) { result = command; }
        else if (type != CT.C_RETURN) { result = command.split(" ")[1]; }

        return result;
    }

    public int arg2() {
        int type = commandType();
        String result = null;
        if (type == CT.C_PUSH || type == CT.C_POP
            || type == CT.C_FUNCTION || type == CT.C_CALL) {
            result = command.split(" ")[2];
        }
        if (!isNumeric(result)) { return -1; }

        int res = Integer.parseInt(result);
        return res;
    }

    private static boolean isNumeric(String str) {
        if (str.length() == 0) { return false; }

        for (char c : str.toCharArray())
        {
            if (!Character.isDigit(c)) return false;
        }
        return true;
    }

    public static void main(String[] args) {
        String m_pattern = "^push.*";
        String s = "push   ";
        System.out.println(s.matches(m_pattern));
    }
}
