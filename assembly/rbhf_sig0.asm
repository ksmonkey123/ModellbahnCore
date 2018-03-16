    #include    <p16f527.inc>
    __config    0x3bc
    radix       HEX

; C<0:4> [DO] exits 1-4 (C<4> is fast mode for track 4 (set w/ C<3>)
; C<5:7> [DO] inner entrance code (track no.)
; B<4:6> [DO] outer entrance code (track no.)
; B<7>   [BI] rbhf command bus

    extern  deactivate_specials
    extern  serial.in
PROGRAM_RAM udata
rbhf    res 1
exit    res 1
enter   res 1
temp    res 1

PROGRAM_VECTOR  code 0x000
start:
    movlw   0xfd ; wdt ratio 1:32 ≈ 0.5s
    option
    clrwdt
    call    deactivate_specials
    banksel 0
    clrwdt
    clrf    PORTB
    movlw   0x8f
    tris    PORTB
    clrf    PORTC
    movlw   0x00
    tris    PORTC
    movlw   rbhf
    movwf   FSR
main:
    call    serial.in
    clrwdt
    movf    INDF, w
    call    decode_exit
    movwf   exit
    movf    INDF, w
    call    decode_enter
    movwf   enter
process:
    movlw   0x1c
    btfss   rbhf, 5
    andwf   exit, f
    movlw   0x03
    btfss   rbhf, 7
    andwf   exit, f
    movlw   0x70
    btfss   rbhf, 4
    andwf   enter, f
    movlw   0x07
    btfss   rbhf, 6
    andwf   enter, f
publish:
    movf    enter, w
    movwf   PORTB
    swapf   enter, f
    rlf     enter, w
    andlw   0xe0
    iorwf   exit, w
    movwf   PORTC
    goto    main

decode_exit:
    andlw   0x0f
    addwf   PCL, f
    retlw   b'00000' ; 0000 => OFF
    retlw   b'00001' ; 0001 => A-1
    retlw   b'00010' ; 0010 => A-2
    retlw   b'00000' ; 0011 => ERROR
    retlw   b'00100' ; 0100 => B-3
    retlw   b'00101' ; 0101 => A-1 + B-3
    retlw   b'00110' ; 0110 => A-2 + B-3
    retlw   b'00100' ; 0111 => A-3
    retlw   b'10000' ; 1000 => B-4
    retlw   b'10001' ; 1001 => A-1 + B-4
    retlw   b'10010' ; 1010 => A-2 + B-4
    retlw   b'01000' ; 1011 => A-4
    retlw   b'00000' ; 1100 => ERROR
    retlw   b'00001' ; 1101 => B-1
    retlw   b'00010' ; 1110 => B-2
    retlw   b'00000' ; 1111 => ERROR
decode_enter:
    andlw   0x0f
    addwf   PCL, f
    retlw   0x00 ; 0000 => OFF
    retlw   0x01 ; 0001 => A-1
    retlw   0x02 ; 0010 => A-2
    retlw   0x00 ; 0011 => ERROR
    retlw   0x30 ; 0100 => B-3
    retlw   0x31 ; 0101 => A-1 + B-3
    retlw   0x32 ; 0110 => A-2 + B-3
    retlw   0x03 ; 0111 => A-3
    retlw   0x40 ; 1000 => B-4
    retlw   0x41 ; 1001 => A-1 + B-4
    retlw   0x42 ; 1010 => A-2 + B-4
    retlw   0x04 ; 1011 => A-4
    retlw   0x00 ; 1100 => ERROR
    retlw   0x10 ; 1101 => B-1
    retlw   0x20 ; 1110 => B-2
    retlw   0x00 ; 1111 => ERROR

    end