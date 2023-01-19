.data 0x0000
    # pass

.text 0x0000
main:
	lui   $1,0xFFFF			
	ori   $28,$1,0xF000
	ori $9, $0, 0xFFFF
	sw $9, 0xC62($28)
	ori $1, $0, 1
	ori $2, $0, 2
	ori $3, $0, -1
	ori $4, $0, 0	
	ori $5, $0, 0
	sw $2, 0xC60($28)
	j tag
	
	tag:
		sw $1, 0xC60($28)
		