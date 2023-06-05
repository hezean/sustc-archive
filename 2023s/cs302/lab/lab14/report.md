# CS302 Lab14 Report

> 何泽安 12011323
>
> 2023.5.17

##### 1.  手册第二部分中的inode，如果block_size是4KB，指针大小是4B，一个inode能管理的最大文件大小是多少，写出计算过程。

<img src="report.assets/Image%20from%20Lab14,%20page%203.png" alt="Image from Lab14, page 3" style="zoom: 33%;" />

max size = #blocks pointed by [direct ptr + single indirect ptr + double indirect ptr + triple indirect ptr] * block size

each block could store 4KB / 4B = 1K ptrs

thus: size = (12 + 1K + (1K)^2^ + (1K)^3^) * 4KB = 48KB + 4MB + 4GB + 4TB $\approx$ 4TB

##### 2. SFS中的inode可以管理的最大文件大小是多少，写出计算过程。

```c
/* inode (on disk) */
struct sfs_disk_inode {
    uint32_t size;                                  /* size of the file (in bytes) */
    uint16_t type;                                  /* one of SYS_TYPE_* above */
    uint16_t nlinks;                                /* # of hard links to this file */
    uint32_t blocks;                                /* # of blocks */
    uint32_t direct[SFS_NDIRECT];                   /* direct blocks */
    uint32_t indirect;                              /* indirect blocks */
};
```

SFS的inode类似于UNIX inode，12个direct ptr + 1个single indirect ptr，计算同理：

size = (12 + 1K) * 4KB = 48KB + 4MB

##### 3. SFS中sfs_disk_inode和sfs_disk_entry的关系是什么。

```c
/* inode (on disk) */
struct sfs_disk_inode {
    uint32_t size;                                  /* size of the file (in bytes) */
    uint16_t type;                                  /* one of SYS_TYPE_* above */
    uint16_t nlinks;                                /* # of hard links to this file */
    uint32_t blocks;                                /* # of blocks */
    uint32_t direct[SFS_NDIRECT];                   /* direct blocks */
    uint32_t indirect;                              /* indirect blocks */
};

/* file entry (on disk) */
struct sfs_disk_entry {
    uint32_t ino;                                   /* inode number */
    char name[SFS_MAX_FNAME_LEN + 1];               /* file name */
};
```

inode较为底层，通过访问`sfs_disk_inode`可以获取一个"file"（文件或者文件夹）所包含的数据段存储位置，进而可以读取文件的实际内容。但是显然OS为了human friendly，会允许我们把文件或者文件夹进行命名，这时候`sfs_disk_entry`就可以用来保管这个名字，起到在名字和inode之间转换所需要信息的介质的作用。