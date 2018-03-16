    #include	<p16f527.inc>
    radix	HEX
    
; =================== MULTIPLICATION FUNCTION =====================
; Performs unsigned multiplication of 2 bytes (indf and following)
; The result will be trimmed to the least significant byte. The
; effective formula for this function therefore is:
;
;       W = ((*(fsr) * *(fsr + 1)) % 256)
;
; On return W will hold the result. The two input arguments are not
; affected
    
    global	multiply
    
MUL_DATA    udata
index	res 1
 
MUL_VECTOR  code
multiply:
    banksel index
    pagesel multiply_loop
    incf    FSR, F
    movfw   INDF
    btfsc   STATUS, Z
    retlw   0
    movwf   index
    decf    FSR, F
    movlw   0
multiply_loop:
    addwf   INDF, W
    decfsz  index, F
    goto    multiply_loop
    return
    
    end