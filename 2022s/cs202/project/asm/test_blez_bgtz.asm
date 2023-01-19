.data 0x0000            
 buf: .word 0x0000
.text 0x0000     
init: 
	lui   $1,0xFFFF			
	ori   $28,$1,0xF000
main:
    ori $1, $0, 4
    addi $2, $0, -4
    ori $3, $0, 1

    jal loop

test_blez1:
    blez $1, t1          #ans -> t2
    j t2

t1: 
    sw $3, 0xC60($28)
    jal loop
    j test_blez2

t2:
    ori $3, $0, 2
    sw $3, 0xC60($28)
    jal loop
    j test_blez2

test_blez2:
    blez $2, t3          #ans -> t3
    j t4

t3: 
    ori $3, $0, 3
    sw $3, 0xC60($28)
    jal loop
    j test_blez3

t4:
    ori $3, $0, 4
    sw $3, 0xC60($28)
    jal loop
    j test_blez3

test_blez3:
    blez $0, t5        #ans -> t5
    j t6

t5: 
    ori $3, $0,5
    sw $3, 0xC60($28)
    jal loop
    j test_bgtz1

t6:
    ori $3, $0,6
    sw $3, 0xC60($28)
    jal loop
    j test_bgtz1

test_bgtz1:
    bgtz $1, t7        #ans -> t7
    j t8

t7: 
    ori $3,$0, 7
    sw $3, 0xC60($28)
    jal loop
    j test_bgtz2

t8:
    ori $3,$0, 8
    sw $3, 0xC60($28)
    jal loop
    j test_bgtz2

test_bgtz2:
    bgtz $2, t9       #ans -> t10
    j t10

t9: 
   ori $3, $0,9
   sw $3, 0xC60($28)
   jal loop
   j test_bgtz3

t10:
    ori $3, $0,10
    sw $3, 0xC60($28)
    jal loop
    j test_bgtz3

test_bgtz3:
    bgtz $0, t11     #ans -> t12
    j t12

t11: 
    ori $3, $0,11
    sw $3, 0xC60($28)
    jal loop
    j t11

t12:
    ori $3, $0,12
    sw $3, 0xC60($28)
    jal loop
    j t12

loop:
    addi $0, $0, 0
    m_loop1:
        lw $24, 0xC80($28)
        beq $24, $0, m_loop1
    
    m_loop2:
        lw $24, 0xC80($28)
        bne $24, $0, m_loop2
    jr $31
