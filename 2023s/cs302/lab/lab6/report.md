# CS302 Lab6 Report

> 何泽安 12011323
>
> 2023.3.22

1. How to enter U mode from S mode?

   Things happens in the `user_main` function, we can notice the used macro contains a call to `kernel_execve` function.

   ```c
   #define __KERNEL_EXECVE(name, binary, size) ({                          \
               cprintf("kernel_execve: pid = %d, name = \"%s\".\n",        \
                       current->pid, name);                                \
               kernel_execve(name, binary, (size_t)(size));                \
           })
   
   #define KERNEL_EXECVE(x) ({                                             \
               extern unsigned char _binary_obj___user_##x##_out_start[],  \
                   _binary_obj___user_##x##_out_size[];                    \
               __KERNEL_EXECVE(#x, _binary_obj___user_##x##_out_start,     \
                               _binary_obj___user_##x##_out_size);         \
           })
   
   static int user_main(void *arg) {
       KERNEL_EXECVE(hello);
       panic("user_main execve failed.\n");
   }
   ```

   We then discuss the asm code in `kernel_execve`. Here a `ebreak` is used to enter the trap, and do the `SYS_exec` syscall. Under `syscall.c`, we can find the registered handler `sys_exec` calls the function `do_execve`.
   ```c
   static int kernel_execve(const char *name, unsigned char *binary, size_t size) {
       int64_t ret=0, len = strlen(name);
       asm volatile(
           "li a0, %1\n"
           "lw a1, %2\n"
           "lw a2, %3\n"
           "lw a3, %4\n"
           "lw a4, %5\n"
       	  "li a7, 10\n"
           "ebreak\n"
           "sw a0, %0\n"
           : "=m"(ret)
           : "i"(SYS_exec), "m"(name), "m"(len), "m"(binary), "m"(size)
           : "memory");
       return ret;
   }
   ```

   We notice the `load_icode` call sets the trap frame's sstatus register's SPP bit into 0, which leads the `sret` instruction will "recover" the permission mode to U mode.

   ```c
   int do_execve(const char *name, size_t len, unsigned char *binary, size_t size) {
       ...
       if ((ret = load_icode(binary, size)) != 0) {
           goto execve_exit;
       }
       ...
   }
   
   static int load_icode(unsigned char *binary, size_t size) {
       ...
       //(6) setup trapframe for user environment
       struct trapframe *tf = current->tf;
       // Keep sstatus
       uintptr_t sstatus = tf->status;
       memset(tf, 0, sizeof(struct trapframe));
       tf->gpr.sp = USTACKTOP;
       tf->epc = elf->e_entry;
       tf->status = sstatus & ~(SSTATUS_SPP | SSTATUS_SPIE);
       ...
   }
   ```

   

1. How to do syscall in a user process?

   The libraries are (often) the high level abstractions of syscalls that OS provides to the user process, for instance, the 7 syscalls in UCore, which are then the wrapper of this function:

   ```c
   static inline int
   syscall(int64_t num, ...) {
       va_list ap;
       va_start(ap, num);
       uint64_t a[MAX_ARGS];
       int i, ret;
       for (i = 0; i < MAX_ARGS; i ++) {
           a[i] = va_arg(ap, uint64_t);
       }
       va_end(ap);
   
       asm volatile (
           "ld a0, %1\n"
           "ld a1, %2\n"
           "ld a2, %3\n"
           "ld a3, %4\n"
           "ld a4, %5\n"
         	"ld a5, %6\n"
           "ecall\n"
           "sd a0, %0"
           : "=m" (ret)
           : "m"(num), "m"(a[0]), "m"(a[1]), "m"(a[2]), "m"(a[3]), "m"(a[4])
           :"memory");
       return ret;
   }
   ```

   We notice the `ecall`, which will help us entering the S mode, and trap for the registered handler.

   

1. What happens after a user process ends? Does the mode change?

   We previously mentioned that entering a user process is using the `do_execve` function, which contains an _exit_ block that calls `do_exit` function, in `do_exit`, we do these jobs (marked in comments):

   ```c
   execve_exit:
       do_exit(ret);
       panic("already exit: %e.\n", ret);
   ```

   ```c
   int do_exit(int error_code) {
       ...
       // release the allocated memory
       struct mm_struct *mm = current->mm;
       if (mm != NULL) {
           lcr3(boot_cr3);
           if (mm_count_dec(mm) == 0) {
               exit_mmap(mm);
               put_pgdir(mm);
               mm_destroy(mm);
           }
           current->mm = NULL;
       }
       // set the process state to PROC_ZOMBIE and error code for the OS to recogination
       current->state = PROC_ZOMBIE;
       current->exit_code = error_code;
       bool intr_flag;
       struct proc_struct *proc;
       local_intr_save(intr_flag);
       {
           proc = current->parent;
           // if the parent is in wait state, wakeup parent process for PCB and kernel stack reclaim
           if (proc->wait_state == WT_CHILD) {
               wakeup_proc(proc);
           }
           // if the process still has children, pass the children to initproc
           while (current->cptr != NULL) {
               proc = current->cptr;
               current->cptr = proc->optr;
       
               proc->yptr = NULL;
               if ((proc->optr = initproc->cptr) != NULL) {
                   initproc->cptr->yptr = proc;
               }
               proc->parent = initproc;
               initproc->cptr = proc;
               if (proc->state == PROC_ZOMBIE) {
                   if (initproc->wait_state == WT_CHILD) {
                       wakeup_proc(initproc);
                   }
               }
           }
       }
       local_intr_restore(intr_flag);
       schedule();  // trap and schedule for other process
       panic("do_exit will not return!! %d.\n", current->pid);
   }
   ```

   We need to know the result of scheduling to judge if the mode needs to be switched. Usually, a mode switch will happen and the OS will do the process management.

   

1. How does the process become a zombie process?

   As the code mentioned in question 3, the resources are released, and the process's state is marked as PROC_ZOMBIE. Additionally, parent process is informed and child process is passed to initproc.