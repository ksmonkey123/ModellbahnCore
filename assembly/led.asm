    #include <p16f527.inc>

    global  led.init
    global  led.on
    global  led.off

LED_VECTOR  CODE
led.init:
    banksel PORTA
    bsf	    PORTA, RA1
    movlw   0xFD
    tris    PORTA
    return

led.on:
    banksel PORTA
    bsf	    PORTA, RA1
    return

led.off:
    banksel PORTA
    bcf	    PORTA, RA1
    return

    END