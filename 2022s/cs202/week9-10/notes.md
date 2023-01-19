# CS202 Week 9

<img src="https://i.imgur.com/KpZmoHN.png" alt="Screen Shot 2022-04-25 at 13.39.54" style="zoom:50%;" />

<img src="https://i.imgur.com/89JILRV.png" alt="Screen Shot 2022-04-25 at 14.49.40" style="zoom:50%;" />

<img src="https://i.imgur.com/V2Lx8GB.png" alt="Screen Shot 2022-04-25 at 14.59.57" style="zoom:50%;" />

<img src="https://i.imgur.com/EiccQ3u.png" alt="Screen Shot 2022-04-25 at 15.04.32" style="zoom:50%;" />



<img src="https://i.imgur.com/6SG0aNQ.png" alt="Screen Shot 2022-04-25 at 15.05.23" style="zoom:50%;" />

<img src="https://i.imgur.com/HC3aQ2g.png" alt="Screen Shot 2022-04-25 at 15.09.20" style="zoom:50%;" />

<img src="https://i.imgur.com/sz7DWfD.png" alt="Screen Shot 2022-04-25 at 15.11.30" style="zoom:50%;" />

<img src="https://i.imgur.com/Lk2HOga.png" alt="Screen Shot 2022-04-25 at 15.25.12" style="zoom:50%;" />

#### 流水线冒险 hazard：在下一个时钟周期中下一条指令不能执行

- 结构冒险 structural hazard
  - 硬件不支持多条指令在同一时钟周期执行
- 数据冒险 (pipeline) data hazard
  - 发生在一条指令必须等待另一条指令的完成而导致流水线暂停的情况下
  - 即因无法提供指令执行所需数据而导致指令不能在预定时钟周期内执行的情况
  - **！how to solve it**
    - find: 在解决 data hazard 前不需要等待指令的执行结束
    - 从内部资源中直接提前得到缺少的运算项的过程：**前推 forwarding** aka **旁路 bypassing**
  - bypassing仍要阻塞一个步骤：取数-使用型数据冒险 load-use data hazard
    - pipeline stall ~ bubble
- 控制冒险 control hazard  /  分支冒险 branch hazard
  - 决策依赖于一条指令的结果，而其他指令正在进行中
  - stall: 速度下降
  - predict: 1. 总预测分支未发生：预测正确，全速执行；预测错误，bubble
  - predict: 2. **branch prediction** *动态硬件预测器*
    - 保存每次分支的历史记录，基于此预测可达90%正确率
  - 预测错误：流水线控制必须确保被错误预测的分支后面的指令执行不会生效，且在正确的分支地址处重启流水线
  - (predict: 3.) delayed decision, delayed branch

#### pipeline->数据通路需要分为5parts

从右往左的指令流中：

- WB 写回阶段（数据通路中间的reg file）
- 选择PC的下一个值时，PC+4 / MEM级的分支地址？

这两项不影响当前指令，但影响之后的指令：**WB->data hazard    PC->control hazard**





### lab

CPU: 取指令 -> 分析 (controller+decoder) -> 执行



<img src="https://i.imgur.com/eY4AWh6.png" alt="Screen Shot 2022-04-30 at 04.07.29" style="zoom:50%;" />

