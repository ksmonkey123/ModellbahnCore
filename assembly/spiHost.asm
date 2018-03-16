    #include    <p16f527.inc>
    __config    0x3b4
    radix       HEX
    
;<editor-fold defaultstate="collapsed" desc="base vectors">
RESET_VECTOR    code    0x3ff
    goto    0x000
    
START_VECTOR    code    0x000
    lgoto   start

IRUPT_VECTOR    code    0x004
    retfie
;</editor-fold>
;<editor-fold defaultstate="collapsed" desc="library imports">
    extern  expansion.in
    extern  expansion.out
    extern  serial.out
    extern  deactivate_specials
    extern  _global_0
    extern  _global_1
;</editor-fold>
;<editor-fold defaultstate="collapsed" desc="ram allocation">
PROGRAM_MEMORY  udata
network res 1
output  res 2
spi     res 4
cache   res 1
;</editor-fold>
;<editor-fold defaultstate="collapsed" desc="SPI pin mapping">
#define SPI_SS   RA5
#define SPI_CLK  RA4
#define SPI_MOSI RA2
#define SPI_MISO RA1
;</editor-fold>

PROGRAM_VECTOR  code
start:  
    call    deactivate_specials
    banksel output
    movlw   0x80
    movwf   PORTB
    movlw   0x0f
    tris    PORTB
    clrf    PORTC
    movlw   0xf1
    tris    PORTC
    clrf    cache
    clrf    output + 0
    clrf    output + 1
    clrf    network
    movlw   b'01101001'
    movwf   spi + 0
    movlw   b'00000000'
    movwf   PORTA
    movlw   b'11111101'
    tris    PORTA
    movlw   output
    movwf   FSR
    call    expansion.out
    ; wait for SS init
    btfsc   PORTA, SPI_SS ; SS must be idle to start
    goto    $-1

main:
    movlw   network
    movwf   FSR
    call    serial.out
    btfss   PORTA, SPI_SS ; detect SS (low)
    goto    spi_prep
    movlw   spi + 1
    movwf   FSR
    call    expansion.in ; read button states directly into SPI
    btfsc   PORTA, SPI_SS ; detect SS (low)
    goto    main
spi_prep:
    movlw   0x03
    movwf   _global_1
    movlw   spi + 0 ; load first byte
    movwf   FSR
_spi_a:
    movlw   0x08
    movwf   _global_0
_spi_a_inner:
 ;   btfss   PORTA, SPI_CLK
 ;   goto    $-1
    bcf     PORTA, SPI_MISO ; write MISO (inverted!)
    btfss   INDF, 7
    bsf     PORTA, SPI_MISO
    bcf     STATUS, C
    btfss   PORTA, SPI_CLK
    goto    $-1
    btfsc   PORTA, SPI_CLK  ; wait for clock high (inverted)
    goto    $-1
    btfss   PORTA, SPI_MOSI ; read MOSI (inverted)
    bsf     STATUS, C
    rlf     INDF, f
  ;  btfss   PORTA, SPI_CLK  ; wait for clock low (inverted)
  ;  goto    $-1
    decfsz  _global_0, f    ; check if still inside current byte
    goto    _spi_a_inner
    incf    FSR, f          ; move to next byte
    decfsz  _global_1, f    ; check if still within first 3 bytes
    goto    _spi_a
    ; first 3 bytes sent. prepare last byte (readback of input)
    movf    spi + 1, w
    xorwf   spi + 2, w
    movwf   spi + 3
    movlw   0x08
    movwf   _global_0
_spi_b_inner:
 ;   btfss   PORTA, SPI_CLK
 ;   goto    $-1
    bcf     PORTA, SPI_MISO ; write MISO (inverted!)
    btfss   spi + 3, 7
    bsf     PORTA, SPI_MISO
    bcf     STATUS, C
    btfss   PORTA, SPI_CLK
    goto    $-1
    btfsc   PORTA, SPI_CLK  ; wait for clock high
    goto    $-1
    btfss   PORTA, SPI_MOSI ; read MOSI
    bsf     STATUS, C
    rlf     spi + 3, f
 ;   btfss   PORTA, SPI_CLK  ; wait for clock low
 ;   goto    $-1
    decfsz  _global_0, f    ; check if still inside current byte
    goto    _spi_b_inner
    ; SPI transmission done, cleanup
    bcf     PORTA, SPI_MISO ; reset MISO pin (inverted)
    ; assert data validity
    movlw   b'01101001'
    xorwf   spi + 0, w
    btfss   STATUS, Z
    goto    spi_clean           ; magic number was wrong > skip data processing
    ; validate last data byte (short led errors are irrelevant)
    movf    spi + 3, w
    xorwf   cache, w
    btfss   STATUS, Z
    goto    spi_shift
    ; data ok, write
    goto    commit
    ; store last SPI data
spi_shift:
    movf    spi + 3, w
    movwf   cache
spi_clean:
    ; prepare SPI files for next packet
    movlw   b'01101001' ; magic number 0x69
    movwf   spi + 0
    btfss   PORTA, SPI_SS   ; wait for SPI to be released by the master
    goto    $-1
    goto    main
    
commit:
    ; commit SPI data to output busses
    ; serial output
    movf    spi + 3, w
    movwf   network
    ; check if parallel output has update
    ; (update is very expensive, avoid if possible)
    movf    spi + 1, w
    xorwf   output + 0, w
    btfss   STATUS, Z
    ; update
    goto    commit_update
    movf    spi + 2, w
    xorwf   output + 1, w
    btfsc   STATUS, Z
    ; don't update
    goto    spi_shift
commit_update:
    ; copy data
    movf    spi + 1, w
    movwf   output + 0
    movf    spi + 2, w
    movwf   output + 1
    ; write to bus
    movlw   output
    movwf   FSR
    call    expansion.out
    ; return to spi routine
    goto    spi_shift
    
    END