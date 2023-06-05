# CS302 Lab10 Report

> 何泽安 12011323
>
> 2023.4.19

##### 1. The process of SV39 conversion

1. The OS properly set up the `satp` register for the currently running process, the 64 bits contain information of `MODE` (``0b1000` for SV39), the `ASID` for the process, and the *`PPN` of the base address of the level 1 page table*. When accessing the memory, we take the *`VA[38:30]` as the level 1 page table number*. MMU then reads the PTE at the physical address (satp.PPN $\times$ 4096 + VA[38:30] $\times$ 8).
2. The PTE read above contains the base address of the level 2 page table, then parse `VA[29:21]` as the page number of level 2 page table, whose base address is (PTE.PPN $\times$ 4096 + VA[29:21] $\times$ 8).
3. Similar to step 2, the base address of level 3 page table is the PPN of the PTE read in step 2, say, PTE2. The page number is `VA[20:12]`, thus the physical address of the leaf PTE is (PTE2.PPN $\times$ 4096 + VA[20:12] $\times$ 8).
4. The physical address of the VA is the physical address stored in the leaf PTE adding the inner-page offset, which is `VA[11:0]`. Finally, thus physical address is (LeafPTE.PPN $\times$ 4096 + VA[11:0]).

##### 2. The size of a huge page

There are 9 (VPN[0]) + 12 (page offset) = 21 bits that can served as the offset of huge page, thus the size is 2^21^ B = 2 MB.

##### 3. The least space of page tables

For the splitting of 4KB pages, thus it needs 2^10^ pages for the 4MB memory. This process needs three level 2 page tables for the three address spaces, and an additional level 1 page table to manage the level 2 page tables. Therefore we need spaces for 4 page tables, each table contains 2^10^ PTE, which takes 2^12^ $\times$ PTE_size in total.

##### 4. The functionality of `page2kva`

```c
// kern/mm/pmm.h

extern const size_t nbase;
extern struct Page *pages;

#define KADDR(pa)                                                \
    ({                                                           \
        uintptr_t __m_pa = (pa);                                 \
        size_t __m_ppn = PPN(__m_pa);                            \
        if (__m_ppn >= npage) {                                  \
            panic("KADDR called with invalid pa %08lx", __m_pa); \
        }                                                        \
        (void *)(__m_pa + va_pa_offset);                         \
    })

static inline ppn_t page2ppn(struct Page *page) { return page - pages + nbase; }
static inline uintptr_t page2pa(struct Page *page) { return page2ppn(page) << PGSHIFT; }

static inline void *page2kva(struct Page *page) { return KADDR(page2pa(page)); }
```

Given the parameter which is a pointer to Page, it gets the physical address of the page by calling `page2ppn` (get the page number) and `page2pa` (shift the page number). It then take the physical address into the macro `KADDR` to get the corresponding kernel virtual address.  