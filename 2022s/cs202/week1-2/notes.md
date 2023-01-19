# CS202 Week 1

### Components of a Computer

Input Device, Output Device, Memory, Processor (Control, Datapath)

> “PC Motherboard” ?  + IO bus slots?

<img src="https://i.imgur.com/5N0SWa0.png" alt="Screen Shot 2022-02-25 at 20.12.35" style="zoom:35%;" />

#### Inside the CPU

- Datapath: performs operations on data
- Control: control the sequence of datapath, memory, I/O
- Cache memory  (Small fast SRAM memory for immediate access to data)



### Moore’s Law — Micro Processor Advances

> The number of transistors that can be integrated on a die would double every 18 to 24 months
>
> how: scale the transistor (晶体管) channel length



### Storage

- Volatile main memory  (Loses instructions and data when power off)  **易失性存储器**
- Non-volatile secondary memory

<img src="https://i.imgur.com/W4zirUY.png" alt="Screen Shot 2022-02-25 at 21.47.40" style="zoom:33%;" />



### Eight Great Ideas

- 面向摩尔定律的设计
- 使用抽象简化设计
- 加速大概率事件
- 通过并行提高性能
- 通过流水线提高性能
- 通过预测提高性能
- 存储器层次  hierarchy of memory
- 通过冗余提高可靠性



# CS202 Week 2

<img src="https://i.imgur.com/TrFxMRk.png" alt="Screen Shot 2022-02-26 at 00.18.18" style="zoom: 25%;" />

系统软件：提供常用服务的软件，**os** + **编译程序** + 加载程序 + 汇编程序 etc

High-level language (HLL)  –**compiler**–>  Assembly language program *(for MIPS)*  –**assembler**–>  Binary machine language program *(for MIPS)*

def 汇编语言：以助记符形式表示的机器指令



## Performance

> To evaluate the performance, we must define the metric first!

<img src="https://i.imgur.com/fzWdE1a.png" alt="Screen Shot 2022-02-26 at 01.32.46" style="zoom:35%;" />

- Responce time  (How long it takes to do a task)
  - **Performance = 1 / Execution Time**
- Throughput  (Total work done per unit time)

```
“X is n time as fast as Y”
> Performance(X) / Performance(Y)
= Execution time(Y) / Execution time(X)
= n
```



- Elapsed time  (Processing, I/O, OS overhead, idle time…)
- CPU time
- <img src="https://i.imgur.com/SMxZtMV.png" alt="Screen Shot 2022-02-26 at 11.00.20" style="zoom:33%;" />



<div style="color:red">CPU Time = #clock cycles * clock period = #clock cycles / clock rate</div>

- Reducing number of clock cycles
- Increasing clock rate

### Instruction Count & CPI

- #clock cycles = #instructions * CPI (cycles per instruction)
- CPU Time = Instruction Count × CPI × Clock Period
- *ISA* = Instruction set architecture

<img src="https://i.imgur.com/1OREARl.png" alt="Screen Shot 2022-02-26 at 17.27.53" style="zoom:33%;" />

<img src="https://i.imgur.com/QCqCUoA.png" alt="Screen Shot 2022-02-26 at 21.54.46" style="zoom:25%;" />



### Energy Consumption of a chip

<div style="color:red">Energy consumption = dynamic energy + static energy</div>

- dynamic: transistors switch from 0->1->0, **primary**

- static: the energy cost when no transistor switches

  <img src="https://i.imgur.com/p3WhHjU.png" alt="Screen Shot 2022-02-26 at 22.27.43" style="zoom:33%;" />

<img src="https://i.imgur.com/V2vdENq.png" alt="Screen Shot 2022-02-26 at 23.55.10" style="zoom:25%;" />



### Multiprocessors

- More than one processor per chip
- Requires explicitly parallel programming

### Benchmarking

> Architecture design is very bottleneck-driven – make the common case fast, do not waste resources on a component that has little impact on overall performance/power.

Each vendor announces a **SPEC (Standard Performance Evaluation Cooperative)** rating for their system

**Amdahl’s Law**: performance improvements through an enhancement is limited by the fraction of time the enhancement comes into play. *aka. make the common case fast*