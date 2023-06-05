# CS302 Assignment 8

> 何泽安 12011323
>
> 2023.5.10

#### 1. I/O

###### (1) What are the pros and cons of polling and interrupt-based I/O?

- Polling
  - Pros
    - Being simple and working.
  - Cons
    - Inefficient and inconvenient: as it has to wait for the (potentially slow) device to complete its activity, it wastes a great deal of CPU time, also, if CPU switches to other tasks, data may be overwritten.
- Interrupt-based
  - Pros
    - Better utilize the CPU by allowing overlap of computation and I/O: when one process is waiting for a time-costing slow I/O task, CPU can use the time to run another process, instead of wasting the CPU clocks on the polling queries.
  - Cons
    - Extra overhead when handling the interruption / context switch, which, in some cases that such as the device is fast, may cause the overall performance slower than polling.
    - Livelock (an example can be receiving the network packages) may makes OS spending a lot time on handling the interruptions, which lets user level processes have no chance to execute.
    - It's implementation is more complex than polling.

###### (2) What are the diﬀerences between PIO and DMA?

- CPU
  - PIO: CPU is in charge of the data transfer operation, in particular, when using PIO to transfer a large chunk of data to a device, the CPU is once again overburdened with a rather trivial task, and thus wastes a lot of time and effort that could better be spent running other processes.
  - DMA: It can effectively transfer data between devices and main memory without much CPU intervention, which reduces CPU's cost and increases the data transfer speed, gives a better performance.
- Hardware support
  - PIO: Does not need extra hardware support, it works on the most basic canonical device as it's simply a protocol that runs on CPU.
  - DMA: Requires DMA controller, DMA channel, cache, etc., to support the data transfer.
- Scope of application
  - PIO: Per previous analysis on the performance (in the section of CPU), PIO generally suitable for cases where only a small amount of data needs to be transferred, such as transferring small files or data blocks.
  - DMA: With the advanced hardware support and the advantages, it is suitable for cases where a large amount of data needs to be transferred, such as network transmission and video processing.

###### (3) How to protect memory-mapped I/O and explicit I/O instructions from being abused by malicious user process?

- Memory-mapped I/O
  - OS participates the process of accessing device registers, and to access such registers, OS will issue load / store instructions to the address, during which, OS can do some checks and protech the memory from malicious user process.
- Explicit I/O
  - Most explicit I/O instructions are designed privileged, letting only OS itself (which is definitely "trusted") could directly communicate with the I/O devices.



#### 2. Condition variable

###### Design Idea

Referring to the material of lab 8:

| Function            | Description                                                  |
| ------------------- | ------------------------------------------------------------ |
| pthread_cond_wait   | Release lock, put thread to sleep until condition is signaled; when thread wakes up again, re-acquire lock before returning. |
| pthread_cond_signal | Wake up at least one of the threads that are blocked on the speciﬁed condition variable; If more than one thread is blocked on a condition variable, the scheduling policy shall determine the order in which threads are unblocked. |

We conclude that, in some aspects, a condition variable could be seemed as a semaphore with size 0, as they behave similar, that the up and down operations of the a size 0 semaphore can support the signal and wait operations of condition variable.

In that case, we can just wrap a semaphore into the struct condvar, and the cond_wait function could be blocked by a wrapped down operation of the semaphore (note that another mutex is used to ensure that only one process can access the resource by "Release lock...re-acquire lock before returning"), while upping the semaphore in the cond_signal function can let other thread to step over the cond_wait.

###### Implementation

```c
// kern/sync/condvar.h

#ifndef __KERN_SYNC_MONITOR_CONDVAR_H__
#define __KERN_SYNC_MOINTOR_CONDVAR_H__

#include <sem.h>

typedef struct condvar {
    semaphore_t sem;  // <---------
} condvar_t;

void cond_init(condvar_t *cvp);
void cond_signal(condvar_t *cvp);
void cond_wait(condvar_t *cvp, semaphore_t *mutex);

#endif /* !__KERN_SYNC_MONITOR_CONDVAR_H__ */
```

```c
// kern/sync/condvar.c

#include <assert.h>
#include <condvar.h>
#include <kmalloc.h>
#include <stdio.h>

void cond_init(condvar_t *cvp) {
    sem_init(&(cvp->sem), 0);
}

void cond_signal(condvar_t *cvp) {
    up(&(cvp->sem));
}

void cond_wait(condvar_t *cvp, semaphore_t *mutex) {
    up(mutex);
    down(&(cvp->sem));
    down(mutex);
}
```

![Screenshot 2023-05-13 at 00.20.39](report.assets/Screenshot%202023-05-13%20at%2000.20.39.png)

###### Running Result

![Screenshot 2023-05-13 at 00.14.26](report.assets/Screenshot%202023-05-13%20at%2000.14.26.png)
