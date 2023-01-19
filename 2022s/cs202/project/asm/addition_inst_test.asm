.data 0x0000            
 buf: .word 0x0000
.text 0x0000     
init: 
	lui   $1,0xFFFF			
	ori   $28,$1,0xF000
main:
    ori $2, $0, 0
    ori $1, $0, 1
    sw $1, 0xC60($28)

    m_loop1:
        lw $24, 0xC80($28)
        beq $24, $0, m_loop1
    
    m_loop2:
        lw $24, 0xC80($28)
        bne $24, $0, m_loop2

    bgez $1, t1
    j t2

    t1:
	addi $2, $2, 1

    t2:
	ori $1, $0, 0
	bgez $1, t3
	j t4
	
    t3:
	addi $2, $2, 2

    t4:
	ori $1, $0, -1
	blez $1, t5
	j t6

    t5:
	addi $2, $2, 4

    t6:
	ori $1, $0, 1
	blez $1, t7
	j t8

    t7:
	addi $2, $2, 8

    t8:
	sw $2, 0xC60($28)
