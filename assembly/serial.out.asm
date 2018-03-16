    #include <p16f527.inc>

; #############################################
; # SERIAL BROADCAST SENDER FUNCTION          #
; #############################################
; # TARGET HARDWARE			                  #
; #   - PIC16F527			                  #
; #   - MasterBoard Rev. A		              #
; #############################################
; # EXPORTED LABELS			                  #
; #   serial.out (main function)    	      #
; #     writes a 16-bit value to the output   #
; #	section of the expansion header	          #
; #   expansion.write.load_tris		          #
; #     returns the tris configuration needed #
; #     for operation.			              #
; #############################################
; # DESCRIPTION 			                  #
; #   This function handles sending data over #
; #   the serial bus pin (RB7).               #
; #############################################
; # I/O CONSIDERATIONS 			              #
; #   This function assumes RB7 to be         #
; #   configured as an output with a default  #
; #   value of 1. RB4-RB6 are not affected    #
; #   aside from the usual bcf/bsf side       #
; #   effects.                                #
; #############################################
; # MEMORY CONSIDERATIONS		              #
; #   This function requires 3 bytes of       #
; #   global RAM. bank/page-handling is       #
; #   is present. on return bank 0 is         #
; #   selected. No stack levels are needed.   #
; #############################################
; # FUNCTION CALL / RETURN CONTRACT	          #
; #   This function assumes FSR to contain    #
; #   the adress for the data byte.           #
; #   - FSR will not be modified              #
; #   - bank 0 will is selected on return     #
; #############################################
    
    global  serial.out

    extern  _global_0
    extern  _global_1
    extern  _global_2

    #define packet  _global_0
    #define parity  _global_1
    #define index   _global_2
    #define out     PORTB, RB7
    #define out_prt PORTB

SERIAL_OUT_VECTOR  code
serial.out:
    banksel out_prt
    bcf     out         ; > 0 (start RESET)
    movfw   INDF
    movwf   packet
    movwf   parity
    swapf   parity, W   ; > 0
    xorwf   parity, F
    rrf     parity, W
    xorwf   parity, F
    rlf     parity, W   ; > 0
    rrf     parity, F
    xorwf   parity, F
    rrf     parity, F
    bsf     out         ; > 1
    movlw   0x08
    movwf   index
    pagesel serial.out                          ; 0 us
serial.out_0:
    bcf     out         ; > 0 (start DATA)
    nop
    rrf     packet, F
    btfsc   STATUS, C
    bsf     out         ; > d
    goto    $+1
    nop
    bsf     out         ; > 1
    decfsz  index, F
    goto    serial.out_0
    nop                                         ; 96 us
    bcf     out         ; > 0 (start calibration segment)
    goto    $+1
    nop                                         ; 100 us
    bsf     out         ; > 0                   ; 101 us
    goto    $+1
    nop                                         
    bsf     out         ; > 1                   ; 105 us
    return

    end