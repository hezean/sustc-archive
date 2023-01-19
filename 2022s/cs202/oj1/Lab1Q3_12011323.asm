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

    move $t2, $s0  # $t2 the target dec value
    li $t4, 2
    li $s3, 0  # revert bin
    cvt_bin:
        sltu $t3, $zero, $t2  # $t2: $t1 != 0
        beq $t3, $zero, cvt_bin_out  # finish shifting
        div $t2, $t4
            mfhi $t5
            mflo $t2
            sll $s3, $s3, 1
            add $s3, $s3, $t5
        j cvt_bin

    cvt_bin_out:

    move $t2, $s0  # $t2 the target dec value
    li $t4, 16
    li $s4, 0  # revert hex
    cvt_hex:
        sltu $t3, $zero, $t2  # $t2: $t1 != 0
        beq $t3, $zero, cvt_hex_out  # finish shifting
        div $t2, $t4
            mfhi $t5
            mflo $t2
            sll $s4, $s4, 4
            add $s4, $s4, $t5
        j cvt_hex

    cvt_hex_out:


    li $v0, 1
    move $a0, $s0
    syscall

    beq $s0, $s3, is_bp
        print(" is NOT binary palindrome, ")
        j cmp_hex
    is_bp:
        print(" is binary palindrome, ")

    cmp_hex:

    li $v0, 1
    move $a0, $s0
    syscall

    beq $s0, $s4, is_hp
        print(" is NOT hexadecimal palindrome\n")
        j info
    is_hp:
        print(" is hexadecimal palindrome\n")


    info:

    print("x2: ")
    li $v0, 35
    move $a0, $s0
    syscall

    print("\nx2r: ")
    li $v0, 35
    move $a0, $s3
    syscall

    print("\nx16: ")
    li $v0, 34
    move $a0, $s0
    syscall

    print("\nx16r: ")
    li $v0, 34
    move $a0, $s4
    syscall

    li $v0, 10
    syscall
