    #include    <p16f527.inc>
    radix       HEX

    global  serial.out
    global  serial.out.init
    global  serial.out.tris

    extern  _global_0
    extern  _global_1
    extern  _global_2

    #define index   _global_0
    #define message _global_1
    #define temp    _global_2

SERIAL_OUT_VECTOR   code
serial.out.tris:
    retlw   0x7f
serial.out.init:
    banksel PORTB
    bsf     PORTB, RB7
    return
serial.out:
    pagesel serial.out
    banksel PORTB
    movfw   INDF
    movwf   message
    ; send start trigger (20µs LOW)
    bcf     PORTB, RB7
    movlw   0x06
    movwf   temp
    decfsz  temp, F
    goto    $-1
    bsf     PORTB, RB7
    