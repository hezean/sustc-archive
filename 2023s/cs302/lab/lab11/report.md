# CS302 Lab11 Report

> 何泽安 12011323
>
> 2023.4.26

##### 1. How many `mm_struct` does a process own? The functionality of `mm_struct`?

As the control struct for a set of VMA using the same page table, one process only has **one** `mm_struct`. It is used to keep track of a process's memory mappings and other memory-related information. It contains fields (counter, pointer, etc) that describe, for instance, the process's VMA and its associated page tables.

##### 2. The functionality of `vma_struct`?

`vma_struct` is used to record the corresponding information of a VMA, such as the start, end, and flags (that are related to the memory protection mechanism). It has a pointer to the `mm_struct` of its corresponding process and a list_entry accessing the linked list that can be helpful for managing the VMAs. It also provides quick access to the physical page of a virtual address.

##### 3. Under what condition will a missing page interruption be triggered?

<img src="report.assets/Image%20from%20Lab11,%20page%203.png" alt="Image from Lab11, page 3" style="zoom: 50%;" />

When CPU tries to access a VA and cannot translate into the designated, legal physical address, the page fault is raised. The above diagram shows the three possible kinds of missing page interruption.

- *Major page fault*: the accessing VA is not present in the memory, and it needs to be loaded from other places such as swap space.
- *Minor page fault*: the VA is not mapped in the page table, which typically happens when manipulating the allocated memory for the first time.
- *Invalid page fault*: the translated address is illegal (not persistent in VMA).

##### 4. How is the major page fault handled?

When CPU detects a page fault, it will put the cause of the exception in the `scause` register and the corresponding virtual address in the `badvaddr` (say, `stval`) register. Then we step into the exception handler:

```c
// kern/trap/trap.c

void exception_handler(struct trapframe *tf) {
    int ret;
    switch (tf->cause) {
        case CAUSE_LOAD_PAGE_FAULT:
            cprintf("Load page fault\n");
            if ((ret = pgfault_handler(tf)) != 0) {
                print_trapframe(tf);
                panic("handle pgfault failed. %e\n", ret);
            }
            break;
        case CAUSE_STORE_PAGE_FAULT:
            cprintf("Store/AMO page fault\n");
            if ((ret = pgfault_handler(tf)) != 0) {
                print_trapframe(tf);
                panic("handle pgfault failed. %e\n", ret);
            }
            break;
        // other cases are omitted
    }
}

static int pgfault_handler(struct trapframe *tf) {
    extern struct mm_struct *check_mm_struct;
    print_pgfault(tf);  // print info
    if (check_mm_struct != NULL) {
        return do_pgfault(check_mm_struct, tf->cause, tf->badvaddr);
    }
    panic("unhandled page fault.\n");
}
```

We can see there are 4 page fault related causes that will be handled in the `pgfault_handler`, which wraps a few check and info output, then pass the stored cause and virtual address into the actual function `do_pgfault`:

```c
// kern/mm/vmm.c

int do_pgfault(struct mm_struct *mm, uint_t error_code, uintptr_t addr) {
    // ...
    if (*ptep == 0) { /* ... */ } else {
        if (swap_init_ok) {
            struct Page *page = NULL;
            swap_in(mm, addr, &page);  //According to the mm AND addr, try
                                       //to load the content of right disk page
                                       //    into the memory which page managed.
            page_insert(mm->pgdir, page, addr, perm);  //According to the mm,
                                                   //addr AND page, setup the
                                                   //map of phy addr <--->
                                                   //logical addr
            swap_map_swappable(mm, addr, page, 1);  //make the page swappable.
            page->pra_vaddr = addr;
        } else {
            cprintf("no swap_init_ok but ptep is %x, failed\n", *ptep);
            goto failed;
        }
   }
   // ...
}
```

When there is a major page fault, a physical page is first allocated. Then through the corresponding PTE we can get the information about its address in swap space, which is fetched to the page and update the PTE which maps the VA with the newly allocated page and make the page swappable.
