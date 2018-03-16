    #include    <p16f527.inc>
    __config    0x3b4
    radix       HEX
    #define delay_settings .2000

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
    extern  serial.in, serial.in.init
    extern  switch_control.process, switch_control.init
    extern  portb.init
    extern  delay
;</editor-fold>

;<editor-fold defaultstate="collapsed" desc="ram allocation">
PROGRAM_RAM udata
input           res 1
delay_config    res 2
;</editor-fold>

SUBROUTINE_VEC  code    0x010
parse:
    andlw   0x0F
    addwf   PCL, F
    retlw   b'000000' ;-  0000
    retlw   b'000000' ;A  0001
    retlw   b'010101' ;B  0010
    retlw   b'010110' ;C  0011
    retlw   b'011000' ;D  0100
    retlw   b'100000' ;E  0101
    retlw   b'000000' ;F  0110
    retlw   b'000000' ;G  0111
    retlw   b'000000' ;H  1000
    retlw   b'000000' ;I  1001
    retlw   b'000000' ;J  1010
    retlw   b'000000' ;0  1011
    retlw   b'010101' ;BK 1100
    retlw   b'010110' ;CK 1101
    retlw   b'011000' ;DK 1110
    retlw   b'100000' ;EK 1111

PROGRAM_VECTOR  code    0x100
start:
    lcall   deactivate_specials
    lcall   portb.init
    lcall   serial.in.init
    lcall   led.init
    lcall   switch_control.init
    ; configure portc for switch control
    banksel 0
    movlw   0xff
    movwf   PORTC
    movlw   0xc0
    tris    PORTC
    banksel delay_config
    ; configure delay subroutine
    movlw   LOW(delay_settings)
    movwf   delay_config + 0
    movlw   HIGH(delay_settings)
    movwf   delay_config + 1
    lcall   led.on
    movlw   delay_config
    movwf   FSR
    lcall   delay
    lcall   led.off
main:
    movlw   input
    movwf   FSR
    lcall   serial.in
    movfw   INDF
    lcall   parse
    lcall   switch_control.process
    movwf   INDF
    pagesel main
    movfw   INDF
    btfsc   STATUS, Z   ; change if any application is required
    goto    main
    xorlw   0xff
    banksel PORTC
    movwf   PORTC
    lcall   led.on
    movlw   delay_config
    movwf   FSR
    lcall   delay
    lcall   led.off
    banksel PORTC
    movlw   0xff
    movwf   PORTC
    lgoto   main
    fill    (xorlw 0xff), (0x200 - $)

    end