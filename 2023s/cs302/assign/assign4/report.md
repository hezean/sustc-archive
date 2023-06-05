# CS302 Assignment 4

> 何泽安 12011323
>
> 2023.4.4

#### 1. CPU Scheduling

|         Time          | HRRN | FIFO/FCFS |  RR  | SJF  | Priority |
| :-------------------: | :--: | :-------: | :--: | :--: | :------: |
|           1           |  A   |     A     |  A   |  A   |    A     |
|           2           |  A   |     A     |  A   |  A   |    B     |
|           3           |  A   |     A     |  B   |  A   |    A     |
|           4           |  A   |     A     |  A   |  A   |    D     |
|           5           |  B   |     B     |  D   |  B   |    D     |
|           6           |  D   |     D     |  A   |  D   |    C     |
|           7           |  D   |     D     |  C   |  D   |    C     |
|           8           |  C   |     C     |  D   |  C   |    C     |
|           9           |  C   |     C     |  C   |  C   |    A     |
|          10           |  C   |     C     |  C   |  C   |    A     |
| Avg. Turn-around Time | 4.5  |    4.5    | 4.75 | 4.5  |   4.25   |



#### 2. Preemptive Process Scheduling

##### 2-1. Design Idea

We will first need to expose a syscall for the process to initiatively change its _good value_ stored in PCB.

As the preemptive scheduler won't use timer break, we can do a schedule each time when the current running process changes its *good value* (we may use a `do_yield` after changing the value, in the syscall).

In the scheduling pick-next-policy, we go through the process list and return the first process with highest _good value_ as result.

See the comments in Part. 2-3 below for more details.

##### 2-2. Running Sequence Analysis

As we have disabled the timer interrupt, this scheduler only do the schedule when a `set_good` is called. Therefore the process to run may only changes when the current process changes the good value -- we cannot use a time sequence table like Q. 1 to show the running sequence, instead, notice that the `set_good` is called after each 200000 spin delays, let's take it as the "time unit".

```c
int goods[TOTAL] = {3, 1, 4, 5, 2};
for (i = 0; i < TOTAL; i++) {
  acc[i] = 0;
  if ((pids[i] = fork()) == 0) {
    acc[i] = 0;
    while (1) {
      spin_delay();
      ++acc[i];
      if (acc[i] == 200000)  set_good(goods[i]);
      if (acc[i] > 4000000)  exit(acc[i]);
    }
  }
}
```

| Time / 200000 spins | PID=3  | PID=4 | PID=5  | PID=6  | PID=7  | Chosen Process to Run |
| :-----------------: | :----: | :---: | :----: | :----: | :----: | :-------------------: |
|          0          |   6    |   6   |   6    |   6    |   6    |           3           |
|          1          |   3    |   6   |   6    |   6    |   6    |           4           |
|          2          |   3    |   1   |   6    |   6    |   6    |           5           |
|          3          |   3    |   1   |   4    |   6    |   6    |           6           |
|          4          |   3    |   1   |   4    |   5    |   6    |           7           |
|          5          |   3    |   1   |   4    |   5    |   2    |           6           |
|          6          |   3    |   1   |   4    | exited |   2    |           5           |
|          7          |   3    |   1   | exited | exited |   2    |           3           |
|          8          | exited |   1   | exited | exited |   2    |           7           |
|          9          | exited |   1   | exited | exited | exited |           4           |

##### 2-3. Modified Code

We first finish the three easy tasks mentioned in the problem description:

```c
/*** 1. modify the entrance first ***/
// kern/process/proc.c
static int user_main(void *arg) {
#ifdef TEST
    KERNEL_EXECVE2(TEST, TESTSTART, TESTSIZE);
#else
    KERNEL_EXECVE(ex3);   //<---- change `rr` to `ex3`
#endif
    panic("user_main execve failed.\n");
}

/*** 2. uncomment the main function in `user/ex3.c` ***/

/*** 3. disable the clock interrupt, making sure the scheduling is purely preemptive ***/
// kern/init/init.c
int kern_init(void) {
    ...
    // clock_init();   //<---- deleted: if we do not init the first timer, no timer interrupt will happen later
    ...
}
```

Then expose a syscall interface (and implement it) to set the good value, as we find there's a function all `set_good(int)` in the main function of `ex3.c`, we rationally define a library function that wraps the syscall in `ulib`:

```c
/*** 4. declare the wrapper lib function `set_good(int)` ***/
// user/libs/ulib.h
void set_good(int);   //<---- added

/*** 5. implement the wrapper lib function `set_good(int)` ***/
// user/libs/ulib.c
void set_good(int good) {
    cprintf("set good to %d\n", good);
    sys_set_good(good);
}

/*** 6. implement the syscall function ***/
// user/libs/syscall.h
int sys_set_good(int64_t good);

// user/libs/syscall.c
int sys_set_good(int64_t good) {
    return syscall(SYS_set_good, good);   //<---- newly defined syscall number (below)
}

// libs/unistd.h
#define SYS_set_good        32

// kern/syscall/syscall.c
static int sys_set_good(uint64_t arg[]) {
    int good = (int) arg[0];
    current->labschedule_good = good;
    do_yield();
}

static int (*syscalls[])(uint64_t arg[]) = {
    ...
    [SYS_set_good]          sys_set_good,   //<---- register the syscall impl
};
```

Finally, we need to implement the scheduling policy.

```c
/*** 7. define `pick_next` policy and set it for the default scheduler ***/
// kern/schedule/default_sched.c
static struct proc_struct* GOOD_pick_next(struct run_queue *rq) {
    // new function modified based on RR_pick_next
    int best_val = -1;
    list_entry_t *res = NULL;

    list_entry_t *le = list_next(&(rq->run_list));
    while (le != &(rq->run_list)) {
        int good_val = le2proc(le, run_link)->labschedule_good;
        if (good_val > best_val) {
            best_val = good_val;
            res = le;
        }
        le = list_next(le);
    }

    if (res != &(rq->run_list)) {
        return le2proc(res, run_link);
    }
    return NULL;
}

struct sched_class default_sched_class = {
    .name = "GOOD_scheduler",
    .init = RR_init,
    .enqueue = RR_enqueue,
    .dequeue = RR_dequeue,
    // .pick_next = RR_pick_next,
    .pick_next = GOOD_pick_next,   //<---- modified
    .proc_tick = RR_proc_tick,
};
```

##### 2-4. Running Result

![Screenshot 2023-04-07 at 16.33.28](report.assets/Screenshot%202023-04-07%20at%2016.33.28.png)

---

**Modified Codes' Screenshot for Part. 2-3**

<img src="report.assets/Screenshot%202023-04-07%20at%2016.45.12.png" alt="Screenshot 2023-04-07 at 16.45.12" style="zoom:25%;" />

<img src="report.assets/Screenshot%202023-04-07%20at%2016.44.53.png" alt="Screenshot 2023-04-07 at 16.44.53" style="zoom:25%;" />

<img src="report.assets/Screenshot%202023-04-07%20at%2016.45.51.png" alt="Screenshot 2023-04-07 at 16.45.51" style="zoom:25%;" />

<img src="report.assets/Screenshot%202023-04-07%20at%2016.46.01.png" alt="Screenshot 2023-04-07 at 16.46.01" style="zoom:25%;" />

<img src="report.assets/Screenshot%202023-04-07%20at%2016.46.09.png" alt="Screenshot 2023-04-07 at 16.46.09" style="zoom:25%;" />

<img src="report.assets/Screenshot%202023-04-07%20at%2016.46.20.png" alt="Screenshot 2023-04-07 at 16.46.20" style="zoom:25%;" />

<img src="report.assets/Screenshot%202023-04-07%20at%2016.46.28.png" alt="Screenshot 2023-04-07 at 16.46.28" style="zoom:25%;" />

<img src="report.assets/Screenshot%202023-04-07%20at%2016.46.37.png" alt="Screenshot 2023-04-07 at 16.46.37" style="zoom:25%;" />

<img src="report.assets/Screenshot%202023-04-07%20at%2016.46.46.png" alt="Screenshot 2023-04-07 at 16.46.46" style="zoom: 25%;" />
