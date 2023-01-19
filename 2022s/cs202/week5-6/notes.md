# CS202 Week 5

## MIPS Addressing

<img src="../../../../Library/Application%20Support/typora-user-images/Screen%20Shot%202022-03-21%20at%2008.21.41.png" alt="Screen Shot 2022-03-21 at 08.21.41" style="zoom:50%;" />

### Immediate Addressing

immediate - 16bits

<img src="https://i.imgur.com/THn9zx5.png" alt="Screen Shot 2022-03-26 at 00.43.59" style="zoom: 50%;" />

<img src="https://i.imgur.com/t7aXNYX.png" alt="Screen Shot 2022-03-26 at 00.57.25" style="zoom: 50%;" />

### Starting a C Program

`.c`  -compiler->  `.asm`  -assembler->  `.o` `.lib/dll`  -linker->  `.out`  -loader->  memory

#### Role of Assembler

- Convert pseudo-instructions into actual hardware instructions
- Convert assembly instrs into machine instrs – a separate object file (x.o) is created for each C file (x.c)

#### Role of Linker

- Stitches different object files into a single executable
  - patch internal and external references
  - determine addresses of data and instruction labels
  - organize code and data modules in memory

> Some libraries (DLLs) are dynamically linked – the executable points to dummy routines – these dummy routines call the dynamic linker-loader so they can update the executable to jump to the correct routine

### Starting Java Applications

<img src="https://i.imgur.com/eHcFv7g.png" alt="Screen Shot 2022-03-26 at 01.06.16" style="zoom:50%;" />

### Saves and Restores

![Screen Shot 2022-03-26 at 01.14.02](https://i.imgur.com/kVcF7DL.png)

## Lab

### Procedure

#### Pass args

By convention, the first four arguments are passed in registers \$a0-\$a3. Any remaining arguments are pushed on the stack and appear at the beginning of the called procedure’s stack frame.

#### jal instruction

Saves the return address in register $ra and jumps to the callee’s first instruction.

#### In Callee

1. Allocate memory for the stack by substracting the frame’s size from the stack pointer($sp).

2. Save callee-saved registers in the frame.
   1. A callee must save the values in these registers($s0-$s7 and $ra) before altering them, since the caller expects to find these registers unchanged after the call.
   2. Register $ra ONLY needs to be saved if the callee itself makes a call. The other callee-saved registers that are used also must be saved.
3. Before returning to caller
   1. If the callee is a function that returns value(s), place the returned value(s) in register \$v0~\$v1
   2. Restore all callee-saved registers that were saved upon procedure entry
   3. Pop the stack frame by adding the frame size to $sp
4. Return by jumping to the address in register $ra

### Stack vs Heap

- **Stack**: used to store the local variable, usually used in calle.
- **Heap**: The heap is reserved for **sbrk** and **break** system calls, and it not always present.




# CS202 Week 6  (Arithmetic for Computers)

## Operations on integers

> **Overflow** if result out of range:
>
> - no overflow, if ddding +ve and –ve operands
> - Overflow, if
>   - Adding two +ve operands, get –ve operand
>   - Adding two -ve operands, get +ve operand

<img src="https://i.imgur.com/gJ5j9YB.png" alt="Screen Shot 2022-03-30 at 18.55.41" style="zoom: 33%;" /><img src="https://i.imgur.com/79UC4sk.png" alt="Screen Shot 2022-03-30 at 19.09.35" style="zoom:33%;" />

<img src="https://i.imgur.com/9uMByYm.png" alt="Screen Shot 2022-03-31 at 13.16.03" style="zoom:50%;" />

### Dealing with Overflow

- Some languages (e.g., C) ignore overflow: Use MIPS **addu, addiu (“u” means it doesn’t generate overflow exception, but the immediate can be a signed number), subu** instructions

- Other languages (e.g., Ada, Fortran) require raising an exception
  - Use MIPS add, addi, sub instructions
  - On overflow, invoke exception handler
    - Save PC in exception program counter (EPC) register
    - Jump to predefined handler address
    - **mfc0** (move from coprocessor reg) instruction can retrieve EPC value, to return after corrective action

### Multiplication

<img src="https://i.imgur.com/pCDpHHF.png" alt="Screen Shot 2022-03-31 at 14.25.22" style="zoom: 33%;" /> <img src="https://i.imgur.com/p9MDKg2.png" alt="Screen Shot 2022-03-31 at 15.54.52" style="zoom:33%;" />

<img src="https://i.imgur.com/DWNszlv.png" alt="Screen Shot 2022-03-31 at 16.36.03" style="zoom: 33%;" /><img src="https://i.imgur.com/HRwKKWD.png" alt="Screen Shot 2022-03-31 at 16.44.02" style="zoom: 33%;" />

32-bit ALU adding<img src="https://i.imgur.com/2cgCzu5.png" alt="Screen Shot 2022-03-31 at 16.56.26" style="zoom:50%;" />

#### MIPS Multiplication

Two 32-bit registers for product: **HI**: most-significant 32 bits / **LO**: least-significant 32-bits

#### Instructions

- **mult rs, rt / multu rs, rt**: 64-bit product in HI/LO
- **mfhi rd / mflo rd**: Move from HI/LO to rd, Can test HI value to see if product overflows 32 bits
- **mul rd, rs, rt**: Least-significant 32 bits of product –> rd

### Division

<img src="https://i.imgur.com/aM1OGcT.png" alt="Screen Shot 2022-03-31 at 20.27.57" style="zoom:33%;" />

<img src="https://i.imgur.com/SUaIPor.png" alt="Screen Shot 2022-03-31 at 20.34.45" style="zoom: 33%;" />

<img src="../../../../Library/Application%20Support/typora-user-images/Screen%20Shot%202022-03-31%20at%2020.52.47.png" alt="Screen Shot 2022-03-31 at 20.52.47" style="zoom:33%;" />



#### Signed Division

- The quotient is +, if the signs of divisor and dividend agrees, otherwise, quotient is –

- The sign of the remainder matches that of the dividend

#### Faster Division

- Can’t use parallel hardware as in multiplier: Subtraction is conditional on sign of remainder
- Faster dividers (e.g. SRT devision) generate multiple quotient bits per step, Still require multiple steps

#### MIPS Division

- Use HI/LO registers for result: HI: 32-bit remainder, LO: 32-bit quotient
- **div rs, rt / divu rs, rt**, *No overflow or divide-by-0 checking*

- No overflow or divide-by-0 checking, Software must perform checks if required
- Use **mfhi, mflo** to access result



# Lab6

<img src="https://i.imgur.com/kQ9kkjY.png" alt="Screen Shot 2022-04-08 at 11.11.10" style="zoom:50%;" />

<img src="https://i.imgur.com/865sWNM.png" alt="Screen Shot 2022-04-08 at 13.41.59" style="zoom: 50%;" />

<img src="https://i.imgur.com/N9BCzPM.png" alt="Screen Shot 2022-04-08 at 13.52.05" style="zoom:67%;" />
