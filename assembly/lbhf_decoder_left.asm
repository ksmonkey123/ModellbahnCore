    #include    <p16f527.inc>
    __config    0x3b4
    radix       HEX
    #define delay_settings_short .200
    #define delay_settings_long  .800
    
    
; LBHF DECODER LEFT
; all code fits in first page (no pagesel required)
; all files fit in first bank (no banksel required)
    
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
input               res 1
delay_config_short  res 2
delay_config_long   res 2
;</editor-fold>

SUBROUTINE_VEC  code    ;0x010
parse:
    ; 001 => 1010
    ; 010 => 1010
    ; 011 => 0110
    ; 100 => 0001
    
    ; parses the command
    movf    input, w
    andlw   0x07
    addwf   PCL, f
    retlw   b'0000' ; 000
    retlw   b'1010' ; 001 - C1
    retlw   b'1010' ; 010 - C2
    retlw   b'0110' ; 011 - C3
    retlw   b'0001' ; 100 - C4
    retlw   b'0000' ; 101
    retlw   b'0000' ; 110
    retlw   b'0000' ; 111

PROGRAM_VECTOR  code    ;0x100
start:
    call    deactivate_specials
    call    led.init
    call    switch_control.init
    banksel 0 ; just to be sure (specials uses other banks)
    ; configure portc for switch control
    movlw   0xff
    movwf   PORTC
    movlw   0xf0
    tris    PORTC
    ; configure short delay
    movlw   LOW(delay_settings_short)
    movwf   delay_config_short + 0
    movlw   HIGH(delay_settings_short)
    movwf   delay_config_short + 1
    ; configure long delay
    movlw   LOW(delay_settings_long)
    movwf   delay_config_long + 0
    movlw   HIGH(delay_settings_long)
    movwf   delay_config_long + 1
    ; setup complete
    call    led.on
    movlw   delay_config_long
    movwf   FSR
    call    delay
    call    led.off
main:
    movlw   input
    movwf   FSR
    call    serial.in
    movf    input, W
    call    parse
    call    switch_control.process
    movwf   input
    movf    input, W
    btfsc   STATUS, Z   ; change if any application is required
    goto    main
    xorlw   0xff
    movwf   PORTC
    movwf   input   ; cache current output in $input
    ; short delay
    call    led.on
    movlw   delay_config_short
    movwf   FSR
    call    delay
    ; disable modern drive (no. 2)
    movf    input, W ; load current output from $input (reading from $PORTC is not save!)
    iorlw   0xfc     ; disable no. 2 pins
    movwf   PORTC
    ; sleep long
    movlw   delay_config_long
    movwf   FSR
    call    led.off
    ; disable output
    movlw   0xff
    movwf   PORTC
    goto   main

    end