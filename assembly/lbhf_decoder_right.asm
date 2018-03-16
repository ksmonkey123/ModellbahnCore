    #include    <p16f527.inc>
    __config    0x3b4
    radix       HEX
    #define delay_settings .200

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
delay_config    res 2
temp            res 1
;</editor-fold>

SUBROUTINE_VEC  code    0x010
parse:
   ; parses the command
   movlw    0x3b
   andwf    input, f
   rrf      input, w
   andlw    0x1c
   movwf    temp
   movf     input, w
   andlw    0x03
   iorwf    temp, w
   addwf    PCL, f
   retlw    b'000000' ; 0.00.00
   retlw    b'011001' ; 0.00.01
   retlw    b'000101' ; 0.00.10
   retlw    b'000000' ; 0.00.11
   retlw    b'011010' ; 0.01.00
   retlw    b'000000' ; 0.01.01
   retlw    b'000000' ; 0.01.10
   retlw    b'011010' ; 0.01.11
   retlw    b'000110' ; 0.10.00
   retlw    b'000000' ; 0.10.01
   retlw    b'000000' ; 0.10.10
   retlw    b'000110' ; 0.10.11
   retlw    b'000000' ; 0.11.00
   retlw    b'000000' ; 0.11.01
   retlw    b'000000' ; 0.11.10
   retlw    b'000000' ; 0.11.11
   retlw    b'100000' ; 1.00.00
   retlw    b'000000' ; 1.00.01
   retlw    b'100101' ; 1.00.10
   retlw    b'100000' ; 1.00.11
   retlw    b'000000' ; 1.01.00
   retlw    b'000000' ; 1.01.01
   retlw    b'000000' ; 1.01.10
   retlw    b'000000' ; 1.01.11
   retlw    b'100110' ; 1.10.00
   retlw    b'000000' ; 1.10.01
   retlw    b'000000' ; 1.10.10
   retlw    b'100110' ; 1.10.11
   retlw    b'000000' ; 1.11.00
   retlw    b'000000' ; 1.11.01
   retlw    b'000000' ; 1.11.10
   retlw    b'000000' ; 1.11.11
    

PROGRAM_VECTOR  code    0x100
start:
    lcall   deactivate_specials
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
    movf    INDF, W
    lcall   parse
    lcall   switch_control.process
    movwf   INDF
    pagesel main
    movf    INDF, W
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
    
; ============================================================================
    
    fill    (xorlw 0xff), (0x200 - $)

    end