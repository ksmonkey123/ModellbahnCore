    #include    <p16f527.inc>

    extern  _global_0
    extern  _global_1
    extern  _global_2
    extern  _global_3

    #define index   _global_0
    #define packet  _global_1
    #define packet2 _global_2
    #define parity  _global_3
    #define in_prt  PORTB
    #define in      PORTB, RB7

    global  serial.in

SERIAL_IN_VECTOR code
serial.in:
    banksel in_prt
    pagesel read
    goto    read_block
read:           ; wait for reset block (0001) and prepare memory
    btfsc   in          ; 0
    goto    read
    movlw   0x09        ; i = 9
    movwf   index
    btfsc   in          ; 0
    goto    read
    clrf    packet      ; packet = 0
    bcf     STATUS, C
    btfsc   in          ; 0
    goto    read
    clrf    parity      ; parity = 0
    nop
    btfss   in          ; 1
    goto    read
    nop ; NOTE: PCL update times are broken in simulator! This is correct!
loop:
    rrf     packet, F   ; roll over to next bit
    btfsc   in          ; 0
    goto    read
    decf    index, F    ; i--
    movlw   loop        ; program jump to loop head
    btfsc   in          ; d
    bsf     STATUS, C   ; set carry bit for next data
    btfsc   STATUS, Z   ; check if i == 0 (zero flag not touched since decrement
    movlw   validate    ; program jump to validation
    btfsc   in          ; 1 --> valid, go to target
    movwf   PCL
    goto    read        ; invalid, abort
validate: ; current version does not validate checksum. Presence check only
    movfw   packet      ; copy data into target file
    movwf   INDF
    return
    
read_block:             ; perform multiple reads for error elimination
    call    read        ; READ 1
promote:                ; cache packet (target for retries)
    movf    packet, W
    movwf   packet2
    call    read        ; READ 2
    movf    packet, W
    xorwf   packet2, W  ; compare packets
    btfss   STATUS, Z   ; data1 === data2 --> continue
    goto    promote     ; not ok, restart
    ; 2 good
    call    read        ; READ 3
    movf    packet, W
    xorwf   packet2, W  ; compare packets
    btfss   STATUS, Z   ; data1 === data2 === data3 --> all good
    goto    promote     ; not ok, restart
    ; 3 good -> return
    return              ; data was already copied into target file by READ 3
    
    end