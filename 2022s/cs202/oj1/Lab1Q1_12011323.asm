.data:
    prompt_bit: .asciiz "Its binary bit-width is "
    prompt_hex: .asciiz ", its number of hexadecimal digits in hexadecimal is "

.text:
main:
    li $v0, 5  # read integer to $v0
    syscall

    move $s0, $v0

    li $t0, 0  # $t0: shift right times
    move $t1, $s0  # $t1: integer to be shifted

    check_bin:
        sltu $t2, $zero, $t1  # $t2: $t1 != 0
        beq $t2, $zero, bin_fin  # finish shifting

        addi $t0, $t0, 1
        srl $t1, $t1, 1
        j check_bin

    bin_fin:    
        li $v0, 4  # print prompt
        la $a0, prompt_bit
        syscall

        li $v0, 1  # print int (bin-width)
        move $a0, $t0
        syscall

        li $t0, 0  # $t0: shift right times
        move $t1, $s0  # $t1: integer to be shifted

    check_hex:
        sltu $t2, $zero, $t1  # $t2: $t1 != 0
        beq $t2, $zero, hex_fin  # finish shifting

        addi $t0, $t0, 1
        srl $t1, $t1, 4
        j check_hex

    hex_fin:    
        li $v0, 4  # print prompt
        la $a0, prompt_hex
        syscall

        li $v0, 1  # print int (hex-width)
        move $a0, $t0
        syscall

    li $v0, 10
    syscall
