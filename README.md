# 👑 Queen Beenie's Hack Computer

A complete, from-scratch implementation of the **Hack Computer** — built from the ground up starting at elementary logic gates (NAND) all the way to a working CPU, Assembler, and Virtual Machine, following the *"From NAND to Tetris"* computer architecture curriculum.

This project demonstrates how a modern computer is built in layers: **Boolean Logic → Boolean Arithmetic → Sequential Logic (Memory) → CPU & Computer Architecture → Assembler → Virtual Machine Translator → Jack (high-level language) program**.

---

## ✨ Author

**Made by: Beenish Tasaffar**

---

## 📁 Project Structure

```
Queen Beenie's Hack Computer/
│
├── files/                  # All project solutions, organized by stage
│   ├── 01/                 # Boolean Logic — basic gates (And, Or, Not, Xor, Mux, DMux, etc.)
│   ├── 02/                 # Boolean Arithmetic — Half/Full Adder, Add16, Inc16, ALU
│   ├── 03/                 # Sequential Logic — Bit, Register, RAM8/64/512/4K/16K, PC (Program Counter)
│   ├── 04/                 # Computer Architecture — CPU, Memory, full Computer, plus test assembly programs
│   ├── 05/                 # Assembler — translates Hack assembly (.asm) into machine code (.hack)
│   ├── 06/                 # VM Translator (Part I) — Stack Arithmetic & Memory Access commands
│   ├── 07/                 # VM Translator (Part II) — Program Flow & Function Calls
│   └── 08/                 # Jack Language Program — a sample "Square" game written in Jack
│
└── tools/                  # The official Nand2Tetris Software Suite (Java-based tools)
    ├── HardwareSimulator.sh / .bat   # Test .hdl chip designs
    ├── CPUEmulator.sh / .bat         # Run and test .asm / .hack programs on the Hack CPU
    ├── Assembler.sh / .bat           # Convert .asm → .hack
    ├── VMEmulator.sh / .bat          # Run and debug .vm files
    ├── JackCompiler.sh / .bat        # Compile .jack files → .vm files
    ├── TextComparer.sh / .bat        # Compare two files (used to verify test outputs)
    ├── builtInChips/                 # Pre-built chip reference implementations
    ├── builtInVMCode/                # Pre-built OS/VM reference classes
    ├── OS/                            # The Jack Operating System (.vm files)
    └── bin/                           # Compiled Java classes & libraries used by the tools
```

Each stage folder under `files/` typically contains:
| Extension | Meaning |
|---|---|
| `.hdl` | Hardware Description Language file — your chip design |
| `.tst` | Test script that the Hardware/CPU Simulator runs |
| `.cmp` | The expected ("compare") output for a test |
| `.out` | The actual output produced when you run a test |
| `.asm` | Hack Assembly language program |
| `.hack` | Compiled Hack machine code (binary, 16-bit instructions) |
| `.vm` | Virtual Machine code |
| `.jack` | Jack high-level language source code |

---

## 🛠️ Requirements / Installation

All the simulator tools in this repository (`tools/`) are **Java applications**, so the only real prerequisite is a working **Java Runtime Environment (JRE)**.

### 1. Install Java

**Windows:**
1. Download the latest JDK/JRE from [https://www.oracle.com/java/technologies/downloads/](https://www.oracle.com/java/technologies/downloads/) (or use [Adoptium/Temurin](https://adoptium.net/) — free & open source).
2. Run the installer and follow the setup wizard.
3. Verify installation by opening **Command Prompt** and running:
   ```
   java -version
   ```

**macOS:**
```bash
brew install openjdk
```
Then verify:
```bash
java -version
```

**Linux (Debian/Ubuntu):**
```bash
sudo apt update
sudo apt install default-jre -y
java -version
```

If `java -version` prints a version number (e.g. `openjdk version "17.0.x"`), you're ready to go.

### 2. Get the Project

- Extract this project (or clone it from GitHub — see the guide below) to any folder on your computer, e.g.:
  ```
  C:\Projects\Queen Beenie's Hack Computer     (Windows)
  ~/Projects/Queen Beenie's Hack Computer      (macOS/Linux)
  ```

### 3. Make the shell scripts executable (macOS/Linux only)

Windows users can skip this — `.bat` files run directly by double-clicking.

```bash
cd "Queen Beenie's Hack Computer/tools"
chmod +x *.sh
```

That's it — no other installation is required. No external dependencies, no package managers, nothing to compile.

---

## ▶️ How to Run the Tools

All tools live inside the `tools/` folder. Run the `.sh` file (macOS/Linux) or the `.bat` file (Windows) that matches the task you want to do.

### 🔹 Hardware Simulator — test your chip (`.hdl`) designs
Used for projects **01, 02, 03, 04** (logic gates, ALU, memory chips, CPU).
```bash
cd tools
./HardwareSimulator.sh          # macOS/Linux
HardwareSimulator.bat           # Windows
```
Inside the GUI: **File → Load** a `.tst` file from the relevant `files/0X` folder (e.g. `files/01/And.tst`), then click **Run**. Compare the `.out` produced with the `.cmp` file — they should match.

### 🔹 CPU Emulator — run assembly / machine code programs
Used for project **04**.
```bash
./CPUEmulator.sh                # macOS/Linux
CPUEmulator.bat                 # Windows
```
Load a `.tst` file (e.g. `files/04/ComputerMax.tst`) and run it to simulate the full computer executing a program.

### 🔹 Assembler — translate `.asm` → `.hack`
Used for project **05**.

Interactive (GUI) mode:
```bash
./Assembler.sh
```
Command-line mode (fast, no GUI):
```bash
./Assembler.sh "../files/05/Add.asm"
```
This produces `Add.hack` next to the `.asm` file.

### 🔹 VM Emulator — run/debug Virtual Machine (`.vm`) code
Used for projects **06 & 07**.
```bash
./VMEmulator.sh                 # macOS/Linux
VMEmulator.bat                  # Windows
```
Load a `.vm` file or a folder containing several `.vm` files (for multi-file programs) and run it.

### 🔹 Jack Compiler — compile `.jack` → `.vm`
Used for project **08** (compiling the `Square` game).
```bash
./JackCompiler.sh "../files/08/Square"
```
This compiles every `.jack` file inside the `Square` folder into corresponding `.vm` files, which can then be run in the VM Emulator (together with the OS files in `tools/OS/`).

### 🔹 Text Comparer — verify your output against the expected result
```bash
./TextComparer.sh "../files/01/And.cmp" "../files/01/And.out"
```
Prints whether the two files are identical — used to confirm a project stage passes all tests.

> 💡 Tip: On Windows, if double-clicking a `.bat` file closes immediately, run it from Command Prompt instead so you can read any error messages.

---

## 🧪 Quick Test Run (Example)

To confirm everything is installed correctly, try this:

```bash
cd tools
./Assembler.sh "../files/04/Add.asm"
```
This should generate an `Add.hack` file — congratulations, your Java setup and the toolchain both work! 🎉

---

## 📚 About This Project

This project follows the layered build process taught in the *Nand2Tetris* course (based on the book **"The Elements of Computing Systems"** by Noam Nisan & Shimon Schocken):

1. **Elementary Logic Gates** — building And, Or, Not, Xor, Mux, DMux from Nand
2. **Boolean Arithmetic** — building an Adder and a full 16-bit ALU
3. **Sequential Logic** — building Flip-Flops, Registers, and RAM using the clock
4. **Machine Language & Computer Architecture** — building the CPU and wiring the full Computer (CPU + Memory + ROM)
5. **Assembler** — translating human-readable Hack Assembly into binary machine code
6–7. **Virtual Machine Translator** — translating stack-based VM code into Hack Assembly (arithmetic, memory access, branching, function calls)
8. **High-Level Programming (Jack)** — a sample object-oriented Jack program (`Square` game) that can be compiled down through the whole stack and run on the simulated computer

---

## 📄 License

This project is shared for educational purposes as part of a computer architecture / systems course. Feel free to reference it while learning — please don't submit it as your own coursework if you're currently taking a similar course, in line with academic integrity policies.

---

*Built with 🧠 and a lot of NAND gates by **Beenish Tasaffar**.*
