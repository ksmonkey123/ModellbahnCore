    #include <p16f527.inc>

    global  delay

DELAY_DATA  UDATA
millis_low  res	1
millis_high res	1
sub_count   res	1

DELAY_VECTOR	CODE
delay:	; leading overhead (including call): 10us
    pagesel delay
    banksel millis_low
    movfw   INDF
    movwf   millis_low
    incf    FSR, F
    movfw   INDF
    movwf   millis_high
    decf    FSR, F
    goto    delay_1ms
virtual_overhead: ; virtual overhead of 10us
    goto    $+1
    goto    $+1
    goto    $+1
    goto    $+1
    goto    $+1
    goto    delay_1ms
delay_1ms: ; 973us
    movlw   0xC2 ; loop config
    movwf   sub_count
    decfsz  sub_count, F
    goto    $+2
    goto    $+2 ; end
    goto    $-3
    goto    $+1
    goto    sub_delay_return
sub_delay_return: ; trailing overhead (including return): 13us
    movf    millis_low, F
    btfsc   STATUS, Z
    decf    millis_high, F
    decf    millis_low, F
    btfss   STATUS, Z
    goto    $+5 ; loop
    movf    millis_high, F
    btfsc   STATUS, Z
    goto    $+5 ; terminate
    goto    $+3
    goto    $+1
    goto    $+1
    goto    virtual_overhead
    nop
    return

    END