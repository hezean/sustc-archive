# CS302 Lab9 Report

> 何泽安 12011323
>
> 2023.4.12

#### 1. Explain the fundamentals of macro `le2page(le, page_link)`.

   ```c
   // kern/mm/memlayout.h
   
   #define le2page(le, member)   \
       to_struct((le), struct Page, member)
   
   
   // libs/defs.h
   
   #define offsetof(type, member)   \
       ((size_t)(&((type *)0)->member))
   
   #define to_struct(ptr, type, member)   \
       ((type *)((char *)(ptr) - offsetof(type, member)))
   ```

Expanding the macro definition of `le2page(le, page_link)`, we get this expression:

   ```c
   (struct Page *) ((char *) le - (size_t) (&((struct Page *) 0)->page_link))
   ```

Having the definition of struct `Page` as:

   ```c
   // kern/mm/memlayout.h
   struct Page {
       int ref;                        // page frame's reference counter
       uint64_t flags;                 // array of flags that describe the status of the page frame
       unsigned int property;          // the num of free block, used in first fit pm manager
       list_entry_t page_link;         // free list link
   };
   ```

We can understand that given the pointer to list_entry entity `page_link`, we can calculate the head address of its corresponding `Page` entity, by substrating the address stored in the pointer with the offset of the `page_link` member from the head. Finally, we cast the type of pointer to `struct Page *`.

To further explain the fundamental of `offsetof`, it first casts `0` as the pointer of type `Page`, when accessing the member, the compiler will calculate the correct offset and add it to the base address, which is stored in the "pointer" (in this case, it is 0), then we can use `&` to get the address `0 + offset = offset`. This calculation is done during compiling.



#### 2. Describe the functionality and implementation of `default_alloc_pages` and `default_free_pages`.

- `default_alloc_pages` accepts an unsigned integer `n` as the parameter, as the desired number of continuous pages. It will find and return a pointer to the page that has `n-1` continuous pages just following it. It also manages the linked list of free pages, removing the pages just allocated out.

   ```c
   // kern/mm/default_pmm.h
   
   /* free_area_t - maintains a doubly linked list to record free (unused) pages */
   typedef struct {
       list_entry_t free_list;         // the list header
       unsigned int nr_free;           // number of free pages in this free list
   } free_area_t;
   
   // if this bit=1: the Page is the head page of a free memory block(contains some continuous_addrress pages), and can be used in alloc_pages; if this bit=0: if the Page is the the head page of a free memory block, then this Page and the memory block is alloced. Or this Page isn't the head page.
   // 「目前代码尚未涉及进程的运行，因此分配的方式暂时只是将分配页面打上”已用“的标记」 from the lab tutorial
   #define PG_property  1
   
   #define SetPageProperty(page)       set_bit(PG_property, &((page)->flags))
   #define ClearPageProperty(page)     clear_bit(PG_property, &((page)->flags))
   
   // kern/mm/default_pmm.c
   
   free_area_t free_area;  // this global variable stores a double linked list of the free pages
   #define nr_free (free_area.nr_free)  // nr_free can speed up the checkings in some cases
   
   static struct Page * default_alloc_pages(size_t n) {
       // 1. check if the alloc request is valid, and it's possible to allocate `n` pages
       assert(n > 0);
       if (n > nr_free) {
           // speed up trick: it's not possible to find a valid sequence of continuous pages, directlly return NULL
           return NULL;
       }
     
       // 2. using first-fit policy, iterate through the linked list
       struct Page *page = NULL;
       list_entry_t *le = &free_list;
       while ((le = list_next(le)) != &free_list) {
           struct Page *p = le2page(le, page_link);
           if (p->property >= n) {
               // there are not less than n continuous pages from this page, so we will give it out as the head address of successfully allocated out memory
               page = p;
               break;
           }
       }
     
       // 3. manage the linked list and nr_free, remove the allocated out pages, mark the page property bit
       if (page != NULL) {
           list_entry_t* prev = list_prev(&(page->page_link));
           list_del(&(page->page_link));
           if (page->property > n) {
               struct Page *p = page + n;
               p->property = page->property - n;
               SetPageProperty(p);
               list_add(prev, &(p->page_link));
           }
           nr_free -= n;
           ClearPageProperty(page);
       }
       return page;
   }
   ```

- We need to pass the head page of the memory to be freed, and the number of continuous pages as the size to be freed to the function `default_free_pages`. It sets the `PG_property` mark for the `n` pages from the `base` (release / mark as free), and update the free_list and nr_free.

```c
static void default_free_pages(struct Page *base, size_t n) {
    // 1. validate the free request's param, and update the flages
    assert(n > 0);
    struct Page *p = base;
    for (; p != base + n; p ++) {
        assert(!PageReserved(p) && !PageProperty(p));
        p->flags = 0;
        set_page_ref(p, 0);
    }
    base->property = n;
    SetPageProperty(base);
    nr_free += n;

    // 2. place back the page info into the linked list, should put it to the tail of the first page that is lower than `base`, so that we can merge the page info in the next step
    if (list_empty(&free_list)) {
        list_add(&free_list, &(base->page_link));
    } else {
        list_entry_t* le = &free_list;
        while ((le = list_next(le)) != &free_list) {
            struct Page* page = le2page(le, page_link);
            if (base < page) {
                list_add_before(le, &(base->page_link));
                break;
            } else if (list_next(le) == &free_list) {
                list_add(le, &(base->page_link));
            }
        }
    }

    // 3. merge the continuous free page info to one, this can speed up the alloc
    list_entry_t* le = list_prev(&(base->page_link));
    if (le != &free_list) {
        p = le2page(le, page_link);
        if (p + p->property == base) {
            p->property += base->property;
            ClearPageProperty(base);
            list_del(&(base->page_link));
            base = p;
        }
    }

    le = list_next(&(base->page_link));
    if (le != &free_list) {
        p = le2page(le, page_link);
        if (base + base->property == p) {
            base->property += p->property;
            ClearPageProperty(p);
            list_del(&(p->page_link));
        }
    }
}
```

