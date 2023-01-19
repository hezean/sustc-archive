.data 0x0000				      		
	buf: .word 0x0000
.text 0x0000
# sw20 is end signal
main:
	jal init
	#loop to confirm whether case_num completed
		
	ori $3, $zero, 0xFFFF
	sw $3, 0xC62($28)
	sw $0, 0xC60($28)

	#wait for push button
	m_loop1:
		lw $8, 0xC80($28)
		beq $8, $0, m_loop1
	
	m_loop2:
		lw $8, 0xC80($28)
		bne $8, $0, m_loop2

	lw $8, 0xC72($28)
	#get the case_num
	sll $8, $8, 24
	srl $8, $8, 29 
	jal switch
	j Exist
	
#initial all the register
init:
	lui   $1,0xFFFF			
	ori   $28,$1,0xF000
	ori $8, $0, 0
	ori $9, $0, 0
	ori $16, $0, 0
	ori $17, $0, 1
	ori $18, $0, 2
	ori $19, $0, 3
	ori $20, $0, 4
	ori $21, $0, 5
	ori $22, $0, 6
	ori $23, $0, 7
	jr $31
	
switch:
	#select the case
	beq $8, $16, case1
	beq $8, $17, case2
	beq $8, $18, case3
	beq $8, $19, case4
	beq $8, $20, case5
	beq $8, $21, case6
	beq $8, $22, case7
	beq $8, $23, case8
	jr $31
	
#A in $8
case1:
	ori $3, $zero, 1
	sw $3, 0xC62($28)
	
	#wait for push button
	c1_loop1:
		lw $8, 0xC80($28)
		beq $8, $0, c1_loop1
	
	c1_loop2:
		lw $8, 0xC80($28)
		bne $8, $0, c1_loop2

	lw $8, 0xC70($28)
	sll $8, $8, 16
	srl $8, $8, 16
	jal palindrome
	sll $2, $2, 7
	sw $2, 0xC62($28)
	sw $8, 0xC60($28)
	#wait for quit
	c1_loop3:
		lw $8, 0xC80($28)
		beq $8, $0, c1_loop3
	
	c1_loop4:
		lw $8, 0xC80($28)
		bne $8, $0, c1_loop4

	j main
	

case2:
	ori $3, $zero, 2
	sw $3, 0xC62($28)
	

	jal read

	c2_loop1:
		lw $3, 0xC80($28)
		beq $3, $0, c2_loop1
	
	c2_loop2:
		lw $3, 0xC80($28)
		bne $3, $0, c2_loop2

	j main
	
case3:
	ori $3, $zero, 3
	sw $3, 0xC62($28)
	

	jal read
	
	c3_loop1:
		lw $3, 0xC80($28)
		beq $3, $0, c3_loop1
	
	c3_loop2:
		lw $3, 0xC80($28)
		bne $3, $0, c3_loop2
			
	and $8, $8, $9
	sw $8, 0xC60($28)

	c3_loop3:
		lw $3, 0xC80($28)
		beq $3, $0, c3_loop3
	
	c3_loop4:
		lw $3, 0xC80($28)
		bne $3, $0, c3_loop4

	j main
	
case4:
	ori $3, $zero, 4
	sw $3, 0xC62($28)
	

	jal read

	c4_loop1:
		lw $3, 0xC80($28)
		beq $3, $0, c4_loop1
	
	c4_loop2:
		lw $3, 0xC80($28)
		bne $3, $0, c4_loop2
	
	or $8, $8, $9
	sw $8, 0xC60($28)

	c4_loop3:
		lw $3, 0xC80($28)
		beq $3, $0, c4_loop3
	
	c4_loop4:
		lw $3, 0xC80($28)
		bne $3, $0, c4_loop4

	j main
	
case5:
	ori $3, $zero, 5
	sw $3, 0xC62($28)

	jal read

	c5_loop1:
		lw $3, 0xC80($28)
		beq $3, $0, c5_loop1
	
	c5_loop2:
		lw $3, 0xC80($28)
		bne $3, $0, c5_loop2
	
	xor $8, $8, $9
	sw $8, 0xC60($28)

	c5_loop3:
		lw $3, 0xC80($28)
		beq $3, $0, c5_loop3
	
	c5_loop4:
		lw $3, 0xC80($28)
		bne $3, $0, c5_loop4

	j main
	
case6:
	ori $3, $zero, 6
	sw $3, 0xC62($28)

	jal read

	c6_loop1:
		lw $3, 0xC80($28)
		beq $3, $0, c6_loop1
	
	c6_loop2:
		lw $3, 0xC80($28)
		bne $3, $0, c6_loop2

	sllv $8, $8, $9
	sw $8, 0xC60($28) 

	c6_loop3:
		lw $3, 0xC80($28)
		beq $3, $0, c6_loop3
	
	c6_loop4:
		lw $3, 0xC80($28)
		bne $3, $0, c6_loop4

	j main
	
case7:
	ori $3, $zero, 7
	sw $3, 0xC62($28)


	jal read

	c7_loop1:
		lw $3, 0xC80($28)
		beq $3, $0, c7_loop1
	
	c7_loop2:
		lw $3, 0xC80($28)
		bne $3, $0, c7_loop2
	
	sll $8, $8, 16
	srlv $8, $8, $9
	srl $8, $8, 24
	sw $8, 0xC60($28)

	c7_loop3:
		lw $3, 0xC80($28)
		beq $3, $0, c7_loop3
	
	c7_loop4:
		lw $3, 0xC80($28)
		bne $3, $0, c7_loop4

	j main
	
case8:
	ori $3, $zero, 8
	sw $3, 0xC62($28)

	jal read

	c8_loop1:
		lw $3, 0xC80($28)
		beq $3, $0, c8_loop1
	
	c8_loop2:
		lw $3, 0xC80($28)
		bne $3, $0, c8_loop2
	
	sll $8, $8, 16
	srav $8, $8, $9
	srl $8, $8, 24
	sw $8, 0xC60($28)

	c8_loop3:
		lw $3, 0xC80($28)
		beq $3, $0, c8_loop3
	
	c8_loop4:
		lw $3, 0xC80($28)
		bne $3, $0, c8_loop4

	j main

#$2 = 0 if false, = 1 true, the argument in $8
palindrome:
	addi $9, $8, 0
	ori $10, $0, 0 #length
	
	#get input length
	p_loop1:
		srl $9, $9, 1
		addi $10, $10, 1
		bne $9, $0, p_loop1
	
	beq $8, $12, p_true
	ori $12, $0, 0
	
	addi $11, $10, 0
	sll $11, $11, 31
	srl $11, $11, 31
	#check even
	beq $11, $0, even
	srl $11, $10, 1
	addi $11, $11, 1
	j p_loop2

	even:
		srl $11, $10, 1
	
	#get high half
	p_loop2:
		addi $9, $8, 0
		srlv $9, $9, $11
		sll $9, $9, 31
		srl $9, $9, 31
		sll $12, $12, 1
		add $12, $12, $9
		addi $11, $11, 1
		bne $11, $10, p_loop2
	
	#get low half
	srl $11, $10, 1
	ori $14, $zero, 32
	sub $11, $14, $11
	addi $9, $8, 0
	sllv $9, $9, $11
	srlv $13, $9, $11
	#check if equal
	beq $12, $13, p_true
	j p_false
	
	
	p_true:
	ori $2, $zero, 1
	jr $31
	
	p_false:
	ori $2, $zero, 0
	jr $31
		

#A,B in $8, $9, sw19 is the end signal
read:
	ori $24, $zero, 0

	#read A
	r_loop1:
		lw $3, 0xC80($28)
		beq $3, $0, r_loop1
	
	r_loop2:
		lw $3, 0xC80($28)
		bne $3, $0, r_loop2


	lw $8, 0xC70($28)
	sll $8, $8, 16
	srl $8, $8, 16
	sw $8, 0xC60($28)
		
	#read B
	r_loop3:
		lw $3, 0xC80($28)
		beq $3, $0, r_loop3
	
	r_loop4:
		lw $3, 0xC80($28)
		bne $3, $0, r_loop4
	
	lw $9, 0xC70($28)
	sll $9, $9, 16
	srl $9, $9, 16
	sw $9, 0xC60($28)

	ori $24, $zero, 0
	jr $ra	

Exist:
	ori $16, $zero, 0
	ori $17, $zero, 0
	ori $18, $zero, 0
	ori $19, $zero, 0
	ori $20, $zero, 0
	ori $21, $zero, 0
	ori $22, $zero, 0
	ori $23, $zero, 0
	ori $28, $zero, 0
	ori $8, $zero, 0
	ori $9, $zero, 0
