    #include    <p16f527.inc>
    __config    0x3b4
    radix       HEX
    #define     delay_settings_short .200
    #define     delay_settings_long .1000
    
    ; code for both center decoders. The pcb determines the used channel.
    ;   -> for channel 0 pull RB4 low
    ;   -> for channel 1 pull RB4 high
    
    
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
    extern  led.init, led.on, led.off
    extern  serial.in
    extern  switch_control.process, switch_control.init
    extern  delay
;</editor-fold>
;<editor-fold defaultstate="collapsed" desc="ram allocation">
PROGRAM_RAM udata
input           res 1
ch_select       res 1
delay_config    res 2
;</editor-fold>

PROGRAM_VECTOR  code
start:
    call   deactivate_specials
    call   led.init
    call   switch_control.init
    ; configure portc for switch control
    banksel 0
    movlw   0xff
    movwf   PORTC
    movlw   0xf0
    tris    PORTC
    ; determine channel to use
    movlw   0x00
    btfsc   PORTB, RB4
    movlw   0x01
    movwf   ch_select
    ; configure delay subroutine
    movlw   LOW(delay_settings_long)
    btfsc   ch_select, 0
    movlw   LOW(delay_settings_short)
    movwf   delay_config + 0
    movlw   HIGH(delay_settings_long)
    btfsc   ch_select, 0
    movlw   HIGH(delay_settings_short)
    movwf   delay_config + 1
    ; finish setup
    call    led.on
    movlw   delay_config
    movwf   FSR
    call    delay
    call    led.off
    goto    main
main:
    ; read to $input^
    movlw   input
    movwf   FSR
    call    serial.in
    ; select channel from $input
    movf    input, w
    btfsc   ch_select, 0
    swapf   input, w
    andlw   0x0f
    ; write output
    call    switch_control.process
    movwf   input
    movf    input, W
    btfsc   STATUS, Z
    goto    main        ; channel was unaltered
    ; apply output
    xorlw   0xff
    movwf   PORTC
    call    led.on
    movlw   delay_config
    movwf   FSR
    call    delay
    call    led.off
    movlw   0xff
    movwf   PORTC
    goto    main
    
    end