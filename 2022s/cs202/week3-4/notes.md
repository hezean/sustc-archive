# CS202 Week 3

## Instruction Set

Two forms of instruction set:

- Assembly language: written by people
- Machine language: read by computer

> Each Intel processor reads in the same x86 instructions, but each processor handles instructions differently
>
> Java programs are converted into portable bytecode that is converted into machine instructions during execution (JIT compilation)

<img src="https://i.imgur.com/czX2ABJ.png" alt="Screen Shot 2022-03-14 at 13.43.47" style="zoom:33%;" />

<img src="https://i.imgur.com/7m7zgdB.png" alt="Screen Shot 2022-03-14 at 14.18.04" style="zoom:33%;" />

**MIPS**: Microprocessor without interlocked pipeline stages

#### RISC vs CISC

- RISC: reduced instruction set computer, e.g. MIPS, ARM, PowerPC, RISC-V
- CISC: complex instruction set computer, e.g. x86

> To simplify the instructions, we require that each instruction (add, sub) only operate on registers



### Registers

- The MIPS ISA has 32 registers (x86 has 8 registers)

- Each register is 32-bit wide (modern 64-bit architectures have 64-bit wide registers)

- A 32-bit entity (4 bytes) is referred to as a word
- To make the code more readable, registers are partitioned as \$s0-\$s7 (C/Java variables), \$t0-\$t9 (temporary variables)…



## Memory Operands

<img src="https://i.imgur.com/jEf4xcw.png" alt="Screen Shot 2022-03-14 at 19.09.45" style="zoom:33%;" />



<img src="https://i.imgur.com/OGDE3fH.png" alt="Screen Shot 2022-03-15 at 15.56.15" style="zoom:50%;" />



## Stored-Program Computer

- Instructions are represented as numbers.

- Programs are stored in memory to be read or written, just like data.

<img src="https://i.imgur.com/KcbPXn4.png" alt="Screen Shot 2022-03-15 at 16.33.28" style="zoom:33%;" />





# CS202 Week 4

## Control Instructions: if else

Conditional branch: Jump to instruction L1 if register1 equals to register2: 

```assembly
beq register1, register2, L1  # Branch if EQual
```

 Similarly, **bne **(Branch if Not Equal) and **slt** (set-on-less-than)

```assembly
      bne $s3, $s4, Else
      add $s0, $s1, $s2
      j Exit
Else: sub $s0, $s1, $s2
Exit:
```

## Loop

```assembly
Loop: sll $t1, $s3, 2
			add $
```

#### Why not blt, bge, etc?

Hardware for <, ≥, … slower than =, ≠

Combining with branch involves more work per instruction, requiring a slower clock All instructions penalized!

• beq and bne are the common case

• This is a good design compromise



## Procedures

>  acquires resources → performs task → covers his tracks → returns back with desired result

When the procedure is executed (when the caller calls the callee), there are six steps

- parameters (arguments) are placed where the callee can see them
- control is transferred to the callee
- acquire storage resources for callee
- execute the procedure
- place result value where caller can access it
- return control to caller

### Registers Used during Procedure Calling

- \$a0 - \$a3: four argument registers to pass parameters

- \$v0 - \$v1: two value registers to return the values

- \$ra: one return address register to return to the point of origin in the caller



## Jump and Link

**program counter (PC)**: A special register maintains the address of the instruction currently being executed

```assembly
jal NewProcedureAddress
```

the current PC (actually, PC+4) is saved in the register $ra and we jump to the procedure’s address (the PC is accordingly set to this address)

<img src="https://i.imgur.com/ujds3Nd.png" alt="Screen Shot 2022-03-21 at 02.07.59" style="zoom:50%;" />

<img src="https://i.imgur.com/Hph9BJW.png" alt="Screen Shot 2022-03-21 at 02.09.09" style="zoom:50%;" />

## Storage Management on a Call/Return

- A new procedure must create space for all its variables on the stack
- Before executing the jal, the caller must save relevant values in \$s0-\$s7, \$a0-\$a3, \$ra, temps into its own stack space

- Arguments are copied into $a0-$a3; the jal is executed
- After the callee creates stack space, it updates the value of $sp
- Once the callee finishes, it copies the return value into \$v0, frees up stack space, and \$sp is **incremented**

- On return, the caller may bring in its stack values, ra, temps into registers

- The responsibility for copies between stack and registers may fall upon either the caller or the callee

  <img src="https://i.imgur.com/Br4vra6.png" alt="Screen Shot 2022-03-21 at 02.32.47" style="zoom:50%;" />

<img src="https://i.imgur.com/pIOHfqS.png" alt="Screen Shot 2022-03-21 at 02.38.20" style="zoom:50%;" />

<img src="https://i.imgur.com/XbIAdHm.png" alt="Screen Shot 2022-03-21 at 02.39.50" style="zoom:50%;" />

<img src="https://i.imgur.com/jDYV0XB.png" alt="Screen Shot 2022-03-21 at 02.47.36" style="zoom:50%;" />
