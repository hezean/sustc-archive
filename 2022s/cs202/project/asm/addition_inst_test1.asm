.data 0x0000            
 buf: .word 0x0000
.text 0x0000     
init: 
	lui   $1,0xFFFF			
	ori   $28,$1,0xF000
        addi $2, $0, 2
	addi $1, $0, 6
main:
    sw $1, 0xC60($28)

    m_loop1:
        lw $24, 0xC80($28)
        beq $24, $0, m_loop1
    
    m_loop2:
        lw $24, 0xC80($28)
        bne $24, $0, m_loop2

    div $1, $2
    mflo $3
    sw $3, 0xC60($28)

    m_loop3:
        lw $24, 0xC80($28)
        beq $24, $0, m_loop3
    
    m_loop4:
        lw $24, 0xC80($28)
        bne $24, $0, m_loop4