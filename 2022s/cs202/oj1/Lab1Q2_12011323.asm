.macro print(%str)
    .data 
        pstr: .asciiz %str
    .text
        li $v0, 4
        la $a0, pstr
        syscall
.end_macro

.text
main:
    li $v0, 5
    syscall

    move $s0, $v0  # $s0 the target dec value

    # print binary
    print("x2: ")
    li $v0, 35
    move $a0, $s0
    syscall
    print("\n")

    move $t2, $s0  # $t2 the target dec value
    li $t4, 2
    li $s3, 0  # revert
    cvt_bin:
        sltu $t3, $zero, $t2  # $t2: $t1 != 0
        beq $t3, $zero, bin_fin  # finish shifting
        div $t2, $t4
            mfhi $t5
            mflo $t2
            sll $s3, $s3, 1
            add $s3, $s3, $t5
        j cvt_bin

    bin_fin:
        print("x2r: ")
        li $v0, 35
        move $a0, $s3
        syscall

    print_hex:
    print("\nx16: ")
    li $v0, 34
    move $a0, $s0
    syscall
    print("\n")

    move $t2, $s0  # $t2 the target dec value
    li $t4, 16
    li $s3, 0  # revert
    cvt_hex:
        sltu $t3, $zero, $t2  # $t2: $t1 != 0
        beq $t3, $zero, hex_fin  # finish shifting
        div $t2, $t4
            mfhi $t5
            mflo $t2
            sll $s3, $s3, 4
            add $s3, $s3, $t5
        j cvt_hex

    hex_fin:
        print("x16r: ")
        li $v0, 34
        move $a0, $s3
        syscall
        
    li $v0, 10
    syscall
