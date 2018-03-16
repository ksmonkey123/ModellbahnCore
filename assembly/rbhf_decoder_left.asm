    #include    <p16f527.inc>
    __config    0x3b4
    radix       HEX
    #define     delay_settings_short .200
    #define     delay_settings_long  .800
    
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
    extern  calibration
;</editor-fold>
;<editor-fold defaultstate="collapsed" desc="ram allocation">
PROGRAM_RAM udata
input              res 1
temp               res 1
cmd                res 1
delay_config_short res 2
delay_config_long  res 2
;</editor-fold>

PROGRAM_VECTOR  code
start:
    call    deactivate_specials
    call    led.init
    call    switch_control.init
    ; configure portc for switch control
    banksel 0
    movlw   0xff
    movwf   PORTC
    movlw   0xc0
    tris    PORTC
    ;configure short delay
    movlw   LOW(delay_settings_short)
    movwf   delay_config_short + 0
    movlw   HIGH(delay_settings_short)
    movwf   delay_config_short + 1
    ;configure long delay
    movlw   LOW(delay_settings_long)
    movwf   delay_config_long + 0
    movlw   HIGH(delay_settings_long)
    movwf   delay_config_long + 1
    call    led.on
    movlw   delay_config_short
    movwf   FSR
    call    delay
    call    calibration
    call    led.off

main:
    movlw   input
    movwf   FSR
    call    serial.in
    movf    INDF, W
    call    parse
    call    switch_control.process
    movwf   input
    movf    input, W
    btfsc   STATUS, Z ; check if any application is required
    goto    main
    xorlw   0xff
    movwf   PORTC
    movwf   input ; cache current output in $input
    ; short delay
    call    led.on
    movlw   delay_config_short
    movwf   FSR
    call    delay
    ; disable modern drives (no. 1 & no. 3)
    movf    input, W ; load current output from $input
    iorlw   0xf3 ; disable no. 1/3 pins
    movwf   PORTC
    ; sleep long
    movlw   delay_config_long
    movwf   FSR
    call    led.off
    ; disable
    movlw   0xff
    movwf   PORTC
    goto    main
    
PARSE_VEC   code 
parse:
    andlw   0x0f
    addwf   PCL, f
    retlw   b'000000' ; 0000 => OFF
    retlw   b'000101' ; 0001 => A-1
    retlw   b'001001' ; 0010 => A-2
    retlw   b'000000' ; 0011 => ERROR
    retlw   b'010000' ; 0100 => B-3
    retlw   b'010101' ; 0101 => A-1 + B-3
    retlw   b'011001' ; 0110 => A-2 + B-3
    retlw   b'010000' ; 0111 => A-3
    retlw   b'100000' ; 1000 => B-4
    retlw   b'100101' ; 1001 => A-1 + B-4
    retlw   b'101001' ; 1010 => A-2 + B-4
    retlw   b'100000' ; 1011 => A-4
    retlw   b'000000' ; 1100 => ERROR
    retlw   b'000110' ; 1101 => B-1
    retlw   b'001010' ; 1110 => B-2
    retlw   b'000000' ; 1111 => ERROR
    end