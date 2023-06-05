# CS302 Assignment 5

> 何泽安 12011323
>
> 2023.4.19

#### 1. Merging free blocks

Added a code fragment at the end of `default_free_pages` function:

```c
static void
default_free_pages(struct Page *base, size_t n) {
    ...

    //-----------------------合并空闲块--------------------

    // the linked list should be merged before this insertion, so we can focus on the entry we just inserted
    // 1. check if can be merged into the previous tail
    list_entry_t *le = list_prev(&(base->page_link));
    if (le != &free_list) {
        struct Page *res = le2page(le, page_link);
        if (res + res->property == base) {
            res->property += base->property;               // modify p's property
            ClearPageProperty(base);                       // clear base's property
            list_del(&(base->page_link));                  // update the free_list
            base = res;  // now we change to consider the merged as the new base to be checked
        }
    }

    // 2. check if the current base can be merged with the next head
    le = list_next(&(base->page_link));
    if (le != &free_list) {
        struct Page *res = le2page(le, page_link);
        if (base + base->property == res) {
            base->property += res->property;               // modify p's property
            ClearPageProperty(res);                        // clear res's property
            list_del(&(res->page_link));                   // update the free_list
        }
    }

    //---------------------------------------------------
}
```

![Screenshot 2023-04-20 at 11.11.52](report.assets/Screenshot%202023-04-20%20at%2011.11.52.png)

After enabling the checker, the test passed:

```c
const struct pmm_manager default_pmm_manager = {
    .name = "default_pmm_manager",
    .init = default_init,
    .init_memmap = default_init_memmap,
    .alloc_pages = default_alloc_pages,
    .free_pages = default_free_pages,
    .nr_free_pages = default_nr_free_pages,
    // .check = default_check,
    // 合并空闲块之后，请将上面的check注释，下面的check解除注释，进行测试
    .check = firstfit_check_final,
};
```

![Screenshot 2023-04-19 at 22.19.59](report.assets/Screenshot%202023-04-19%20at%2022.19.59.png)

#### 2. Best Fit PMM

Fundamentally, the only difference between `best_fit_pmm` and `default_pmm` (first-fit) is their policy of allocating pages, which is implemented in the `struct Page *xxx_alloc_pages(size_t n)` function. Therefore, the other functions, including `init`, `init_memmap`, `free_pages`, `nr_free_pages` are totally the same as the ones in `default_pmm`.

```c
static void best_fit_init(void) {
    list_init(&free_list);
    nr_free = 0;
}

static void best_fit_init_memmap(struct Page *base, size_t n) {
    assert(n > 0);
    struct Page *p = base;
    for (; p != base + n; p ++) {
        assert(PageReserved(p));
        p->flags = p->property = 0;
        set_page_ref(p, 0);
    }
    base->property = n;
    SetPageProperty(base);
    nr_free += n;
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
}

static struct Page *best_fit_alloc_pages(size_t n) {
    assert(n > 0);
    if (n > nr_free) {
        return NULL;
    }
    
    struct Page *page = NULL;
    size_t best_fit_size = 0;  // having assert(n > 0) above, we can use 0 as the empty flag

    list_entry_t *le = &free_list;
    while ((le = list_next(le)) != &free_list) {
        struct Page *p = le2page(le, page_link);
        // best fit policy
        if (p->property >= n && (p->property < best_fit_size || best_fit_size == 0)) {
            page = p;
            best_fit_size = p->property;
        }
    }
    
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

static void best_fit_free_pages(struct Page *base, size_t n) {
    assert(n > 0);
    struct Page *p = base;
    for (; p != base + n; p ++) {
        assert(!PageReserved(p) && !PageProperty(p));
        p->flags = 0;
        set_page_ref(p, 0);
    }
    base->property = n;
    SetPageProperty(base);
    nr_free += n;  // okay

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

    // the linked list should be merged before this insertion, so we can forcus on the entry we just inserted
    // 1. check if can be merged to the previous tail
    list_entry_t *le = list_prev(&(base->page_link));
    if (le != &free_list) {
        struct Page *res = le2page(le, page_link);
        if (res + res->property == base) {
            res->property += base->property;               // modify p's property
            ClearPageProperty(base);                       // clear base's property
            list_del(&(base->page_link));                  // update the free_list
            base = res;  // now we change consider the merged as the new base to be checked
        }
    }

    // 2. check if the current base can be merged to the next head
    le = list_next(&(base->page_link));
    if (le != &free_list) {
        struct Page *res = le2page(le, page_link);
        if (base + base->property == res) {
            base->property += res->property;               // modify p's property
            ClearPageProperty(res);                        // clear res's property
            list_del(&(res->page_link));                   // update the free_list
        }
    }
}

static size_t best_fit_nr_free_pages(void) {
    return nr_free;
}
```

![Screenshot 2023-04-20 at 11.19.27](report.assets/Screenshot%202023-04-20%20at%2011.19.27.png)

With the `best_fit_check` checker, and changing the PMM to `best_fit_pmm`, we also pass the test:

```c
// kern/mm/pmm.c

static void init_pmm_manager(void) {
    // pmm_manager = &default_pmm_manager;
    pmm_manager = &best_fit_pmm_manager;
    cprintf("memory management: %s\n", pmm_manager->name);
    pmm_manager->init();
}
```

![Screenshot 2023-04-19 at 23.36.50](report.assets/Screenshot%202023-04-19%20at%2023.36.50.png)