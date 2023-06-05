# CS302 Assignment 9

> 何泽安 12011323
>
> 2023.5.16

##### 1. Explain what happens when the process accesses a memory page not present in the physical memory.

- When accessing a certain address, MMU do the check on the present bit of PTE. If present bit = 0, a page fault will be thrown and OS will use the page-fault handler to handle it:
  - First, find a page to contain the incoming page. OS first tries to find/allocate a currently free page, if there's no free page left, it will replace a existing page (the selection is guided under the page replacement policy).
  - Then, OS swaps the page into the physical memory (the page just selected in the last step) - it find the page's address in the disk by reading the info stored in PTE, then fetch the data.
  - After the fetch I/O operation finishes, OS updates the PTE of this VA, setting the PPN to the just selected page and present bit = 1 (if a page was replaced out from the memory, OS also needs to maintain its PTE).
- This instruction will be re-executed again, this time, with the updated page table, it won't cause page fault and can successfully finish the I/O operation, during which, TLB may be updated.

##### 2. Realize the Clock algorithm

- Implementation

![image-20230523094034370](./report.assets/image-20230523094034370.png)

![image-20230523094049477](./report.assets/image-20230523094049477.png)

- Check

![image-20230523094002319](./report.assets/image-20230523094002319.png)
