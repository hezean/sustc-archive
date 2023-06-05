# CS302 Assignment 6

> 何泽安 12011323
>
> 2023.4.29

#### Q. 1

While **OS** is in charge of managing the memory, e.g., keeping track of which part is free and should alloc which part of memory for a process, and setting up the hardware properly, the **hardware** simply intercepts every memory-related instruction, with the help of MMU, it verifies and translates the attached virtual address into the physical address. Take the following two mechanisms as an example:

1. base & bound: OS needs to find and assign free memory spaces for the process, and set \& store the values of base and bound registers in each context switch (for different processes). MMU intercepts the virtual address, and checks if it exceeds the `bound`, if not, add it to `base` to get the physical address.

2. paging: OS needs to manage free pages and the content of page tables. In the context switch, it set the PageTablePtr so that MMU can use it to find the L1 page table and after one or more memory accesses, it finds the head physical address of the page and adds it with the offset.



#### Q.2

|                                 |                         Segmentation                         |                            Paging                            |
| :-----------------------------: | :----------------------------------------------------------: | :----------------------------------------------------------: |
|         size of chunks          | divides memory into variable-sized chunks<br>the size of required chunk is defined by process itself | all chunks are of the same size<br>typically the page size is config by OS |
|    management of free space     | (pmm keep a linked list) to record every free chunks and their length<br>pmm's computing overhead is higher than paging's, and alloc may cause external fragmentations that's a waste | memories are managed in the basic unit of pages<br>OS keeps track of which page is free and simply assigns it to a process that allocates |
|     context switch overhead     |       the segment registers must be saved and restored       | only need to change the content of PageTablePtr register to the address of root page table, this overhead is lower than segmentation's<br>in some TLB impl, may also need to delete all entries |
|          fragmentation          | suffers from both external fragmentation and internal fragmentation | only has internal fragmentation<br>for a well-defined page size, the total internal fragmentation size won't be large |
| status bits and protection bits | state bit indicates whether the segmentation grows positive<br>each segmentation has a protection bit, supporting the memory sharing | State bits indicate whether the page is dirty, accessed, etc, to support advanced pmm like replacement policies<br>stored in page table (PTE), each page has a set of protection bits to indicate the access permissions |



#### Q. 3

The OS could divide the memory into 2^46^ / 2^13^ = 2^33^ pages.

Each page could contain 2^13^ / 2^2^ = 2^11^ PTE.

Then we can use a 3-level paging mechanism to map these 2^33^ pages:

|     L1 index (11 bits)      |    L2 index (11 bits)     |     L3 index (11 bits)     |    offset (13 bits)     |
| :-------------------------: | :-----------------------: | :------------------------: | :---------------------: |
| 45                       35 | 34                     24 | 23                      13 | 12                    0 |



#### Q. 4

##### Q. 4-1

**Page size**: 2^12^ byte = **4 KB**

This OS supports at most 2^32^ byte memory, which could be divided into 2^32^ / 2^12^ = 2^20^ pages. The **max page table** therefore contains 2^20^ PTE, taking 2^20^ $\times$ 4 byte = **4 MB**

##### Q. 4-2

**0xC302C302** = 0b11000011000000101100001100000010 = 1100001100~L1\ PN~0000101100~L2\ PN~001100000010~offset~

*1-st level page number*: 1100001100~bin~ = 780~dec~

*offset*:  001100000010~bin~ = 770~dec~

**0xEC6666AB** = 0b11101100011001100110011010101011 = 1110110001~L1\ PN~1001100110~L2\ PN~011010101011~offset~

*2-nd level page number*: 1001100110~bin~ = 614~dec~

*offset*:  011010101011~bin~ = 1707~dec~
