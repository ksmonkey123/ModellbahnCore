    #include <p16f527.inc>

    global  deactivate_specials

SPECIALS_VECTOR CODE

deactivate_specials:
    banksel CM1CON0
    bcf	    CM1CON0, C1ON
    bcf	    CM2CON0, C2ON
    banksel ANSEL
    clrf    ANSEL
    banksel VRCON
    bcf	    VRCON, VREN
    banksel INTCON1
    bcf	    INTCON0, GIE
    clrf    INTCON1
    banksel 0
    return

    END