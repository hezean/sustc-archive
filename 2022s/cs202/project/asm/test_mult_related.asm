.data 0x0000            
 buf: .word 0x0000
.text 0x0000     
init: 
	lui   $1,0xFFFF			
	ori   $28,$1,0xF000
main:
    ori $2, $0, 2
    addi $1, $0, -3
  
    m_loop1:
        lw $24, 0xC80($28)
        beq $24, $0, m_loop1
    
    m_loop2:
        lw $24, 0xC80($28)
        bne $24, $0, m_loop2

test_mult:
    mult $2, $1
    mflo $3
    sw $3, 0xC60($28)   # 2 * -3 = -6   lo: ans: 1111 1111 1111 1010

    m_loop3:
        lw $24, 0xC80($28)
        beq $24, $0, m_loop3
    
    m_loop4:
        lw $24, 0xC80($28)
        bne $24, $0, m_loop4

    mfhi $3
    sw $3, 0xC60($28)   # hi: ans£º1111 1111 1111 1111

    m_loop5:
        lw $24, 0xC80($28)
        beq $24, $0, m_loop5
    
    m_loop6:
        lw $24, 0xC80($28)
        bne $24, $0, m_loop6

test_multu:
    multu $2, $1
    mflo $3
    sw $3, 0xC60($28)       # unsigned: lo: ans: 1111 1111 1111 1010

    m_loop7:
        lw $24, 0xC80($28)
        beq $24, $0, m_loop7
    
    m_loop8:
        lw $24, 0xC80($28)
        bne $24, $0, m_loop8

    mfhi $3
    sw $3, 0xC60($28)   # hi: ans: 0000 0000 0000 0001

    m_loop9:
        lw $24, 0xC80($28)
        beq $24, $0, m_loop9
    
    m_loop10:
        lw $24, 0xC80($28)
        bne $24, $0, m_loop10

test_mthi:
    ori $4, $0, 5
    mthi $4
    mfhi $3
    sw $3, 0xC60($28)   # ans 5

    m_loop11:
        lw $24, 0xC80($28)
        beq $24, $0, m_loop11
    
    m_loop12:
        lw $24, 0xC80($28)
        bne $24, $0, m_loop12


test_mtlo:
    ori $4, $0, 7
    mtlo $4
    mflo $3
    sw $3, 0xC60($28)   # ans 7

    m_loop13:
        lw $24, 0xC80($28)
        beq $24, $0, m_loop13
    
    m_loop14:
        lw $24, 0xC80($28)
        bne $24, $0, m_loop14