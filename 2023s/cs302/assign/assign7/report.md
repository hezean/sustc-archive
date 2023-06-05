# CS302 Assignment 7

> 何泽安 12011323
>
> 2023.5.4

#### Q. 1

##### 1-1

- Access `2333H`
  - Check TLB, take **10 ns**
  - Didn't hit (stored PFN is null), access page table, take **100 ns**
  - (valid bit = 1, no page fault)
  - Memory access (including updating TLB for PN=2), take **100 ns**
  - ***In total 210 ns***

- Access `1555H`
  - Check TLB, take **10 ns**
  - Didn't hit (stored PFN is null), access page table, take **100 ns**
  - Valid bit = 0, handle page fault, with TLB and page table updated, take **10^8^ ns**
  - Check TLB, and hit for PN=1, take **10 ns**
  - Memory access, take **100 ns**
  - ***In total (10^8^* + 220) ns**
- Access `2555H`
  - Check TLB, and hit, take **10 ns**
  - Memory access, take **100 ns**
  - ***In total 110 ns***

##### 1-2

In the previous access to `2333H`, the page frame `233H` that is assigned to virtual page 2 is recently accessed. Thus the left page frame `122H` would be replaced and assigned to virtual page 1. Thus the physical address for `1555H` is `122555H`.



#### Q. 2

> 0x 00 0021 2345 6789 = 0b 0...0 | 010000100 | 100011010 | 001010110 | offset
>

1. The address of the root page table is: **0x000 0008 4000 000**
   The PTE of index **0x084** contains a value of **0x0000 0000 2180 0011**
2. The address of the level-2 page table is: **0x000 0008 6000 000**
   The PTE of index **0x11A** contains a value of **0x0000 0000 2180 0411**
3. The address of the level-3 page table is: **0x000 0008 6001 000**
   The PTE of index **0x056** contains a value of **0x0000 0000 2180 0811**
4. The physical address for VA 0x0000 0021 2345 6789 is **0x000 0008 6002 789**
