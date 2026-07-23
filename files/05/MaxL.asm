// File name: files/04/MaxL.asm

// Symbol-less version of the Max.asm program.

@0
D=M
@1
D=D-M
@10
D;JGT
@1
D=M
@12
0;JMP
@0
D=M
@2
M=D
@14
0;JMP
