    #include <p16f527.inc>

    extern _global_0
    extern _global_1
    extern _global_2
    extern _global_3
    
    #define calib    _global_0
    #define misses   _global_1
    #define hits     _global_2
    #define index    _global_3
    
    #define HIT_COUNT   0x05
    #define MISS_COUNT  0x05
    
    global calibration
    
AUTOCAL_VECTOR CODE

calibration:
    movlw   0x80
    movwf   calib
    movwf   OSCCAL
    movlw   HIT_COUNT
    movwf   hits
calibration.restart:
    movlw   MISS_COUNT
    movwf   misses
calibration.loop:
    btfsc   PORTB, RB7
    goto    calibration.loop
    goto    $+1
    btfsc   PORTB, RB7
    goto    calibration.loop
    goto    $+1
    btfsc   PORTB, RB7
    goto    calibration.loop
    movlw   .34
    movwf   index
    decfsz  index, f
    goto    $-1
    goto    $+1
    btfss   PORTB, RB7
    goto    calibration.hit
    
calibration.miss:
    movlw   HIT_COUNT
    movwf   hits
    decfsz  misses, f
    goto    calibration.loop
    incf    calib, f
    incf    calib, f
    movf    calib, w
    movwf   OSCCAL
    goto    calibration.restart
    
calibration.hit:
    decfsz  hits, f
    goto    calibration.restart
    return
    
    END