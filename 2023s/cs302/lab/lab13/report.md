# CS302 Lab13 Report

> 何泽安 12011323
>
> 2023.5.10

##### 1. When do `swap_in` and `swap_out` occur respectively?

Swapping operations occur when the target data is not presented in RAM. When there is enough free space in the memory for the target data stored in the disk, a swap-in loads the data into memory; in the case of no enough space is left, swap-out is needed to provide spaces for new data to be loaded in, by selecting some data/pages that are considered to be less frequent accessed or may cause least affections (the policy depends on the implementation of the replacement algorithm). Note that incluing creating and destroying a process, swaps happen.



##### 2. What does the chain pointed to by `(list_entry_t*) mm->sm_priv` do, and under what circumstances will pages be added to this chain?

This linked list is the page table sequence maintained by the operating system to record all pages that can be swapped out in the process. When a page is swapped out, the page will be added to the end of the linked list, so that a linked list sorted by the most recent access time of the page can be maintained. The most recently used page is at the front of the linked list, and the oldest page is at the back of the linked list.

Pages are swappable and when a process accesses a virtual page, but the page is not in memory, the operating system will read the page from the hard disk and put it into memory to satisfy the process's access request.
A swap_in operation also occurs when the operating system needs to map some system files into memory during startup.
Join this linked list when swap_in occurs.



##### 3. Why is OPT a theoretical algorithm and what is the point of its existence?

OPT is not practical because it requires the knowledge of a sequence of future page requests, which is not possible in real-world scenarios.

The reason for OPT's existence is to provide a benchmark baseline (best/ideal case) against other page replacement algorithms. By comparing the performance of other algorithms to OPT, we can determine how close they are to the optimal solution. This information can be used to evaluate the effectiveness of different page replacement algorithms and to improve their performance.



##### 4. A system assigns four physical pages to a process, and given a page access sequence of `7 0 1 2 0 3 0 4 2 3 0 3 2 1 2 0 1 7 0 1`, give the replacement process of the physical page content (using the OPT algorithm and CLOCK algorithm, respectively), and the corresponding number of missing pages.

###### OPT

| page access | physical pages | page fault? |
| ----------- | -------------- | ----------- |
| 7           | 7              | 7           |
| 0           | 7 0            | 0           |
| 1           | 7 0 1          | 1           |
| 2           | 7 0 1 2        | 2           |
| 0           | 7 0 1 2        |             |
| 3           | 3 0 1 2        | 3           |
| 0           | 3 0 1 2        |             |
| 4           | 3 0 4 2        | 4           |
| 2           | 3 0 4 2        |             |
| 3           | 3 0 4 2        |             |
| 0           | 3 0 4 2        |             |
| 3           | 3 0 4 2        |             |
| 2           | 3 0 4 2        |             |
| 1           | 1 0 4 2        | 1           |
| 2           | 1 0 4 2        |             |
| 0           | 1 0 4 2        |             |
| 1           | 1 0 4 2        |             |
| 7           | 1 0 7 2        | 7           |
| 0           | 1 0 7 2        |             |
| 1           | 1 0 7 2        |             |

8 missing pages.

###### CLOCK

| page access | physical pages | reference bit | page fault? |
| ----------- | -------------- | ----------- | ------------- |
| 7           | 7 | 1 '0 0 0 |7|
| 0           | 7 0 | 1 1 '0 0 |0|
| 1           | 7 0 1 | 1 1 1 '0 |1|
| 2           | 7 0 1 2 | '1 1 1 1 |2|
| 0           | 7 0 1 2 | '1 1 1 1 ||
| 3           | 3 0 1 2 | 1 '0 0 0 |3|
| 0           | 3 0 1 2 | 1 '1 0 0 ||
| 4           | 3 0 4 2 | 1 0 1 '0 |4|
| 2           | 3 0 4 2 | 1 0 1 '1 ||
| 3           | 3 0 4 2 | 1 0 1 '1 ||
| 0           | 3 0 4 2 | 1 1 1 '1 ||
| 3           | 3 0 4 2 | 1 1 1 '1 ||
| 2           | 3 0 4 2 | 1 1 1 '1 ||
| 1           | 3 0 4 1 | '0 0 0 1 |3|
| 2           | 2 0 4 1 | 1 '0 0 1 ||
| 0           | 2 0 4 1 | 1 '1 0 1 ||
| 1           | 2 0 4 1 | 1 '1 0 1 ||
| 7           | 2 0 7 1 | 1 0 1 '1 |7|
| 0           | 2 0 7 1 | 1 1 1 '1 ||
| 1           | 2 0 7 1 | 1 1 1 '1 ||

9 missing pages.
