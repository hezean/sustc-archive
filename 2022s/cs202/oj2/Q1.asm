.data
    one: .double 1
    
.text
main:
    li $v0, 7
    syscall
    mov.d $f4, $f0
    
    ldc1 $f6, one  # this turn to add
    ldc1 $f10, one  # turn idx
    ldc1 $f8, one  # sum
    ldc1 $f20, one  # const 1.
    
turn:
    c.lt.d $f6, $f4
    bc1t ans

        add.d $f8, $f8, $f6
        add.d $f10, $f10, $f20
        div.d $f6, $f6, $f10

    j turn
    
ans:
    li $v0, 3
    mov.d $f12, $f8
    syscall
    
    li $v0, 10
    syscall