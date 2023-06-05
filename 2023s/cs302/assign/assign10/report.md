# CS302 Assignment 10

> 何泽安 12011323
>
> 2023.5.24

##### 1. Disk scheduling

(1) READ/WRITE data time = (1) **Seek time** + (2) **Rotational latency** + (3) **Transfer time**

(2) Use FIFO\SSTF\SCAN\CSCAN algorithm to read the six sectors

```
avg. rational latency = (time per round) / 2
                      = (1 / (12000 r/min) * 60 s/min) / 2
                      = 0.005 s / 2
                      = 2.5 ms
```

|      | FIFO access | FIFO time | SSTF access | SSTF time | SCAN access  | SCAN time |   CSCAN access   | CSCAN time |
| :--: | :---------: | :-------: | :---------: | :-------: | :----------: | :-------: | :--------------: | :--------: |
| init |    (100)    |     -     |    (100)    |     -     |    (100)     |     -     |      (100)       |     -      |
|  #1  |     70      |    30     |     90      |    10     |     120      |    20     |       120        |     20     |
| (*)  |      -      |     -     |      -      |     -     | move twd 199 |    79     | move twd <br> 199 -> 0 |     79 + 199     |
|  #2  |     30      |    40     |     70      |    20     |      90      |    109    |        20        |    20    |
|  #3  |     90      |    60     |     60      |    10     |      70      |    20     |        30        |     10     |
|  #4  |     120     |    30     |     30      |    30     |      60      |    10     |        60        |     30     |
|  #5  |     60      |    60     |     20      |    10     |      30      |    30     |        70        |     10     |
|  #6  |     20      |    40     |     120     |    100    |      20      |    10     |        90        |     20     |

Then we need to sum up their seek time, and use the average rational latency to calculate their total latency for 6 accesses.

- FIFO: 30 + 40 + 60 + 30 + 60 + 40 + 2.5 * 6 = 275 ms
- SSTF: 10 + 20 + 10 + 30 + 10 + 100 + 2.5 * 6 = 195 ms
- SCAN: 20 + 79 + 109 + 20 + 10 + 30 + 10 + 2.5 * 6 = 293 ms
- CSCAN: 20 + 79 + 199 + 20 + 10 + 30 + 10 + 20 + 2.5 * 6 = 403 ms  
