    #include    <p16f527.inc>
    radix       HEX

; ================= PORTB BUFFERED OUTPUT MANAGER =================
;   - Version: 1.0, 2016-01-02
;   - Author:  Andreas Wälchli
;
; This utility provides functions for double-buffered write access
; to PORTB and its tristate configuration. This also allows changes
; to the tristate setting of a single pin without having to know the
; tristate settings of all other pins.

    global  portb.init
    global  portb.tris.copy
    global  portb.tris.set
    global  portb.tris.unset
    global  portb.tris.flush
    global  portb.data.copy
    global  portb.data.set
    global  portb.data.unset
    global  portb.data.flush

PORTB_RAM   udata
portb.tris  res 1
portb.data  res 1

PORTB_VEC   code
portb.init:
    banksel portb.tris
    movlw   0xff
    movwf   portb.tris
    clrf    portb.data
    return
portb.tris.copy:
    banksel portb.tris
    movwf   portb.tris
    return
portb.tris.set:
    banksel portb.tris
    iorwf   portb.tris, F
    return
portb.tris.unset:
    banksel portb.tris
    xorlw   0xff
    andwf   portb.tris, F
    return
portb.tris.flush:
    banksel portb.tris
    movfw   portb.tris
    banksel PORTB
    tris    PORTB
    return
portb.data.copy:
    banksel portb.data
    movwf   portb.data
    return
portb.data.set:
    banksel portb.data
    iorwf   portb.data, F
    return
portb.data.unset:
    banksel portb.data
    xorlw   0xff
    andwf   portb.data, F
    return
portb.data.flush:
    banksel portb.data
    movfw   portb.data
    banksel PORTB
    movwf   PORTB
    return

    end