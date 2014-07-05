proc lit_istr {arg pass line_number} {
	global output unnamed_label_addr
	upvar address_labels labels
	if {[string trim $arg] == {}} {
		puts "Error. Incorrect argument: $arg (line $line_number)"
		exit
	}
	if {$pass == 2} {
		if {[string index $arg 0] == {#}} {
			set number [string range $arg 1 end]
		} elseif {[string index $arg 0] == {$}} {
			set number $labels([string range $arg 1 end])
		} elseif {[string index $arg 0] == {-}} {
			set number $unnamed_label_addr
		} else {
			puts "Error. Incorrect argument: $arg (line $line_number)"
			exit
		}
		set number_bin [string range [format %015b $number] end-14 end]
		if {$number > 32767} {
			puts "Warning. Argument out of range: $arg (line $line_number)"
		}
		lappend output "1$number_bin"
	}
	return 2
}

proc jump_istr {arg pass line_number} {
	global output unnamed_label_addr
	upvar address_labels labels
	if {[string trim $arg] == {}} {
		puts "Error. Incorrect argument: $arg (line $line_number)"
		exit
	}
	if {$pass == 2} {
		if {[string index $arg 0] == {#}} {
			set number [string range $arg 1 end]
		} elseif {[string index $arg 0] == {$}} {
			set number $labels([string range $arg 1 end])
		} elseif {[string index $arg 0] == {-}} {
			set number $unnamed_label_addr
		} else {
			puts "Error. Incorrect argument: $arg (line $line_number)"
			exit
		}
		if {[expr {$number % 2}] == 1} {
			puts "Error. Jump to unaligned address: $arg (line $line_number)"
			exit
		}
		if {$number > 16383} {
			puts "Warning. Argument out of range: $arg (line $line_number)"
		}
		set number_bin [string range [format %015b $number] end-13 end-1]
		lappend output "000$number_bin"
	}
	return 2
}

proc cond_jump_istr {arg pass line_number} {
	global output unnamed_label_addr
	upvar address_labels labels
	if {[string trim $arg] == {}} {
		puts "Error. Incorrect argument: $arg (line $line_number)"
		exit
	}
	if {$pass == 2} {
		if {[string index $arg 0] == {#}} {
			set number [string range $arg 1 end]
		} elseif {[string index $arg 0] == {$}} {
			set number $labels([string range $arg 1 end])
		} elseif {[string index $arg 0] == {-}} {
			set number $unnamed_label_addr
		} else {
			puts "Error. Incorrect argument: $arg (line $line_number)"
			exit
		}
		if {[expr {$number % 2}] == 1} {
			puts "Error. Jump to unaligned address: $arg (line $line_number)"
			exit
		}
		if {$number > 16383} {
			puts "Warning. Argument out of range: $arg (line $line_number)"
		}
		set number_bin [string range [format %015b $number] end-13 end-1]
		lappend output "001$number_bin"
	}
	return 2
}

proc call_istr {arg pass line_number} {
	global output unnamed_label_addr
	upvar address_labels labels
	if {[string trim $arg] == {}} {
		puts "Error. Incorrect argument: $arg (line $line_number)"
		exit
	}
	if {$pass == 2} {
		if {[string index $arg 0] == {#}} {
			set number [string range $arg 1 end]
		} elseif {[string index $arg 0] == {$}} {
			set number $labels([string range $arg 1 end])
		} elseif {[string index $arg 0] == {-}} {
			set number $unnamed_label_addr
		} else {
			puts "Error. Incorrect argument: $arg (line $line_number)"
			exit
		}
		if {[expr {$number % 2}] == 1} {
			puts "Error. Jump to unaligned address: $arg (line $line_number)"
			exit
		}
		if {$number > 16383} {
			puts "Warning. Argument out of range: $arg (line $line_number)"
		}
		set number_bin [string range [format %015b $number] end-13 end-1]
		lappend output "010$number_bin"
	}
	return 2
}

#################################################################################################################

proc alu_istr {arg pass line_number} {
	global output
	if {[string trim $arg] == {}} {
		puts "Error. Incorrect argument: $arg (line $line_number)"
		exit
	}
	if {$pass == 2} {
		set code [regsub -all {[_\s]} $arg {}]

		# Не работет условие [regexp {[^01]} $code] != 0
		if {[regexp {[^01]} $code] != 0 && [string length $code] != 13} {
			puts "Error. Incorrect argument: $arg (line $line_number)"
			exit
		}
		lappend output "011$code"
	}
	return 2
}

#################################################################################################################
# n -- n1 n2

proc dup_istr {arg pass line_number} {
	global output
	if {[string trim $arg] != {}} {
		puts "Error. (line $line_number)"
		exit
	}
	if {$pass == 2} {
		lappend output {0110000010000001}
	}
	return 2
}

#################################################################################################################
# n1 n2 -- n

proc add_istr {arg pass line_number} {
	global output
	if {[string trim $arg] != {}} {
		puts "Error. (line $line_number)"
		exit
	}
	if {$pass == 2} {
		lappend output {0110001000000011}
	}
	return 2
}

proc and_istr {arg pass line_number} {
	global output
	if {[string trim $arg] != {}} {
		puts "Error. (line $line_number)"
		exit
	}
	if {$pass == 2} {
		lappend output {0110001100000011}
	}
	return 2
}

proc or_istr {arg pass line_number} {
	global output
	if {[string trim $arg] != {}} {
		puts "Error. (line $line_number)"
		exit
	}
	if {$pass == 2} {
		lappend output {0110010000000011}
	}
	return 2
}

proc =_istr {arg pass line_number} {
	global output
	if {[string trim $arg] != {}} {
		puts "Error. (line $line_number)"
		exit
	}
	if {$pass == 2} {
		lappend output {0110011100000011}
	}
	return 2
}

proc <_istr {arg pass line_number} {
	global output
	if {[string trim $arg] != {}} {
		puts "Error. (line $line_number)"
		exit
	}
	if {$pass == 2} {
		lappend output {0110100000000011}
	}
	return 2
}

proc u<_istr {arg pass line_number} {
	global output
	if {[string trim $arg] != {}} {
		puts "Error. (line $line_number)"
		exit
	}
	if {$pass == 2} {
		lappend output {0110111100000011}
	}
	return 2
}

proc _A!__istr {arg pass line_number} {
	global output
	if {[string trim $arg] != {}} {
		puts "Error. (line $line_number)"
		exit
	}
	if {$pass == 2} {
		lappend output {0110000100100011}
	}
	return 2
}

proc A!_istr {arg pass line_number} {
	global output
	if {[string trim $arg] != {}} {
		puts "Error. (line $line_number)"
		exit
	}
	if {$pass == 2} {
		lappend output {0110000100100011}
		lappend output {0110000100000011}
	}
	return 4
}

proc nip_istr {arg pass line_number} {
	global output
	if {[string trim $arg] != {}} {
		puts "Error. (line $line_number)"
		exit
	}
	if {$pass == 2} {
		lappend output {0110000000000011}
	}
	return 2
}

proc drop_istr {arg pass line_number} {
	global output
	if {[string trim $arg] != {}} {
		puts "Error. (line $line_number)"
		exit
	}
	if {$pass == 2} {
		lappend output {0110000100000011}
	}
	return 2
}

#################################################################################################################
# n1 -- n

proc invert_istr {arg pass line_number} {
	global output
	if {[string trim $arg] != {}} {
		puts "Error. (line $line_number)"
		exit
	}
	if {$pass == 2} {
		lappend output {0110011000000000}
	}
	return 2
}

proc 1-_istr {arg pass line_number} {
	global output
	if {[string trim $arg] != {}} {
		puts "Error. (line $line_number)"
		exit
	}
	if {$pass == 2} {
		lappend output {0110101000000000}
	}
	return 2
}

proc 1_lshift_istr {arg pass line_number} {
	global output
	if {[string trim $arg] != {}} {
		puts "Error. (line $line_number)"
		exit
	}
	if {$pass == 2} {
		lappend output {0110110100000000}
	}
	return 2
}

proc 1_rshift_istr {arg pass line_number} {
	global output
	if {[string trim $arg] != {}} {
		puts "Error. (line $line_number)"
		exit
	}
	if {$pass == 2} {
		lappend output {0110100100000000}
	}
	return 2
}

proc swap_bytes_istr {arg pass line_number} {
	global output
	if {[string trim $arg] != {}} {
		puts "Error. (line $line_number)"
		exit
	}
	if {$pass == 2} {
		lappend output {0110010100000000}
	}
	return 2
}

proc A@_istr {arg pass line_number} {
	global output
	if {[string trim $arg] != {}} {
		puts "Error. (line $line_number)"
		exit
	}
	if {$pass == 2} {
		lappend output {0110110000000000}
	}
	return 2
}

proc swap_istr {arg pass line_number} {
	global output
	if {[string trim $arg] != {}} {
		puts "Error. (line $line_number)"
		exit
	}
	if {$pass == 2} {
		lappend output {0110000110000000}
	}
	return 2
}

#################################################################################################################
# -- n

proc depth_istr {arg pass line_number} {
	global output
	if {[string trim $arg] != {}} {
		puts "Error. (line $line_number)"
		exit
	}
	if {$pass == 2} {
		lappend output {0110111010000001}
	}
	return 2
}

proc r@_istr {arg pass line_number} {
	global output
	if {[string trim $arg] != {}} {
		puts "Error. (line $line_number)"
		exit
	}
	if {$pass == 2} {
		lappend output {0110101111000001}
	}
	return 2
}

#################################################################################################################

proc over_istr {arg pass line_number} {
	global output
	if {[string trim $arg] != {}} {
		puts "Error. (line $line_number)"
		exit
	}
	if {$pass == 2} {
		lappend output {0110000110000001}
	}
	return 2
}

proc >r_istr {arg pass line_number} {
	global output
	if {[string trim $arg] != {}} {
		puts "Error. (line $line_number)"
		exit
	}
	if {$pass == 2} {
		lappend output {0110000101000111}
	}
	return 2
}

proc r>_istr {arg pass line_number} {
	global output
	if {[string trim $arg] != {}} {
		puts "Error. (line $line_number)"
		exit
	}
	if {$pass == 2} {
		lappend output {0110101111001101}
	}
	return 2
}

proc return_istr {arg pass line_number} {
	global output
	if {[string trim $arg] != {}} {
		puts "Error. (line $line_number)"
		exit
	}
	if {$pass == 2} {
		lappend output {0111000000001100}
	}
	return 2
}

#################################################################################################################

proc !word_directive {arg pass line_number} {
	global output unnamed_label_addr
	upvar address_labels labels
	if {[string trim $arg] == {}} {
		puts "Error. Incorrect argument: $arg (line $line_number)"
		exit
	}
	if {$pass == 2} {
		if {[string index $arg 0] == {#}} {
			set number [string range $arg 1 end]
		} elseif {[string index $arg 0] == {$}} {
			set number $labels([string range $arg 1 end])
		} elseif {[string index $arg 0] == {-}} {
			set number $unnamed_label_addr
		} else {
			puts "Error. Incorrect argument: $arg (line $line_number)"
			exit
		}
		set number_bin [string range [format %016b $number] end-15 end]
		if {$number > 65535} {
			puts "Warning. Argument out of range: $arg (line $line_number)"
		}
		lappend output "$number_bin"
	}
	return 2
}

proc !string_directive {arg pass line_number} {
	global output
	if {$arg == {}} {
		puts "Error. Incorrect argument: $arg (line $line_number)"
		exit
	}
	if {[regexp {"(.*)"} $arg x1 str] == 0} {
		puts "Error. Incorrect argument: $arg (line $line_number)"
		exit
	}
	set str_len [string length $str]
	if {$pass == 2} {
		set pos 0
		while {$pos < $str_len} {
			set chr1 [string index $str $pos]
			incr pos
			set chr2 [string index $str $pos]
			incr pos
			if {$chr2 == {}} {
				lappend output "00000000[format %08b [scan $chr1 %c]]"
			} else {
				lappend output [format %08b [scan $chr2 %c]][format %08b [scan $chr1 %c]]
			}
		}
		if {[expr {($str_len + 1) % 2}] == 1} {
			lappend output {0000000000000000}
		}
		lappend "	!string \"$str\""
	}
	if {[expr {($str_len + 1) % 2}] == 1} {
		return [expr {$str_len + 2}]
	} else {
		return [expr {$str_len + 1}]
	}
}

proc !head_directive {arg pass line_number} {
	global output
	if {[string trim $arg] == {}} {
		puts "Error. Incorrect argument: $arg (line $line_number)"
		exit
	}
	if {[regexp {"(.*)"\s(.*)} $arg x1 str num] == 0} {
		puts "Error. Incorrect argument: $arg (line $line_number)"
		exit
	}
	set str_len [string length $str]
	if {$str == {} || [string trim $num] == {}} {
		puts "Error. Incorrect argument: $arg (line $line_number)"
		exit
	}

	set number_bin [string range [format %08b $num] end-7 end]
	if {$num > 255} {
		puts "Warning. Argument out of range: $arg (line $line_number)"
	}

	if {$pass == 2} {
		set _code_ {00000000}
		for {set pos 0} {$pos < $str_len} {incr pos} {
			set chr [string index $str $pos]
			lappend _code_ [format %08b [scan $chr %c]]
		}
		lappend _code_ {00000000}
		lappend _code_ $number_bin

		if {[expr {[llength $_code_] % 2}] == 1} {
			set _code_ "00000000 $_code_"
		}

		for {set i 0} {$i < [llength $_code_]} {incr i; incr i} {
			lappend output [lindex $_code_ $i+1][lindex $_code_ $i]
		}
	}

	if {[expr {($str_len) % 2}] == 1} {
		return [expr {$str_len + 3}]
	} else {
		return [expr {$str_len + 4}]
	}
}

proc !allot_directive {arg pass line_number} {
	global output
	if {[string is integer -strict $arg] == 0} {
		puts "Error. Incorrect argument: $arg (line $line_number)"
		exit
	}
	if {[expr {$arg %2}] == 1} {
		puts "Error. Odd argument: $arg (line $line_number)"
		exit
	}
	if {$pass == 2} {
		for {set i 0} {$i < $arg} {incr i; incr i} {
			lappend output {0000000000000000}
		}
	}
	return $arg
}
