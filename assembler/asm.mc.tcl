
if {$argc != 2} {
	puts {Incorrect command line arguments.}
	exit
}

source {asm_proc.tcl}

set asm_filename [lindex $argv 0]
set out_filename [lindex $argv 1]

# Список всех мнемоник.
set mnemonic_list {lit jump cond_jump call alu dup add
					and or xor = < u< _A!_ A! nip drop
					invert 1- 1_lshift 1_rshift swap_bytes A@ swap
					depth r@
					over >r r> return}

set directives {!word !string !head !allot}

# Сравнение строки с шаблонами (комментарий, метка, инструкция и т.д.)
# Возвращает: empty_string comment instruction directive label error
proc check_line {line} {
	global mnemonic_list directives
	set op_flag 0
	set directive_flag 0
	set op_in_first_column 0
	foreach cmd $mnemonic_list {
		set pattern_op {}
		set pattern_op_in_first_column {}
		append pattern_op {^\s*} $cmd {(\s|$)}
		append pattern_op_in_first_column {^} $cmd {(\s|$)}
		if {[regexp $pattern_op $line] == 1} {incr op_flag}
		if {[regexp $pattern_op_in_first_column $line] == 1} {incr op_in_first_column}
	}
	foreach cmd $directives {
		set pattern_directive {}
		set pattern_op_in_first_column {}
		append pattern_directive {^\s*} $cmd {(\s|$)}
		append pattern_op_in_first_column {^} $cmd {(\s|$)}
		if {[regexp $pattern_directive $line] == 1} {incr directive_flag}
		if {[regexp $pattern_op_in_first_column $line] == 1} {incr op_in_first_column}
	}
	set label_flag [regexp {^[^\s]*(\s|$)*$} $line]

	if {[regexp {(^$|^\s$)} $line] == 1} {
		return {empty_string}
	} elseif {[regexp {(^;|^\s;)} $line] == 1} {
		return {comment}
	} elseif {$op_flag == 1 && $directive_flag == 0 && $label_flag == 0 && $op_in_first_column == 0} {
		return {instruction}
	} elseif {$op_flag == 0 && $directive_flag == 1 && $label_flag == 0 && $op_in_first_column == 0} {
		return {directive}
	} elseif {$op_flag == 0 && $directive_flag == 0 && $label_flag == 1 && $op_in_first_column == 0} {
		return {label}
	} else {
		if {$op_in_first_column == 1} {
			puts stderr "Error. Opcode in a first column: $line"
		} elseif {$op_flag == 0 && $label_flag == 0 && $op_in_first_column == 0} {
			puts stderr "Error: $line"
		}
		return {error}
	}
}

# Чтение строк из файла в список.
if [catch {open $asm_filename r} asm_file] {
	puts stderr $asm_file
	exit
} else {
	set asm [split [read $asm_file] "\n"]
	close $asm_file
}

# Вычисление адресов меток. Составление списка меток. (Первый проход)
set addr 0
set line_number 0
foreach line $asm {
	incr line_number
	switch [check_line $line] {
		{instruction} {
			regexp {^\s*([^\s]*)(\s*|$)(.*)} $line tmp1 mnemonic tmp2 arg
			set size [${mnemonic}_istr $arg 1 $line_number]
			set addr [expr {$addr + $size}]
		}
		{directive} {
			regexp {^\s*([^\s]*)(\s*|$)(.*)} $line tmp1 mnemonic tmp2 arg
			set size [${mnemonic}_directive $arg 1 $line_number]
			set addr [expr {$addr + $size}]
		}
		{label} {
			set label [string trim $line]
			# Если это не метка "-"
			if {$label != {-}} {
				set address_labels($label) $addr
				lappend labels $line
			}
		}
		{error} {
			exit
		}
	}
}

# Проверка отсутствия одинаковых меток.
for {set j 0} {$j < [llength $labels]} {incr j} {
	set label [lindex $labels $j]
	if {[llength [lsearch -all -exact $labels $label]] != 1} {
		puts "Error. Duplicate label: $label"
		exit
	}
}

set unnamed_label_addr 0
set addr 0
set line_number 0
foreach line $asm {
	incr line_number
	switch [check_line $line] {
		{instruction} {
			regexp {^\s*([^\s]*)(\s*|$)(.*)} $line tmp1 mnemonic tmp2 arg
			set size [${mnemonic}_istr $arg 2 $line_number]
			set addr [expr {$addr + $size}]
		}
		{directive} {
			regexp {^\s*([^\s]*)(\s*|$)(.*)} $line tmp1 mnemonic tmp2 arg
			set size [${mnemonic}_directive $arg 2 $line_number]
			set addr [expr {$addr + $size}]
		}
		{label} {
			# Если это метка "-"
			if {[string trim $line] == {-}} {
				set unnamed_label_addr $addr
			}
		}
		{error} {
			exit
		}
	}
}


if {[catch {open $out_filename w} out_file]} {
	puts stderr $txt_file
} else {
	fconfigure $out_file -translation binary -encoding binary
	set addr 0
	foreach line $output {
		incr addr
		incr addr
		set code 0
		foreach i [split $line {}] {
			set code [expr {$code * 2}]
			if {$i == {1}} {
				set code [expr {$code + 1}]
			}
		}
		#puts $out_file $line
		puts -nonewline $out_file [binary format s $code]
	}
	for {set i $addr} {$i < 16383} {set i [expr {$i + 2}]} {
		#puts $out_file 0000000000000000
		puts -nonewline $out_file [binary format s 0]
	}
	close $out_file
}

puts {Done.}