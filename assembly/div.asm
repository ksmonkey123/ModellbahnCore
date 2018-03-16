    #include    <p16f527.inc>
    radix       HEX
    
    global  divide
    
DIV_DATA    udata
count   res 1
arg     res 1
divisor res 1
 
DIV_VECTOR  code
divide:
    banksel count
    pagesel divide
    clrf    count
    movfw   INDF
    movwf   arg
    incf    FSR, F
    movfw   INDF
    movwf   divisor
    decf    FSR, F
    ; corner-case tests
    movf    arg, F
    btfsc   STATUS, Z   ; (0 / x) = 0
    retlw   0x00
    movf    divisor, F
    btfsc   STATUS, Z   ; (x / 0) = 255
    retlw   0xff
    ; perform normal division
div_loop:
    movfw   divisor
    subwf   arg, F
    btfss   STATUS, C
    goto    $+3
    incf    count, F
    goto    div_loop
    movfw   count
    return
    
    end