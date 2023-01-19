.data 0x0000				      		
	buf: .word 0x0000
.text 0x0000
main:
	ori $1, $0, 3
	ori $2, $0, 2
	multu $2, $1
	divu $1, $2
	blez $1, ttt
	bgtz $2, ttt

ttt:
	ori $1, $0,  4