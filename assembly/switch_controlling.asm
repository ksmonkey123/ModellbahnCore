    #include    <p16f527.inc>
    radix       HEX

; =============== SWITCH CONTROLLING ===============
;   - Version: 1.0, 2016-01-02
;   - Author: Andreas Wälchli
;
; This module manages switch control signals to avoid
; powering switch relays that do not change state.

SWITCH_CONTROL_RAM  udata
switch_cache    res 1
switch_temp     res 1

    #define cache   switch_cache
    #define temp    switch_temp

    global  switch_control.process
    global  switch_control.init

SWITCH_CONTROL_VEC  code
switch_control.init:
    banksel cache
    clrf    cache
    return
switch_control.process:
    banksel cache
    pagesel $
    ; reduce command
    movwf   temp
    andwf   cache, W
    xorwf   temp, F
    ; update cache
    btfsc   temp, 0
    bcf     cache, 1
    btfsc   temp, 1
    bcf     cache, 0
    btfsc   temp, 2
    bcf     cache, 3
    btfsc   temp, 3
    bcf     cache, 2
    btfsc   temp, 4
    bcf     cache, 5
    btfsc   temp, 5
    bcf     cache, 4
    movfw   temp
    iorwf   cache, F
    ; return reduced command
    return

    end