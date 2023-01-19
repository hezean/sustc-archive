# CS202 Week 7

## Floating Point Standard `IEEE 754-1985`

- NaN (not a number): invalid operation: 0/0, subtracting infinity from infinity
- <img src="https://i.imgur.com/VNcPuWD.png" alt="Screen Shot 2022-04-01 at 16.17.38" style="zoom: 33%;" />

<img src="https://i.imgur.com/KunurlA.png" alt="Screen Shot 2022-04-01 at 18.21.42" style="zoom:33%;" />

### Relative precision

- all fraction bits are significant

- $\Delta$A/|A|=2-23×2y/|1.xxx×2y|

  ≤ 2- 23 ×2y /|1×2y |

  =2- 23 

- Single: approx 2–23: Equivalent to 23 × log10 2 ≈ 23 × 0.3 ≈ 6 decimal digits of precision

- Double: approx 2–52: Equivalent to 52 × log10 2 ≈ 52 × 0.3 ≈ 16 decimal digits of precision



<img src="https://i.imgur.com/QtIvxJ7.png" alt="Screen Shot 2022-04-01 at 19.56.22" style="zoom:50%;" />

### FP Adder Hardware

- Much more complex than integer adder
- FP adder usually takes several cycles  —  *Can be pipelined*

<img src="https://i.imgur.com/NpjPfDe.png" alt="Screen Shot 2022-04-01 at 20.04.18" style="zoom: 50%;" />

### FP Multiplication

<img src="https://i.imgur.com/aYSzYIT.png" alt="Screen Shot 2022-04-01 at 20.18.01" style="zoom:33%;" />

<img src="https://i.imgur.com/r9iaPnU.png" alt="Screen Shot 2022-04-01 at 20.22.41" style="zoom:50%;" />

<img src="../../../../Library/Application%20Support/typora-user-images/Screen%20Shot%202022-04-01%20at%2021.07.18.png" alt="Screen Shot 2022-04-01 at 21.07.18" style="zoom:67%;" />

<img src="https://i.imgur.com/9r3kLO3.png" alt="Screen Shot 2022-04-01 at 21.19.31" style="zoom:50%;" />

<img src="https://i.imgur.com/jMWsHj0.png" alt="Screen Shot 2022-04-01 at 21.22.21" style="zoom:50%;" />

# CS202 Week 8

### Recap

<img src="https://i.imgur.com/DVADT4P.png" alt="Screen Shot 2022-04-13 at 16.42.55" style="zoom:25%;" />

<img src="https://i.imgur.com/JcNXIGi.png" alt="Screen Shot 2022-04-16 at 11.12.09" style="zoom:25%;" />

- We need memory: store instructions / data

  > for now, let’s make them separate units

- We need registers, ALU, and a whole lot of control logic

- CPU operations common to all instructions

  - use the program counter (PC) to pull instruction out of instruction memory
  - read register values

### Instruction / Data Memory

<img src="https://i.imgur.com/ZD8winf.png" alt="Screen Shot 2022-04-25 at 11.47.10" style="zoom:50%;" />

<img src="https://i.imgur.com/qTuNLyh.png" alt="Screen Shot 2022-04-25 at 11.48.08" style="zoom:50%;" />

<img src="https://i.imgur.com/LxZAfhT.png" alt="Screen Shot 2022-04-25 at 11.51.45" style="zoom:50%;" />

