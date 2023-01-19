.data 0x0000
    # pass

.text 0x0000
main:
lui   $1,0xFFFF			
ori   $28,$1,0xF000
ori $1, $0, 0
ori $2, $0, 1 
sll $2, $2, 27 #cpi = 2^28 / time
ori $3, $0, 0xFFFF
ori $4, $0, 1

sw $4, 0xC60($28)

  m_loop1:
	  lw $8, 0xC80($28)
		beq $8, $0, m_loop1
	
  m_loop2:
		lw $8, 0xC80($28)
		bne $8, $0, m_loop2

sw $3, 0xC60($28)

  loop:
    addi $1, $1, 1
    bne $1, $2, loop

j main