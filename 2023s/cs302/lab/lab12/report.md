# CS302 Lab12 Report

> 何泽安 12011323
>
> 2023.5.6

##### 1. Explain the functionality of `local_intr_save(intr_flag)`

This function is called before entering the critical zone, it can handle the problem of kernel synchronization mutexes by masking to enable interrupts (屏蔽使能中断).

```c
// kern/sync/sync.h

#define local_intr_save(x)      do { x = __intr_save(); } while (0)

static inline bool __intr_save(void) {
    if (read_csr(sstatus) & SSTATUS_SIE) {
        intr_disable();
        return 1;
    }
    return 0;
}
```

By checking the SIE bit in the CSR register and executing the intr_disable when the interruption is enabled, it ensures that the current code execution will not be interrupted by any interruptions, guaranteeing the reliability and stability of the code. Note that disabling interruption won't affect the exceptions.



##### 2. Deadlock and the final solution of the philosopher sync problem

```c
int philosopher_using_semaphore(void * arg) 
{
    int i, iter=0;
    i=(int)arg;
    cprintf("I am No.%d philosopher_sema\n",i);
    while(iter++<TIMES) { 
        cprintf("Iter %d, No.%d philosopher_sema is thinking\n",iter,i); 
        do_sleep(SLEEP_TIME);
        phi_take_forks_sema(i); 
        cprintf("Iter %d, No.%d philosopher_sema is eating\n",iter,i); 
        do_sleep(SLEEP_TIME);
        phi_put_forks_sema(i); 
    }
    cprintf("No.%d philosopher_sema quit\n",i);
    return 0;    
}

void check_sync(void){
    int i;
    sem_init(&mutex, 1);  // init a mutex lock that accepts only 1 process at one time
    for(i=0; i<N; i++) {
        sem_init(&s[i], 1);  // each philopher also holds another mutex lock
        int pid = kernel_thread(philosopher_using_semaphore, (void *)i, 0);
        if (pid <= 0) { panic("create No.%d philosopher_using_semaphore failed.\n"); }
        philosopher_proc_sema[i] = find_proc(pid);
        set_proc_name(philosopher_proc_sema[i], "philosopher_sema_proc");
    }
}
```

###### 2.1 Part 1 algorithm

```c
void phi_take_forks_sema(int i) { 
        down(&mutex);  // acquire the mutex to ensure that only one philosopher can access the shared forks array at a time
        down(&s[i]);  // acquire his left fork before he can eat
        down(&s[RIGHT]);  // acquire his right fork before he can eat
        up(&mutex);  // release the mutex to allow other philosophers to access the forks array
}

void phi_put_forks_sema(int i) { 
        up(&s[RIGHT]);  // after eating, put down (release) his right fork
        up(&s[i]);  // after eating, put down (release) his left fork
}
```

<img src="report.assets/Screenshot%202023-05-06%20at%2017.30.56.png" alt="Screenshot 2023-05-06 at 17.30.56" style="zoom:50%;" />

> Above shows a case that the problem is stucked

The above solution uses one semaphore for each fork, and each philosopher may operate on the two semaphores next to him, which does not completely avoid deadlocks. In some cases, for example, a deadlock may occur if the philosophers try to pick up the left fork and wait for the right fork at the same time, e.g., in the case of N=5, if philosophers 1, 3, and 5 pick up the left fork and wait for the right fork one after another, a deadlock will occur because philosophers 2 and 4 have already picked up the right fork and wait for the left fork, and the forks they picked up are occupied by other philosophers.

Therefore, to avoid deadlocks, it is also necessary to introduce some notion of randomness or priority to avoid all philosophers trying to pick up the same fork at the same time. Common solutions include letting philosophers choose the order of picking up forks randomly, or assigning a priority to each philosopher, with the philosopher with higher priority picking up the fork first to dine, to ensure the full utilization of resources.

###### 2.2 Final solution

<img src="report.assets/Screenshot%202023-05-06%20at%2017.41.11.png" alt="Screenshot 2023-05-06 at 17.41.11" style="zoom:30%;" />

refering to the above slides:![Screenshot 2023-05-06 at 18.41.27](report.assets/Screenshot%202023-05-06%20at%2018.41.27.png)

![Screenshot 2023-05-06 at 18.41.08](report.assets/Screenshot%202023-05-06%20at%2018.41.08.png)
