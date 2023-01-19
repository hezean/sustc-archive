.macro divSCheck(%f1, %f2, %f3)
.text
    addi $sp, $sp, -4
    sw $a1, 0($sp)
    mfc1 $t4, %f3
     
    bnez $t4, f3isnt0
    li $k0, 11
    teqi $zero, 0
    break

    f3isnt0:
    div.s %f1, %f2, %f3
    lw $a1, 0($sp)
    addi $sp, $sp, 4
.end_macro


.kdata
    prom: .asciiz "exception:divisor is 0.0 "
.ktext 0x80000180
    bne $k0, 11, nothishandler
    li $v0, 4
    la $a0, prom
    syscall
    
    nothishandler:
    mfc0 $k0, $14
    addi $k0, $k0, 4
    mtc0 $k0, $14
    eret
    

# do not submit
.text
li $v0,6
syscall
mov.s $f20,$f0
li $v0,6
syscall
mov.s $f21,$f0 
divSCheck($f12,$f20,$f21)
li $v0,2
syscall
li $v0,10
syscall

