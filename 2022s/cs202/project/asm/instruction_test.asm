.data 0x0000            
 buf: .word 0x0000
.text 0x0000      
start: lui   $1,0xFFFF   
        ori   $28,$1,0xF000  
             
test_sra:
lw $8, 0xC72($28)
sll $30, $8, 24
srl $30, $30, 31
beq $30, $0, test_sra

 lui $2, 0x80
 ori $2, $2, 0
 sra $3, $2, 31
 
 output_sra:
 sw $3, 0x0C60($28)
 lw $8, 0xC72($28)
sll $30, $8, 24
srl $30, $30, 31
bne $30, $0, output_sra

test_sub:
lw $8, 0xC72($28)
sll $30, $8, 24
srl $30, $30, 31
beq $30, $0, test_sub

addi $2, $0, 19
addi $3, $0, 4
sub $3 ,$2, $3

output_sub:
sw $3, 0x0C60($28)
 lw $8, 0xC72($28)
sll $30, $8, 24
srl $30, $30, 31
bne $30, $0, output_sub
 
test_subu:
lw $8, 0xC72($28)
sll $30, $8, 24
srl $30, $30, 31
beq $30, $0, test_sub

addi $2, $0, 13
addi $3, $0, 5
subu $3, $3, $2


output_subu:
sw $3, 0x0C60($28)
 lw $8, 0xC72($28)
sll $30, $8, 24
srl $30, $30, 31
bne $30, $0, output_subu
 
test_and:
lw $8, 0xC72($28)
sll $30, $8, 24
srl $30, $30, 31
beq $30, $0, test_and

addi $2, $0, 2
addi $3, $0, 5
and $3, $3, $2

output_and:
sw $3, 0x0C60($28)
 lw $8, 0xC72($28)
sll $30, $8, 24
srl $30, $30, 31
bne $30, $0, output_and

test_or:
lw $8, 0xC72($28)
sll $30, $8, 24
srl $30, $30, 31
beq $30, $0, test_or

addi $2, $0, 6
addi $3, $0, 5
or $3, $3, $2

output_or:
sw $3, 0x0C60($28)
 lw $8, 0xC72($28)
sll $30, $8, 24
srl $30, $30, 31
bne $30, $0, output_or

test_nor:
lw $8, 0xC72($28)
sll $30, $8, 24
srl $30, $30, 31
beq $30, $0, test_nor

addi $2, $0, 6
addi $3, $0, 5
or $3, $3, $2

output_nor:
sw $3, 0x0C60($28)
 lw $8, 0xC72($28)
sll $30, $8, 24
srl $30, $30, 31
bne $30, $0, output_nor

test_slt:
lw $8, 0xC72($28)
sll $30, $8, 24
srl $30, $30, 31
beq $30, $0, test_slt

addi $2, $0, 6
addi $3, $0, 5
slt $3, $3, $2

output_slt:
sw $3, 0x0C60($28)
lw $8, 0xC72($28)
sll $30, $8, 24
srl $30, $30, 31
bne $30, $0, output_slt

test_sltu:
lw $8, 0xC72($28)
sll $30, $8, 24
srl $30, $30, 31
beq $30, $0, test_sltu

lui $2, 0x90
ori $2, $2, 0
addi $3, $0, 5
sltu $3, $2, $3

output_sltu:
sw $3, 0x0C60($28)
lw $8, 0xC72($28)
sll $30, $8, 24
srl $30, $30, 31
bne $30, $0, output_sltu

test_addiu:
lw $8, 0xC72($28)
sll $30, $8, 24
srl $30, $30, 31
beq $30, $0, test_addiu

addiu $3, $0, -10

output_addiu:
sw $3, 0x0C60($28)
lw $8, 0xC72($28)
sll $30, $8, 24
srl $30, $30, 31
bne $30, $0, output_addiu

test_slti:
lw $8, 0xC72($28)
sll $30, $8, 24
srl $30, $30, 31
beq $30, $0, test_slti

addi $2, $0, -3
slti $3, $2, -1

output_slti:
sw $3, 0x0C60($28)
lw $8, 0xC72($28)
sll $30, $8, 24
srl $30, $30, 31
bne $30, $0, output_slti

test_sltiu:
lw $8, 0xC72($28)
sll $30, $8, 24
srl $30, $30, 31
beq $30, $0, test_sltiu

addi $2, $0, -3
sltiu $3, $2, 2

output_sltiu:
sw $3, 0x0C60($28)
lw $8, 0xC72($28)
sll $30, $8, 24
srl $30, $30, 31
bne $30, $0, output_sltiu

test_andi:
lw $8, 0xC72($28)
sll $30, $8, 24
srl $30, $30, 31
beq $30, $0, test_andi

addi $2, $0, -3
sltiu $3, $2, 2

output_andi:
sw $3, 0x0C60($28)
lw $8, 0xC72($28)
sll $30, $8, 24
srl $30, $30, 31
bne $30, $0, output_andi

test_xori:
lw $8, 0xC72($28)
sll $30, $8, 24
srl $30, $30, 31
beq $30, $0, test_xori

addi $2, $0, -3
xori $3, $2, 6

output_xori:
sw $3, 0x0C60($28)
lw $8, 0xC72($28)
sll $30, $8, 24
srl $30, $30, 31
bne $30, $0, output_xori


