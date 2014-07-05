; lit jump 0jump call alu dup add
; and or = < u< _A!_ A! nip drop
; invert 1- 1_lshift 1_rshift A@ swap swap_bytes
; depth r@
; over >r r> return

	lit #0
	dup
	dup
	lit $first_line
	A!
	lit $cursor_x
	A!
	lit $cursor_y
	A!
	call $COLD

; =================================================================
; Для майнкрафта.
; =================================================================
	!head "TICK" 0
	!word #0
-
TICK
	lit #1
	lit #28680
	A!
	return

	!head "TICKS" 0
	!word -
-
TICKS_loop
	call $TICK
	dup
	cond_jump $TICKS_end
	1-
	jump $TICKS_loop
TICKS_end
	drop
	return

	!head "NBP!" 0
	!word -
-
NBP!
	lit #28690
	A!
	return

	!head "IOX!" 0
	!word -
-
	lit $_IOXADDR_
	A@
	call $NBP!
	; 0x7502
	lit #29954
	A!
	return

	!head "IOX@" 0
	!word -
-
	lit $_IOXADDR_
	A@
	call $NBP!
	; 0x7500
	lit #29952
	A@
	return

	!head "IOXSET" 0
	!word -
-
	lit $_IOXADDR_
	A@
	call $NBP!
	lit #29954
	A@
	or
	lit #29954
	A!
	return

	!head "IOXRST" 0
	!word -
-
	lit $_IOXADDR_
	A@
	call $NBP!
	invert
	lit #29954
	A@
	and
	lit #29954
	A!
	return

	!head "IOXADDR" 1
	!word -
-
IOXADDR
	call $DOVAR
_IOXADDR_
	!word #1

	; -- nL nH
	!head "UTIME@" 0
	!word -
-
	lit #28682
	A@
	lit #28684
	A@
	return

; =================================================================
; Переменные, константы и т.п.
; =================================================================
	!head "DOVAR" 0
	!word -
-
DOVAR
	r>
	lit $_STATE_
	A@
	cond_jump $DOVAR_interpret
	; Режим компиляции.
	call $LITERAL
	return
DOVAR_interpret
	; Режим интерпретации.
	return

	!head "DOCON" 0
	!word -
-
DOCON
	r>
	A@
	lit $_STATE_
	A@
	cond_jump $DOCON_interpret
	; Режим компиляции.
	call $LITERAL
	return
DOCON_interpret
	; Режим интерпретации.
	return

	!head "DOISTR" 0
	!word -
-
DOISTR
	lit $_STATE_
	A@
	cond_jump $DOISTR_interpret
	r>
	A@
	call $A,
DOISTR_interpret
	return

	!head "VARIABLE" 0
	!word -
-
VARIABLE
	call $HEADER
	lit $DOVAR
	1_rshift
	lit #16384
	or
	call $A,
	lit #0
	call $A,
	call $IMMEDIATE
	return

	!head "CONSTANT" 0
	!word -
-
CONSTANT
	call $HEADER
	lit $DOCON
	1_rshift
	lit #16384
	or
	call $A,
	call $A,
	call $IMMEDIATE
	return

	!head "CREATE" 0
	!word -
-
	call $HEADER
	lit $_HERE_
	A@
	lit #4
	add
	lit #128
	swap_bytes
	or
	call $A,
	; return
	lit #28684
	call $A,
	return

	!head "DOES>" 0
	!word -
-
	lit $_LAST_
	A@
	lit #2
	add
	r>
	dup
	>r
	1_rshift
	swap
	A!
	return

; addr -- n
	!head "C@" 0
	!word -
-
C@
	dup
	lit #1
	and
	; Переход, если адрес кратен 2.
	cond_jump $fetch_byte_a
	A@
	swap_bytes
	lit #255
	and
	return
fetch_byte_a
	A@
	lit #255
	and
	return

; Чтение по невыровненному адресу.
; addr -- n
	!head "@" 0
	!word -
-
@
	dup
	lit #1
	and
	; Переход, если адрес кратен 2.
	cond_jump $fetch_a
	dup
	; addr addr
	call $1+
	A@
	lit #255
	and
	swap_bytes
	swap
	A@
	swap_bytes
	lit #255
	and
	or
	return
fetch_a
	A@
	return

; n addr --
	!head "C!" 0
	!word -
-
C!
	dup
	lit #1
	and
	; Переход, если адрес кратен 2.
	cond_jump $c_store_a
	dup
	A@
	lit #255
	and
	call $ROT
	lit #255
	and
	swap_bytes
	or
	swap
	A!
	return
c_store_a
	dup
	A@
	lit #255
	swap_bytes	
	and
	; n addr n1
	call $ROT
	; addr n1 n
	lit #255
	and
	or
	swap
	A!
	return

; n addr --
	!head "!" 0
	!word -
-
!
	dup
	lit #1
	and
	; Переход, если адрес кратен 2.
	cond_jump $store_a
	call $2DUP
	; n addr n addr
	call $C!
	call $1+
	swap
	swap_bytes
	swap
	call $C!
	return
store_a
	A!
	return

; n addr --
	!head "+!" 0
	!word -
-
	swap
	over
	call $@
	add
	swap
	call $!
	return

; n addr --
	!head "-!" 0
	!word -
-
	swap
	over
	call $@
	swap
	call $SUB
	swap
	call $!
	return

; n addr --
	!head "+A!" 0
	!word -
-
	swap
	over
	A@
	add
	swap
	A!
	return

; n addr --
	!head "-A!" 0
	!word -
-
	swap
	over
	A@
	swap
	call $SUB
	swap
	A!
	return

	!head "CELL" 0
	!word -
-
	call $DOCON
	!word #2

	!head "CELLS" 0
	!word -
-
	1_lshift
	return

; =================================================================
; Слова - инструкции процессора.
; =================================================================

	!head "DEPTH" 1
	!word -
-
	call $DOISTR
	depth
	return

	!head "DUP" 1
	!word -
-
	call $DOISTR
	dup
	return

	!head "DROP" 1
	!word -
-
	call $DOISTR
	drop
	return

	!head "OVER" 1
	!word -
-
	call $DOISTR
	over
	return

	!head "NIP" 1
	!word -
-
	call $DOISTR
	nip
	return

	!head "A!" 0
	!word -
-
	A!
	return

	!head "A@" 1
	!word -
-
	call $DOISTR
	A@
	return

	!head "+" 1
	!word -
-
	call $DOISTR
	add
	return

	!head "INVERT" 1
	!word -
-
	call $DOISTR
	invert
	return

	!head "AND" 1
	!word -
-
	call $DOISTR
	and
	return

	!head "OR" 1
	!word -
-
	call $DOISTR
	or
	return

	!head "1LSHIFT" 1
	!word -
-
	call $DOISTR
	1_lshift
	return

	!head "1RSHIFT" 1
	!word -
-
	call $DOISTR
	1_rshift
	return

	!head "1-" 1
	!word -
-
	call $DOISTR
	1-
	return

	!head "=" 1
	!word -
-
	call $DOISTR
	=
	return

	!head "<" 1
	!word -
-
	call $DOISTR
	<
	return

	!head "U<" 1
	!word -
-
	call $DOISTR
	u<
	return

	!head "R@" 1
	!word -
-
	call $DOISTR
	r@
	return

	!head "R>" 1
	!word -
-
	call $DOISTR
	r>
	return

	!head ">R" 1
	!word -
-
	call $DOISTR
	>r
	return

	!head "EXIT" 1
	!word -
-
	call $DOISTR
	return

; =================================================================
; Операции со стеком.
; =================================================================
; n1 n2 -- n1 n2 n1 n2
	!head "2DUP" 0
	!word -
-
2DUP
	over
	over
	return

; n1 n2 --
	!head "2DROP" 0
	!word -
-
2DROP
	drop
	drop
	return

; n1 n2 n3 -- n2 n3 n1
	!head "ROT" 0
	!word -
-
ROT
	>r
	swap
	r>
	swap
	return

; n1 n2 n3 -- n3 n1 n2
	!head "-ROT" 0
	!word -
-
-ROT
	call $ROT
	call $ROT
	return

; n1 n2 n3 n4 -- n3 n4 n1 n2
	!head "2SWAP" 0
	!word -
-
2SWAP
	call $ROT
	>r
	call $ROT
	r>
	return

	; Удаление всех элементов из стека данных.
	!head "0SP" 0
	!word -
-
0SP
	depth
	cond_jump $0SP_zero
	drop
	jump $0SP
0SP_zero
	return

	!head ".S" 0
	!word -
-
.S
	; Запись первоначального размера стека.
	depth
	lit $.S_depth
	A!
	; Перенос всего из стека в буфер.
.S_loop1
	depth
	cond_jump $.S_zero_depth
	depth
	1_lshift
	lit $.S_buf
	add
	A!
	jump $.S_loop1
.S_zero_depth
	; Перенос из буфера в стек.
.S_loop2
	; Если .S_depth = depth, то выход.
	depth
	lit $.S_depth
	A@
	=
	invert
	cond_jump $.S_end
	lit $.S_buf
	depth
	1_lshift
	add
	A@
	dup
	call $.
	jump $.S_loop2
.S_end
	return
.S_buf
	!allot 64
.S_depth
	!word #0

; =================================================================
; Ввод/вывод.
; =================================================================
; addr --
	!head "TYPE" 0
	!word -
-
TYPE
	dup
	call $C@
	dup
	cond_jump $TYPE_end
	call $EMIT
	call $1+
	jump $TYPE
TYPE_end
	call $2DROP
	return

; x y --
	!head "AT-XY" 0
	!word -
-
AT-XY
	lit $cursor_y
	_A!_
	; 0x6604
	lit #26116
	A!
	lit $cursor_x
	_A!_
	; 0x6602
	lit #26114
	A!
	return

; <char> --
	!head "EMIT" 0
	!word -
-
EMIT
	; y * 128 + x + 0x4000
	lit $cursor_y
	A@
	lit $first_line
	A@
	add
	; если cursor_y + first_line > 74, то отнять 75.
	lit #74
	over
	<
	cond_jump $EMIT_s
	lit #-75
	add
EMIT_s
	; 7 lshift
	swap_bytes
	1_rshift
	; 11111110000000
	lit #16256
	and
	lit $cursor_x
	A@
	add
	; 0x4000
	lit #16384
	add
	A!
	; Если cursor_x = 99, то вызвать cr.
	lit $cursor_x
	A@
	lit #99
	=
	cond_jump $EMIT_not_99
	call $CR
	return
EMIT_not_99
	lit $cursor_x
	A@
	call $1+
	lit $cursor_y
	A@
	call $AT-XY
	return

; addr n -- addr
	!head "ACCEPT" 0
	!word -
-
ACCEPT
	over
	add
	1-
	over
ACCEPT_loop
	call $KEY
	dup
	lit #13
	=
	cond_jump $ACCEPT_not_enter
	; Была нажата клавиша Enter.
	drop
	lit #0
	swap
	call $C!
	drop
	return
ACCEPT_not_enter
	dup
	lit #8
	=
	cond_jump $ACCEPT_KEY
	; Была нажата клавиша backspace.
	drop
	call $ROT
	call $2DUP
	=
	cond_jump $ACCEPT_bs
	call $-ROT
	jump $ACCEPT_loop
ACCEPT_bs
	call $-ROT
	1-
	call $BS
	jump $ACCEPT_loop
ACCEPT_KEY
	call $-ROT
	call $2DUP
	=
	cond_jump $ACCEPT_KEY_1
	call $ROT
	drop
	jump $ACCEPT_loop
ACCEPT_KEY_1
	call $ROT
	dup
	call $EMIT
	over
	call $C!
	call $1+
	jump $ACCEPT_loop

	!head "BS" 0
	!word -
-
BS
	lit $cursor_x
	A@
	dup
	cond_jump $bs_end
	1-
	lit $cursor_y
	A@
	over
	over
	call $AT-XY
	call $SPACE
	call $AT-XY
	return
bs_end
	drop
	return

	!head "CR" 0
	!word -
-
CR
	lit $cursor_y
	A@
	lit #74
	=
	cond_jump $cr_not_74
	lit #0
	lit #74
	call $AT-XY
	call $SCROLL
	return
cr_not_74
	lit #0
	lit $cursor_y
	A@
	call $1+
	call $AT-XY
	return

	!head "SCROLL" 0
	!word -
-
SCROLL
	lit $first_line
	A@
	lit #1
	add
	dup
	lit #75
	=
	; Переход, если first_line != 74
	cond_jump $SCROLL_not_74
	drop
	lit #0
SCROLL_not_74
	lit $first_line
	_A!_
	lit #26112
	_A!_

	; Определение номера нижней строки.
	dup
	cond_jump $SCROLL_zero
	1-
	jump $SCROLL_w
SCROLL_zero
	drop
	lit #74
SCROLL_w
	; В стеке - номер нижней строки.
	; 128 * 0x4000 +
	swap_bytes
	1_rshift
	lit #16256
	and
	; 0x4000
	lit #16384
	add
	; Заполнение нижней строки пробелами.
	lit #100
SCROLL_loop
	swap
	dup
	lit #32
	swap
	A!
	call $1+
	swap
	1-
	dup
	; addr+1 i-1 i-1
	cond_jump $SCROLL_end
	jump $SCROLL_loop
SCROLL_end
	drop
	drop
	return

; 0x7000 (28672) - Указатель начала очереди. Чтение и запись.
; 0x7002 (28674) - Указатель конца очереди. Только чтение.
; 0x7004 (28676) - Значение клавиши в начале очереди. Только чтение.
	!head "KEY" 0
	!word -
-
KEY
	lit #28672
	A@
	lit #28674
	A@
	over
	=
	; Переход, если буфер клавиатуры не пуст.
	cond_jump $KEY_p
	drop
	call $TICK
	jump $KEY
KEY_p
	call $1+
	lit #28672
	A!
	lit #28676
	A@
	return

	!head "KEY?" 0
	!word -
-
	lit #28672
	A@
	lit #28674
	A@
	=
	invert
	return

	!head "PAGE" 0
	!word -
-
	lit #9600
PAGE_loop
	1-
	; k
	dup
	lit #16384
	add
	; k a+k
	lit #32
	swap
	A!
	; k
	dup
	cond_jump $PAGE_end
	jump $PAGE_loop
PAGE_end
	lit #0
	call $AT-XY
	return

	; Выводит пробел.
	!head "SPACE" 0
	!word -
-
SPACE
	lit #32
	call $EMIT
	return

	!head "(.")" 0
	!word -
-
(.")
	r>
	; addr
(.")_loop
	dup
	call $C@
	dup
	; addr c c
	cond_jump $(.")_end
	call $EMIT
	call $1+
	jump $(.")_loop
(.")_end
	drop
	call $1+
	; addr
	dup
	lit #1
	and
	cond_jump $(.")_exit
	call $1+
(.")_exit
	>r
	return

	!head "."" 1
	!word -
-
."
	lit $(.")
	1_rshift
	lit #16384
	or
	call $A,
	lit #34
	call $WORD
	call $S,
	lit $_HERE_
	A@
	lit #1
	and
	cond_jump $."_even
	lit #0
	call $C,
."_even
	return

	!head "." 0
	!word -
-
.
	call $ITOA
	call $TYPE
	call $SPACE
	return

	!head "U." 0
	!word -
-
U.
	call $UITOA
	call $TYPE
	call $SPACE
	return

; =================================================================
; Арифметические и логические операции.
; =================================================================
	!head "1+" 0
	!word -
-
1+
	lit #1
	add
	return

; n1 n2 -- n
	!head "U*" 0
	!word -
-
U*
	lit #0
u_mul_loop
	over
	cond_jump $u_mul_exit
	over
	lit #1
	and
	cond_jump $u_mul_zero_bit
	call $ROT
	swap
	over
	add
	call $ROT
	swap
u_mul_zero_bit
	call $ROT
	1_lshift
	call $ROT
	1_rshift
	call $ROT
	jump $u_mul_loop
u_mul_exit
	nip
	nip
	return

; Знаковое умножение.
; n1 n2 -- n
	!head "*" 0
	!word -
-
*
	call $2DUP
	call $0<
	swap
	call $0<
	call $XOR
	call $ROT
	call $ABS
	call $ROT
	call $ABS
	call $U*
	swap
	cond_jump $mul_exit
	call $NEGATE
mul_exit
	return

; n1 n2 -- n3 n4
; n1 - делимое, n2 - делитель, n3 - остаток, n4 - частное.
	!head "U/MOD" 0
	!word -
-
U/MOD
	; DUP 0= IF 2DROP 0 0 EXIT THEN
	dup
	call $0=
	; Переход, если n2=0.
	cond_jump $U/MOD_not_zero
	; Деление на 0.
	call $2DROP
	lit #0
	lit #0
	return
U/MOD_not_zero
	; 1
	lit #1
	; BEGIN
U/MOD_loop1
	; -ROT DUP 32768 AND 0=
	call $-ROT
	dup
	; 0x8000
	lit #32767
	invert
	and
	call $0=
	; WHILE
	cond_jump $U/MOD_loop1_exit
	; 2* ROT 2*
	1_lshift
	call $ROT
	1_lshift
	; REPEAT
	jump $U/MOD_loop1
U/MOD_loop1_exit
	; ROT 0 SWAP
	call $ROT
	lit #0
	swap
	; BEGIN
U/MOD_loop2
	; DUP
	dup
	; WHILE
	cond_jump $U/MOD_loop2_exit
	; 2SWAP
	call $2SWAP
	; 2DUP U< INVERT IF
	call $2DUP
	u<
	invert
	cond_jump $U/MOD_g
	; SWAP OVER -
	swap
	over
	call $NEGATE
	add
	;SWAP 2SWAP SWAP OVER +
	swap
	call $2SWAP
	swap
	over
	add
	; SWAP 2SWAP
	swap
	call $2SWAP
	; THEN
U/MOD_g
	; 1 U>> 2SWAP 1 U>>
	1_rshift
	call $2SWAP
	1_rshift
	; REPEAT
	jump $U/MOD_loop2
U/MOD_loop2_exit
	; DROP NIP
	drop
	nip
	return

	!head "/" 0
	!word -
-
/
	call $2DUP
	call $0<
	swap
	call $0<
	call $XOR
	call $ROT
	call $ABS
	call $ROT
	call $ABS
	call $U/MOD
	nip
	swap
	cond_jump $DIV_exit
	call $NEGATE
DIV_exit
	return

; n1 n2 -- n
	!head "XOR" 0
	!word -
-
XOR
	call $2DUP
	invert
	and
	call $-ROT
	swap
	invert
	and
	or
	return

	!head "-" 0
	!word -
-
SUB
	call $NEGATE
	add
	return

; n1 -- n
	!head "ABS" 0
	!word -
-
ABS
	dup
	call $0<
	cond_jump $abs_pos
	call $NEGATE
abs_pos
	return

; n1 -- n
	!head "NEGATE" 0
	!word -
-
NEGATE
	invert
	lit #1
	add
	return

	; n1 n2 -- n
	!head "MIN" 0
	!word -
-
MIN
	call $2DUP
	<
	cond_jump $MIN_g
	drop
	return
MIN_g
	nip
	return

	; n1 n2 -- n
	!head "MAX" 0
	!word -
-
MAX
	call $2DUP
	<
	cond_jump $MIN_g
	nip
	return
MAX_g
	drop
	return

; =================================================================
; Сравнение.
; =================================================================
	!head "0=" 0
	!word -
-
0=
	lit #0
	=
	return

	!head "0<>" 0
	!word -
-
	lit #0
	=
	invert
	return

	!head "0<" 0
	!word -
-
0<
	lit #0
	<
	return

	!head "<>" 0
	!word -
-
<>
	=
	invert
	return

	!head ">" 0
	!word -
-
	swap
	<
	return

	!head "U>" 0
	!word -
-
	swap
	u<
	return

	!head ">=" 0
	!word -
-
	<
	invert
	return

	!head "<=" 0
	!word -
-
	swap
	<
	invert
	return

; =================================================================
; Операции со строками.
; =================================================================

	; str1 str2 -- f
	!head "STRCMP" 0
	!word -
-
STRCMP
	call $2DUP
	call $C@
	swap
	call $C@
	; addr1 addr2 k2 k1
	=
	cond_jump $STRCMP_not_eq
	; addr1 addr2
	dup
	call $C@
	cond_jump $STRCMP_eq
	call $1+
	swap
	call $1+
	jump $STRCMP
STRCMP_eq
	call $2DROP
	lit #0
	invert
	return
STRCMP_not_eq
	call $2DROP
	lit #0
	return

; n -- str
	!head "UITOA" 0
	!word -
-
UITOA
	lit $UITOA_buf_end
	swap
; BEGIN
UITOA_loop
	; _BASE_ @ U/MOD -ROT
	lit $_BASE_
	A@
	call $U/MOD
	call $-ROT
	; DUP 10 U< INVERT IF 7 + THEN 48 +
	dup
	lit #10
	u<
	invert
	cond_jump $UITOA_N
	lit #7
	add
UITOA_N
	lit #48
	add
	; OVER C! 1- SWAP
	over
	call $C!
	1-
	swap
	; DUP 0=
	dup
	call $0=
	; UNTIL
	cond_jump $UITOA_loop
	; DROP 1+
	drop
	call $1+
	return
	!word #0
	!word #0
	!word #0
	!word #0
	!word #0
	!word #0
	!word #0
	!word #0
	!word #0
UITOA_buf_end
	!word #0

	!head "ITOA" 0
	!word -
-
ITOA
	; DUP 0< IF
	dup
	call $0<
	cond_jump $ITOA_pos
	; NEGATE UITOA 1- DUP 45 SWAP C!
	call $NEGATE
	call $UITOA
	1-
	dup
	lit #45
	swap
	call $C!
	return
ITOA_pos
	call $UITOA
	return

; Преобразование кода одного символа в число.
; Если код не соответствует никакому числу, то вернет -1.
; <char> -- n1
	!head "(UATOI)" 0
	!word -
-
(UATOI)
	;DUP DUP 2DUP
	dup
	dup
	call $2DUP
	; 48 U< INVERT SWAP 58 U< AND
	lit #48
	u<
	invert
	swap
	lit #58
	u<
	and
	; -ROT
	call $-ROT
	; 65 U< INVERT SWAP 71 U< AND
	lit #65
	u<
	invert
	swap
	lit #71
	u<
	and
	; OR
	or
	; IF
	cond_jump $_UATOI_not_num
	; 48 -
	lit #48
	call $SUB
	; DUP 10 U< INVERT IF
	dup
	lit #10
	u<
	invert
	cond_jump $_UATOI_10g
	; 7 -
	lit #7
	call $SUB
	; THEN
_UATOI_10g
	dup
	lit $_BASE_
	A@
	u<
	cond_jump $_UATOI_not_num
	return
_UATOI_not_num
	; DROP -1
	drop
	lit #0
	invert
	return

; str -- n
	!head "UATOI" 0
	!word -
-
UATOI
	; 0
	lit #0
	; BEGIN
UATOI_loop
	; OVER C@ (UATOI) DUP
	over
	call $C@
	call $(UATOI)
	dup
	; -1 <>
	lit #0
	invert
	call $<>
	; WHILE
	cond_jump $UATOI_loop_exit
	; SWAP _BASE_ @ U* +
	swap
	lit $_BASE_
	A@
	call $U*
	add
	; SWAP 1+ SWAP
	swap
	call $1+
	swap
	; REPEAT
	jump $UATOI_loop
UATOI_loop_exit
	; DROP NIP ;
	drop
	nip
	return

; str -- n
	!head "ATOI" 0
	!word -
-
ATOI
	; DUP C@ 45 = IF
	dup
	call $C@
	lit #45
	=
	cond_jump $ATOI_pos
	; 1+ UATOI NEGATE
	call $1+
	call $UATOI
	call $NEGATE
	return
ATOI_pos
	call $UATOI
	return

	; str -- f
	!head "ISUNUM" 0
	!word -
-
ISUNUM
	; C@ (UATOI) TRUE = IF FALSE ELSE TRUE THEN ;
	call $C@
	call $(UATOI)
	lit #0
	invert
	=
	cond_jump $ISUNUM_TRUE
	lit #0
	return
ISUNUM_TRUE
	lit #0
	invert
	return

	; str -- f
	!head "ISNUM" 0
	!word -
-
ISNUM
	; DUP C@ 45 = IF 1+ THEN ISUNUM ;
	dup
	call $C@
	lit #45
	=
	cond_jump $ISNUM_pos
	call $1+
ISNUM_pos
	call $ISUNUM
	return

	; Удаление пробелов в начале строки.
	; str -- str11
	!head "SKIP-WS" 0
	!word -
-
SKIP-WS
	dup
	call $C@
	dup
	cond_jump $SKIP-WS_zero
	lit #33
	u<
	cond_jump $SKIP-WS_end
	call $1+
	jump $SKIP-WS
SKIP-WS_zero
	drop
SKIP-WS_end
	return

	; Возвращает длину строки (без терминирующего нулевого байта).
	; str -- n
	!head "STRLEN" 0
	!word -
-
STRLEN
	lit #0
STRLEN_loop
	swap
	dup
	call $C@
	cond_jump $STRCMP_end
	call $1+
	swap
	call $1+
	jump $STRLEN_loop
STRCMP_end
	drop
	return

; =================================================================
; =================================================================
	; Объем свободной памяти.
	; -- n
	!head "FREE" 0
	!word -
-
FREE
	lit #16384
	lit $_HERE_
	A@
	call $SUB
	return

	; n --
	!head "ALLOT" 0
	!word -
-
ALLOT
	dup
	call $FREE
	u<
	cond_jump $allot_out_of_memory
	lit $_HERE_
	A@
	add
	lit $_HERE_
	A!
	return
allot_out_of_memory
	call $(.")
	!string "Out of memory"
	call $ABORT

	; n --
	!head "A," 0
	!word -
-
A,
	lit $_HERE_
	A@
	A!
	lit #2
	call $ALLOT
	return

	!head "C," 0
	!word -
-
C,
	lit $_HERE_
	A@
	call $C!
	lit #1
	call $ALLOT
	return

	; str --
	!head "S," 0
	!word -
-
S,
	; str
	dup
	call $C@
	dup
	cond_jump $S,_end
	call $C,
	call $1+
	jump $S,
S,_end
	call $C,
	drop	
	return

	!head "DECIMAL" 0
	!word -
-
	lit #10
	lit $_BASE_
	A!
	return

	!head "HEX" 0
	!word -
-
	lit #16
	lit $_BASE_
	A!
	return

	; Нахождение имени слова по адресу его исполняемой части.
	; addr -- str
	!head ">NAME" 0
	!word -
-
>NAME
	lit #6
	call $SUB
>NAME_loop
	dup
	call $C@
	cond_jump $>NAME_exit
	1-
	jump $>NAME_loop
>NAME_exit
	call $1+
	return

	!head "WORDS" 0
	!word -
-
WORDS
	lit $_LAST_
	A@
WORDS_loop
	dup
	call $>NAME
	call $TYPE
	call $SPACE
	lit #2
	call $SUB
	A@
	dup
	cond_jump $WORDS_end
	jump $WORDS_loop
WORDS_end
	drop
	return

	; addr --
	!head "EXECUTE" 0
	!word -
-
EXECUTE
	>r
	return

	!head "POSTPONE" 1
	!word -
-
	call $'
	dup
	cond_jump $POSTPONE_zero
	1_rshift
	lit #16384
	or
	call $A,
POSTPONE_zero
	return

	; n --
	!head "LITERAL" 1
	!word -
-
LITERAL
	dup
	call $0<
	cond_jump $LITERAL_pos
	invert
	; 0x32768
	lit #128
	swap_bytes
	or
	call $A,
	; Код команды invert.
	lit #26112
	call $A,
	return
LITERAL_pos
	; 0x32768
	lit #128
	swap_bytes
	or
	call $A,
	return

	!head "HEADER" 0
	!word -
-
HEADER
	lit #32
	call $WORD
	dup
	call $STRLEN
	lit #1
	and
	call $0=
	cond_jump $HEADER_odd
	; Длина строки четная.
	lit #0
	call $C,
HEADER_odd
	lit #0
	call $C,
	call $S,
	lit #0
	call $C,
	lit $_LAST_
	A@
	call $A,
	lit $_HERE_
	A@
	lit $_LAST_
	A!
	return

	!head "'" 0
	!word -
-
'
	lit #32
	call $WORD
	dup
	call $FIND
	drop
	dup
	cond_jump $q_ABORT
	nip
	return
q_ABORT
	drop
	call $(.")
	!string "Unknown token: "
	call $TYPE
	call $CR
	call $ABORT
	return

	!head "FORGET" 0
	!word -
-
	call $'
	dup
	1-
	1-
	A@
	lit $_LAST_
	A!
	call $>NAME
	1-
	lit #1
	invert
	and
	lit $_HERE_
	A!
	return

; =================================================================
; Условия и циклы.
; =================================================================

	!head "IF" 1
	!word -
-
IF
	lit $_HERE_
	A@
	lit #8192
	call $A,
	return

	!head "ELSE" 1
	!word -
-
ELSE
	lit $_HERE_
	A@
	swap
	lit #0
	call $A,
	call $THEN
	return

	!head "THEN" 1
	!word -
-
THEN
	dup
	A@
	lit $_HERE_
	A@
	1_rshift
	or
	swap
	A!
	return

	!head "BEGIN" 1
	!word -
-
BEGIN
	lit $_HERE_
	A@
	1_rshift
	return

	!head "AGAIN" 1
	!word -
-
AGAIN
	call $A,
	return

	!head "UNTIL" 1
	!word -
-
UNTIL
	lit #8192
	or
	call $A,
	return

	!head "WHILE" 1
	!word -
-
WHILE
	lit $_HERE_
	A@
	lit #0
	call $A,
	return

	!head "REPEAT" 1
	!word -
-
REPEAT
	swap
	call $A,

	lit $_HERE_
	A@
	1_rshift
	lit #8192
	or
	swap
	A!
	return

	!head "DO" 1
	!word -
-
DO
	; >r
	lit #24903
	call $A,
	lit #24903
	call $A,
	lit $_HERE_
	A@
	return

	!head "(loop)" 0
	!word -
-
(loop)
	r>
	r>
	r>
	call $1+
	call $2DUP
	=
	cond_jump $(loop)_not_equ
	drop
	drop
	lit #2
	add
	>r
	return
(loop)_not_equ
	>r
	>r
	A@
	>r
	return

	!head "LOOP" 1
	!word -
-
LOOP
	lit $(loop)
	1_rshift
	lit #16384
	or
	call $A,
	call $A,
	return

	!head "I" 0
	!word -
-
I
	r>
	r>
	r@
	swap
	>r
	swap
	>r
	return

	!head "J" 0
	!word -
-
J
	r>
	r>
	r>
	r>
	r@
	swap
	>r
	swap
	>r
	swap
	>r
	swap
	>r
	return

	!head "TIMES" 1
	!word -
-
	lit $_STATE_
	A@
	cond_jump $TIMES_i
	; STATE = 1
	call $'
	call $LITERAL
	lit $TIMES_d
	1_rshift
	lit #16384
	or
	call $A,
	return
TIMES_i
	; STATE = 0
	call $'
TIMES_d
	>r
	>r
TIMES_i_loop
	r>
	dup
	cond_jump $TIMES_i_end
	1-
	r>
	dup
	call $EXECUTE
	>r
	>r
	jump $TIMES_i_loop
TIMES_i_end
	r>
	call $2DROP
	return

; =================================================================
; Интерпретатор.
; =================================================================
; Так как в этом форте используются нуль-терминированные строки,
; то нет смысла пытаться придерживатся какого-нибудь стандарта в реализации интерпретатора.
; TIB ( -- addr ) возвращает адрес входного буфера.
; TIBPTR ( -- addr ) переменная, указывающая на интерпертируемую строку.
; WORD ( <char> -- addr ) выделяет (ограничивает нулем) слово ограниченное разделителем <char> из строки, на которую указывает TIBPTR.
; Оставляет адрес нуль-терминированной строки. В TIBPTR сохраняется адрес оставшейся строки.
; INTERPRET ( addr -- ?? ) Сохраняет addr в TIBPTR, а потом...

	; str1 -- addr1 n
	; addr1 - адрес исполняемой части найденного слова, если слово не найдено - 0.
	; n - байт признака.
	!head "FIND" 0
	!word -
-
FIND
	lit $_LAST_
	A@
FIND_loop
	; str addr
	call $2DUP
	; str addr str addr
	call $>NAME
	call $STRCMP
	cond_jump $FIND_not_eq
	; str addr
	; Проверка признака скрытого слова.
	dup
	lit #3
	call $SUB
	call $C@
	; str addr s
	lit #2
	call $<>
	cond_jump $FIND_not_eq
	nip
	dup
	lit #3
	call $SUB
	call $C@

	return
FIND_not_eq
	; str addr
	lit #2
	call $SUB
	A@
	dup
	cond_jump $FIND_not_found
	jump $FIND_loop
FIND_not_found
	call $2DROP
	lit #0
	lit #0
	return

	; Переход в режим интерпретации.
	!head "[" 1
	!word -
-
[
	lit #0
	lit $_STATE_
	A!
	return

	; Переход в режим компиляции.
	!head "]" 0
	!word -
-
]
	lit #1
	lit $_STATE_
	A!
	return

	!head ":" 0
	!word -
-
	call $HEADER
	call $HIDE
	call $]
	return

	!head ";" 1
	!word -
-
	lit #28684
	call $A,
	call $UNHIDE
	call $[
	return

	!head "HIDE" 0
	!word -
-
HIDE
	lit #2
	lit $_LAST_
	A@
	lit #3
	call $SUB
	call $C!
	return

	!head "UNHIDE" 0
	!word -
-
UNHIDE
	lit #0
	lit $_LAST_
	A@
	lit #3
	call $SUB
	call $C!
	return

	!head "IMMEDIATE" 0
	!word -
-
IMMEDIATE
	lit #1
	lit $_LAST_
	A@
	lit #3
	call $SUB
	call $C!
	return

	; Выход в диалоговый режим.
	!head "QUIT" 0
	!word -
-
QUIT
	lit #0
	lit $_STATE_
	A!
QUIT_loop
	call $CR
	lit $_STATE_
	A@
	cond_jump $QUIT_interpret
	call $(.")
	!string "compile: "
	jump $QUIT_a
QUIT_interpret
	call $(.")
	!string "> "
QUIT_a
	lit $_TIB_
	lit #150
	call $ACCEPT
	call $SPACE
	call $INTERPRET
	jump $QUIT_loop

	!head "COLD" 0
	!word -
-
COLD
	call $CR
	call $(.")
	!string "NedoForth v0.1 Initialized"
	call $CR
	call $FREE
	call $.
	call $(.")
	!string "bytes free."
	call $CR
	call $QUIT

	; Очистка стека и выход в диалоговый режим.
	!head "ABORT" 0
	!word -
-
ABORT
	call $0SP
	call $QUIT

	; <char> -- addr
	!head "WORD" 0
	!word -
-
WORD
	lit $_TIBPTR_
	A@
WORD_loop1
	dup
	call $C@
	cond_jump $WORD_loop1_end
	call $2DUP
	call $C@
	=
	cond_jump $WORD_loop1_end
	call $1+
	jump $WORD_loop1
WORD_loop1_end
	swap
	over
WORD_loop2
	dup
	call $C@
	cond_jump $WORD_loop2_end
	call $2DUP
	call $C@
	call $<>
	cond_jump $WORD_loop2_end
	call $1+
	jump $WORD_loop2
WORD_loop2_end
	nip
	dup
	call $C@
	cond_jump $WORD_zero
	dup
	lit #0
	swap
	call $C!
	call $1+
WORD_zero
	lit $_TIBPTR_
	A!
	return

	; addr -- ??
	!head "INTERPRET" 0
	!word -
-
INTERPRET
	lit $_TIBPTR_
	A!
INTERPRET_loop
	lit #32
	call $WORD
	; word
	; Выход, если word указывает на конец строки (нулевой байт).
	dup
	call $C@
	cond_jump $INTERPRET_exit
	; word
	dup
	call $FIND
	; word w-addr s
	over
	cond_jump $INTERPRET_not_found
	; Слово найдено.
	; word w-addr s
	lit $_STATE_
	A@
	cond_jump $INTERPRET_state_zero
	; Режим компиляции.
	; word w-addr s

	cond_jump $INTERPRET_zero_sig
	; word w-addr
	; Байт признака не нулевой. Интерпретация.
	nip
	call $EXECUTE
	jump $INTERPRET_loop
INTERPRET_zero_sig
	; Байт признака нулевой. Компиляция.
	; word w-addr
	nip
	1_rshift
	lit #16384
	or
	call $A,
	jump $INTERPRET_loop
INTERPRET_state_zero
	; Режим интерпретации.
	; word w-addr s
	drop
	nip
	call $EXECUTE
	jump $INTERPRET_loop

INTERPRET_not_found
	; Такого слова в словаре нет.
	; word w-addr s
	; Проверка, не число ли это.
	call $2DROP
	; word
	dup
	call $ISNUM
	cond_jump $INTERPRET_error
	; Это число.
	; word
	call $ATOI
	; n
	lit $_STATE_
	A@
	cond_jump $INTERPRET_state_zero2
	; Режим компиляции.
	call $LITERAL
	jump $INTERPRET_loop
INTERPRET_state_zero2
	; Режим интерпретации.
	jump $INTERPRET_loop
INTERPRET_error
	; word
	call $(.")
	!string "Unknown token: "
	call $TYPE
	call $CR
	call $ABORT
INTERPRET_exit
	drop
	return

; =================================================================
; Системные переменные и константы.
	!head "TIB" 1
	!word -
-
TIB
	call $DOVAR
_TIB_
	!allot 150

	!head "TIBPTR" 1
	!word -
-
TIBPTR
	call $DOVAR
_TIBPTR_
	!word #0

	!head "TRUE" 1
	!word -
-
TRUE
	call $DOCON
	!word #-1

	!head "FALSE" 1
	!word -
-
FALSE
	call $DOCON
	!word #0

	!head "BASE" 1
	!word -
-
BASE
	call $DOVAR
_BASE_
	!word #10

	; 0 - режим интерпретации, 1 - режим компиляции.
	!head "STATE" 1
	!word -
-
STATE
	call $DOVAR
_STATE_
	!word #0

	; Адрес начала незанятой памяти.
	!head "HERE" 1
	!word -
-
HERE
	call $DOVAR
_HERE_
	!word $HERE_label

	; Адрес последнего слова.
	!head "LAST" 1
	!word -
-
LAST
	call $DOVAR
_LAST_
	!word -

cursor_x
	!word #0
cursor_y
	!word #0
first_line
	!word #0
HERE_label
