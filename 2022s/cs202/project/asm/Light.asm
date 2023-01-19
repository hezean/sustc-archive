.data 0x0000            
 buf: .word 0x0000
.text 0x0000     
init: 
	lui   $1,0xFFFF			
	ori   $28,$1,0xF000
main:
	ori $1, $0, 3
	sw $1, 0xC50($28)
	#sw $1, 0xC60($28)
	j main