# File name: files/05/Assembler.py

#!/usr/bin/env python3

from sys import argv
import os

# Messages
USAGE_MSG = "Usage: {0} <filename.asm>"
BAD_INPUT = "Invalid program. Terminating compilation"
BAD_FILE = "Problem reading from file or writing to file"

# Exit codes
CODE_SUCCESS = 0
CODE_FAILURE = -1

HACK_EXTENSION = ".hack"
ASM_EXTENSION = ".asm"

# Hack prefixes etc
A_COMMAND_PREFIX = "@"
A_COMMAND_HACK_PREFIX = "0"
C_COMMAND_HACK_PREFIX = "1"
C_COMMAND_JMP_DELIM = ";"
C_COMMAND_EQUAL_DELIM = "="
L_COMMAND_OPEN_PARAN = "("
COMMENT_PREFIX = "//"
LINUX_NEWLINE = "\n"
WINDOWS_NEWLINE = "\r\n"
DEFAULT_JUMP = "000"
DEFAULT_DEST = "000"

# Hack bit commands
JUMP_DICT = {"JGT":"001", "JEQ":"010" , "JGE":"011","JLT":"100","JLE":"110","JNE":"101","JMP":"111"}
DEST_DICT = {"M":"001", "D":"010", "MD":"011","A":"100","AD":"110","AM":"101","AMD":"111"}
COMP_DICT = {"0":"110101010", "1":"110111111", "-1":"110111010", "D":"110001100", "A":"110110000", "!D":"110001101",
             "!A":"110001111", "-D":"110001111", "-A":"110110011","D+1":"110011111","A+1":"110110111","D-1":"110001110",
             "A-1":"110110010","D+A":"110000010","D-A":"110010011","A-D":"110000111","D&A":"110000000","D|A":"110010101",
             "M":"111110000","!M":"111110001","-M":"111110011","M+1":"111110111","M-1":"111110010","D+M":"111000010",
             "D-M":"111010011","M-D":"111000111","D&M":"111000000","D|M":"111010101",
             "D<<":"010110000","A<<":"010100000","M<<":"011100000","D>>":"010010000","A>>":"010000000","M>>":"011000000"}

# Program predefined symbols
DEFAULT_SYMBOL_DICT = {"SP":"0","LCL":"1","ARG":"2","THIS":"3","THAT":"4",
                        "R0":"0","R1":"1","R2":"2","R3":"3","R4":"4","R5":"5","R6":"6","R7":"7","R8":"8","R9":"9","R10":"10",
                        "R11":"11","R12":"12","R13":"13","R14":"14","R15":"15",
                        "SCREEN":"16384","KBD":"24576"}


class Parser(object):
    """
    This class is built in order to perform the whole assembly process.
    Contains functions that handle the different textual aspects of the asm code,
    and converts them into the correct Hack bit codes.
    """
    
    def __init__(self):
        # Current program's symbol hash table
        self.symbols = DEFAULT_SYMBOL_DICT.copy()
        # Next address to allocate variable symbols
        self.next_addr = 16

    def parse(self, text):        
        """
        Main function of the parser.
        Receives raw text of an asm program, and returns its Hack binary code.
        """
        
        # Final program string
        result = ""
        
        # Preliminary handlinf of input lines
        lines = text.splitlines()
        lines = self.clean_lines(lines)
        
        # First pass - parse label symbols
        self.parse_labels(lines)
        
        # Second pass - parse the program
        for line in lines:
            line_binary = self.parse_line(line)
            if line_binary:
                result += line_binary + "\n"
        
        # Return the ready program code
        return result

    def clean_lines(self, lines):
        """
        Removes empty lines, comments and spaces.
        Returns a new list of lines, which are the actual program lines.
        """
        new_lines = []
        for line in lines:
            # Remove comments
            split_line = line.split(COMMENT_PREFIX)
            # Remove spaces
            clean_line = split_line[0].replace(' ','')
            if clean_line:
                # Add line only if it is an actual program line.
                new_lines.append(clean_line)
        return new_lines
            
    def parse_labels(self, lines):
        """
        Parses the label symbols and updates the symbol dictionary accordingly.
        """
        line_num = 0
        for line in lines:
            if line.startswith(L_COMMAND_OPEN_PARAN):
                symbol = line[1:-1]
                self.symbols[symbol] = line_num
            else:
                line_num += 1
                        
    def parse_line(self, line):
        """
        Translates a single line (either C or A command) to Hack binary code.
        """
        if line.startswith(L_COMMAND_OPEN_PARAN):
            # Label line - already handled in first pass
            return None
            
        elif line.startswith(A_COMMAND_PREFIX):
            # A command
            parsed_line = self.parse_A_command(line)
            
        else:
            # C command
            parsed_line = self.parse_C_command(line)
            
        return parsed_line

    def parse_A_command(self, line):
        """
        Translates an A command line to Hack binary code.
        """
        # Get content of command
        var_name = line[len(A_COMMAND_HACK_PREFIX):]
        
        if var_name.isnumeric():
            # Got literal (number)
            return A_COMMAND_HACK_PREFIX + self.int_to_15bit_str(var_name)
            
        elif not var_name in self.symbols:
            # Got a new symbol - add to symbol dictionary and advance the address counter
            self.symbols[var_name] = self.next_addr
            self.next_addr += 1
        
        # In case of symbol - return the compiled bit command
        return A_COMMAND_HACK_PREFIX + self.int_to_15bit_str(self.symbols[var_name])
                                
    def parse_C_command(self, line):
        """
        Translates a C command line to Hack binary code.        
        """
        
        # Set jump and dest to default (in case they don't appear)
        dest = DEFAULT_DEST
        jump = DEFAULT_JUMP
        
        # Handle jump
        split_line = line.split(C_COMMAND_JMP_DELIM)
        if len(split_line) == 2:
            jump = self.parse_jump(split_line[1])
        
        # Handle dest
        split_line2 = split_line[0].split(C_COMMAND_EQUAL_DELIM)
        if len(split_line2) == 2:
            dest = self.parse_dest(split_line2[0])
        
        # Handle comp
        comp = self.parse_comp(split_line2[-1])
        
        # Return the compiled bit command
        return C_COMMAND_HACK_PREFIX + comp + dest + jump

    def parse_jump(self, jump):
        """
        Returns the binary code of the given jump string
        """
        return JUMP_DICT[jump]
        
    def parse_comp(self, comp):
        """
        Returns the binary code of the given comp string
        """    
        return COMP_DICT[comp]

    def parse_dest(self, dest):
        """
        Returns the binary code of the given dest string
        """    
        return DEST_DICT[dest]

    def int_to_15bit_str(self, x):
        """
        Converts a given string which contains an integer to a binary string, padded to 15 bits.
        """
        return bin(int(x))[2:].zfill(15)
        
def get_hack_filename(asm_filename):
    """
    Gets an input filename and returns the output Hack filename.
    """
    return ''.join(asm_filename.split('.')[:-1]) + HACK_EXTENSION

def main(path):
	if os.path.isdir(path):
		for filename in os.listdir(path):
			if filename.endswith(ASM_EXTENSION):
				handle_file(path + "/" + filename)
	else:
		handle_file(path)
    
def handle_file(asm_filename):
    """
    Main program - compiles the given file,
    and saves the output to a Hack file.
    """
    
    parser = Parser()
    
    # Open input file and handle file errors
    try:
        text = open(asm_filename).read()
    except OSError:
        raise
    
    # Compile file content and handle compilation errors
    try:
        hack_program = parser.parse(text)        
    except:
        raise Exception(BAD_INPUT)        
    
    # Write to Hack file and handle file errors.
    hack_filename = get_hack_filename(asm_filename)
    try:
        open(hack_filename, 'w').write(hack_program)
    except OSError:
        raise
            
def usage():
    """
    Prints a usage message to the user.
    """
    print(USAGE_MSG.format(argv[0]))

if __name__=="__main__":

    if len(argv) != 2:
        # Invalid command line arguments
        usage()
        exit(CODE_SUCCESS)
        
    # Run program, exit(-1) on failure
    try:
        main(argv[1])
    except OSError as e:
        print(BAD_FILE)
        exit(CODE_FAILURE)
    except Exception as e:
        raise
        exit(CODE_FAILURE)
    
    # exit(0) on success
    exit(CODE_SUCCESS)
    
    
    
