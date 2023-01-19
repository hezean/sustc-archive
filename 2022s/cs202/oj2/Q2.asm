.macro print(%str)
    .data 
        pstr: .asciiz %str
    .text
        la $a0,pstr
        li $v0,4
        syscall
.end_macro

.macro print_hex(%num, %sss)

.data
    res: .space 9

.text
    li $t0, 8
    la $t3, res
    addi $t9, $zero, 0
    sb $t9, 0($t3)
    move $t2, %num
    
    beqz $t2, occu
    j loop
    
    occu:
    print(%sss)
    j fin
    
    loop:
        beqz $t0, prt
        rol $t2, $t2, 4
        and $t4, $t2, 0xf
        ble $t4, 9, ge9
        addi $t4, $t4, 87  # number ascii
        j mv
        
        ge9:
            addi $t4, $t4, 48  # starting lowcase

    mv:
        sb $t4, 0($t3)
        addi $t3, $t3, 1
        addi $t0, $t0, -1
        j loop
        
    prt:
    la $t0, res
    movp:
    lb $t1, 0($t0)
    
    bne $t1, 48, nadj
    add $t0, $t0, 1
    j movp
    
    nadj:
        li $v0, 4
        move $a0, $t0
        syscall
    fin:
.end_macro


.text
main:
    li $v0, 5
    syscall
    
    beq $v0, 6, flt
    beq $v0, 7, dbl
    j invalid
    
flt:
    li $v0, 6
    syscall
    
    print("s: ")
    mfc1 $t0, $f0
    srl $t0, $t0, 31
    li $v0, 1
    move $a0, $t0
    syscall
    
    print(", e: 0x")
    mfc1 $t0, $f0
    srl $t0, $t0, 23
    andi $t0, $t0, 0xff
    move $a0, $t0
    print_hex($a0, "00")
    
    print(", f: 0x")
    mfc1 $t0, $f0
    andi $t0, $t0, 0x7fffff
    move $a0, $t0
    print_hex($a0, "00000000")
    
    j end

dbl:
    li $v0, 7
    syscall

    print("s: ")
    mfc1 $t0, $f1
    srl $t0, $t0, 31
    li $v0, 1
    move $a0, $t0
    syscall
        
    print(", e: 0x")
    mfc1 $t0, $f1
    srl $t0, $t0, 20
    andi $t0, $t0, 0x7ff
    move $a0, $t0
    print_hex($a0, "000")

    print(", f: 0x")

   
    .data
    res: .space 17

    .text
    li $t0, 16
    la $t3, res
    addi $t9, $zero, 0
    sb $t9, 0($t3)
    
    mfc1 $t2, $f1  # fix
    andi $t2, 0xfffff

    occu:
    mfc1 $t4, $f0
    bnez $t4, loop
    bnez $t2, loop

    print("0000000000000")
    j fin

    loop:
        beqz $t0, prt
        rol $t2, $t2, 4
        and $t4, $t2, 0xf
        ble $t4, 9, ge9
        addi $t4, $t4, 87  # number ascii
        j mv

        ge9:
            addi $t4, $t4, 48  # starting lowcase

    mv:
        sb $t4, 0($t3)
        addi $t3, $t3, 1
        addi $t0, $t0, -1

        beq $t0, 8, cgggg
        j loop
        cgggg:
        mfc1 $t2, $f0   # fix
        j loop
        
    prt:
    la $t0, res
    movp:
    lb $t1, 0($t0)
    
    bne $t1, 48, nadj
    add $t0, $t0, 1
    j movp
    
    nadj:
        li $v0, 4
        move $a0, $t0
        syscall
    fin:
    j end

invalid:
    print("Invalid float type")

end:
    li $v0, 10
    syscall