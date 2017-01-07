.data
	.asciiz "gggg"

.text
test:
	add r0 = r0 , 129
	add r1 = r1 , 129
	add r2 = r2 , 140
	add r3 = r3, 150
	and eq r2 = r1 r0
	and ne r3 = r1 r0
	lt r2 = r0 , 129
	le r3 = r0 , 129
	add r4 = r4 , 1
	and ne r1 = r2 r3
	subf r2 = r2 , r2
	sb r1 , 1(r2)
	##ret subf r0 = r0 , r0
	reti_add r0 = r0 ,88

	
