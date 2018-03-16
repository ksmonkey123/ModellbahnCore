    #include    <p16f527.inc>
    __config    0x3bc
    radix       HEX
    
; C<0:2> [DO] inner entrance main (GRY [td])
; C<4:7> [DO] inner entrance aux  (YYGG [cw])
; B<4:6> [DI] inner entrance code (track no.)
; B<7>   [BI] lbhf command bus

;<editor-fold defaultstate="collapsed" desc="base vectors">
RESET_VECTOR    code    0x3ff
    goto    0x000
START_VECTOR    code    0x000
    lgoto   start
IRUPT_VECTOR    code    0x004
    retfie
;</editor-fold>
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

PROGRAM_VECTOR  code
start:
    movlw   b'11111101' ; wdt ratio 1:32 ≈ 0.5s
    option
    clrwdt
    call    deactivate_specials
    banksel 0
    ; configure ports
    movlw   b'11110010'
    movwf   PORTC
    movlw   b'00001000' ; C<3> is signal power supply (isolated)
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
    movlw   lbhf
    movwf   FSR
    call    serial.in
    clrwdt
    movlw   0xc7
    andwf   INDF, f
    swapf   PORTB, W
    andlw   0x07
    ; case selection
    addwf   PCL, F
    goto    handle_off      ; case 0
    goto    handle_track_1  ; case 1
    goto    handle_track_2  ; case 2
    goto    handle_track_3  ; case 3
    goto    handle_track_4  ; case 4
    nop                     ; case 5
    nop                     ; case 6
    nop                     ; case 7
    
handle_off:
    movlw   b'00000010'
    movwf   output
    goto    publish
    
handle_track_1:
    movf    lbhf, W
    xorlw   b'10000001'
    movlw   b'00110101'
    btfsc   STATUS, Z
    movlw   b'01010101'
    movwf   output
    goto    publish
    
handle_track_2:
    movf    lbhf, W
    xorlw   b'10000010'
    movlw   b'00110101'
    btfsc   STATUS, Z
    movlw   b'01010101'
    movwf   output
    goto    publish
    
handle_track_3:
    movf    lbhf, W
    xorlw   b'10000011'
    movlw   b'00110101'
    btfsc   STATUS, Z
    movlw   b'11010101'
    movwf   output
    goto    publish
    
handle_track_4:
    movf    lbhf, W
    xorlw   b'10000100'
    movlw   b'00110101'
    btfsc   STATUS, Z
    movlw   b'11000101'
    movwf   output
    
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