    #include    <p16f527.inc>
    __config    0x3bc
    radix       HEX

; C<0:3> [DO] entrance main (GRYG [td])
; C<4:7> [DO] entrance aux  (YYGG [cw]) (driven over PNP, therefore invert I/O)
; B<4:6> [DI] entrance code (track no.)
; B<7>   [BI] rbhf command bus

;<editor-fold defaultstate="collapsed" desc="library imports">
    extern  deactivate_specials
    extern  serial.in
    extern  delay
;</editor-fold>
;<editor-fold defaultstate="collapsed" desc="ram allocation">
PROGRAM_RAM udata
rbhf    res 1
lbhf    res 1
output  res 1
cache   res 1
mask    res 1
delay_config res 2
;</editor-fold>

PROGRAM_VECTOR  code 0x000
start:
    movlw   b'11111101' ; wdt ratio 1:32 ≈ 0.5s
    option
    clrwdt
    call    deactivate_specials
    banksel 0
    clrwdt
    ; configure portc for output
    movlw   b'11110010'
    movwf   PORTC
    movlw   0x00
    tris    PORTC
    ; setup cache
    movlw   b'00000010'
    movwf   cache
    ; configure delay subroutine
    movlw   .40
    movwf   delay_config + 0
    clrf    delay_config + 1
    
main:
    ; read inputs
    clrwdt
    swapf   PORTB, W
    andlw   0x07
    movwf   lbhf
    movlw   rbhf
    movwf   FSR
    call    serial.in
    swapf   PORTB, W
    andlw   0x07
    xorwf   lbhf, W
    btfss   STATUS, Z
    goto    main
    movf    lbhf, F
    clrwdt
    ; case selection
    btfsc   STATUS, Z
    goto    handle_off
    movlw   0x01
    xorwf   lbhf, W
    btfsc   STATUS, Z
    goto    handle_track_1
    movlw   0x02
    xorwf   lbhf, W
    btfsc   STATUS, Z
    goto    handle_track_2
    movlw   0x03
    xorwf   lbhf, W
    btfsc   STATUS, Z
    goto    handle_track_3
    goto    handle_track_4
    
handle_off:
    movlw   b'00000010'
    movwf   output
    goto    publish
    
handle_track_1:
    movlw   b'00110011'
    andwf   rbhf, W
    xorlw   b'00100001'
    movlw   b'00110101'
    btfsc   STATUS, Z
    movlw   b'01010101'
    movwf   output
    goto    publish
    
handle_track_2:
    movlw   b'00110011'
    andwf   rbhf, W
    xorlw   b'00100010'
    movlw   b'00110101'
    btfsc   STATUS, Z
    movlw   b'01010101'
    movwf   output
    goto    publish
    
handle_track_3:
    movlw   b'11001100'
    andwf   rbhf, W
    xorlw   b'10000100'
    movlw   b'00111001'
    btfsc   STATUS, Z
    movlw   b'01011001'
    movwf   output
    goto    publish
    
handle_track_4:
    movlw   b'11001100'
    andwf   rbhf, W
    xorlw   b'10001000'
    movlw   b'00111001'
    btfsc   STATUS, Z
    movlw   b'01010001'
    btfss   rbhf, 0
    iorlw   b'10000000'
    btfss   rbhf, 1
    iorlw   b'10000000'
    movwf   output
    btfsc   output, 5
    bcf     output, 7
publish:
    ; determine if a switching action is required
    movf    output, W
    xorwf   cache, W
    btfsc   STATUS, Z
    goto    main ; last value is still ok!
    movwf   cache
    
    ; setup animation delay
    movlw   delay_config
    movwf   FSR
    
    ; root mask
    movlw   0x0f
    movwf   mask
    movlw   0x0f
    andwf   cache, W
    btfss   STATUS, Z
    clrf    mask
    ; STEP 1: kill all
    movf    output, W
    andwf   mask, W
    xorlw   0xf0
    movwf   PORTC
    ; WAIT 400 ms
    clrwdt
    call    delay
    call    delay
    clrwdt
    call    delay
    call    delay
    clrwdt
    call    delay
    call    delay
    clrwdt
    call    delay
    call    delay
    clrwdt
    call    delay
    call    delay
    clrwdt
    ; STEP 2: activate core
    movlw   b'00110011'
    iorwf   mask, F
    movf    output, W
    andwf   mask, W
    xorlw   0xf0
    movwf   PORTC
    ; WAIT 40 ms
    call    delay
    clrwdt
    ; STEP 3: add secondary
    movlw   b'01111111'
    iorwf   mask, F
    movf    output, W
    andwf   mask, W
    xorlw   0xf0
    movwf   PORTC
    ; WAIT 40 ms
    call    delay
    clrwdt
    ; STEP 3: add secondary
    movlw   b'11111111'
    iorwf   mask, F
    movf    output, W
    andwf   mask, W
    xorlw   0xf0
    movwf   PORTC
    ; WAIT 200 ms
    clrwdt
    call    delay
    call    delay
    clrwdt
    call    delay
    call    delay
    call    delay
    clrwdt
    
    ; update cache
    movf    output, W
    movwf   cache
    
    goto    main
    
    
    end