    #include    <p16f527.inc>
    __config    0x3bc
    radix       HEX
    
    ; #######################################
    ; # LEFT EXIT SIGNAL DECODER            #
    ; #######################################
    ; # Notes                               #
    ; #  - watchdog timer is enabled        #
    ; #  - watchdog ratio is 1:64 (1s)      #
    ; #######################################
    ; # I/O configuration                   #
    ; #  - C<0:3> track exits 1-4           #
    ; #  - C<4:6> entrance command code     #
    ; #######################################

    extern  deactivate_specials
    extern  serial.in

PROGRAM_RAM udata
input   res 1

PROGRAM_VECTOR  code 0x000
start:
    movlw   b'11111101' ; wdt ratio 1:32 ≈ 0.5s
    option
    clrwdt
    call    deactivate_specials
    banksel 0
    clrwdt  
    clrf    PORTC
    movlw   0x80
    tris    PORTC
    movlw   input
    movwf   FSR
main:
    call    serial.in
    clrwdt
    goto    process
process:
    call    resolve
    btfss   input, 7 ; [7] = 1 --> outbound
    andlw   0xf0
    btfss   input, 6 ; [6] = 1 --> inbound
    andlw   0x0f
    movwf   PORTC
    goto    main
resolve:
    ; the last 3 bit indicate the main path target
    movlw   0x07
    andwf   input, W
    addwf   PCL, F
    retlw   0x00    ; 000 => OFF
    retlw   0x11    ; 001 => 1
    retlw   0x22    ; 010 => 2
    retlw   0x34    ; 011 => 3
    retlw   0x48    ; 100 => 4
    retlw   0x00    ; 101 => ERROR
    retlw   0x00    ; 110 => ERROR
    retlw   0x00    ; 111 => ERROR

    end