.data 0x0000				      		
	buf: .word 0x0000
.text 0x0000
init:
	lui   $1,0xFFFF			
	ori   $28,$1,0xF000

read:
    ori $8, $0, 0
    m_loop1:
		lw $8, 0xC80($28)
		beq $8,$0, m_loop1
	
    m_loop2:
		lw $8, 0xC80($28)
		bne $8,$0, m_loop2

    lw $7, 0xC72($28)
    lw $8, 0xC70($28)
    
    beq $7, $0, led
    j light

    led:
        sw $8, 0xC60($28)
	      sw $0, 0xC50($28)
        j read

    light:
	sw $8, 0xC50($28)
	sw $0, 0xC60($28)
        j read
    