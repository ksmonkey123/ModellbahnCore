; #############################################
; # READ FUNCTION FOR EXPANSION HEADER REV. A #
; #############################################
; # TARGET HARDWARE                           #
; #   - PIC16F527			                  #
; #   - MasterBoard Rev. A		              #
; #   - ExpansionHeader Rev. A		          #
; #############################################
; # EXPORTED LABELS                           #
; #   expansion.read (main function)	      #
; #     reads the 16-bit input on the header  #
; #	and stores them at a given location.      #
; #############################################
; # DESCRIPTION 			                  #
; #   This function handles communication     #
; #   with the input section of the expansion #
; #   header (RC1-RC5).			              #
; #############################################
; # I/O CONSIDERATIONS 			              #
; #   This function assumes RC1, RC2 and RC3  #
; #   to be configured as outputs, and RC4    #
; #   and RC5 to be configured as inputs.     #
; #   RC0, RC6 and RC7 are not affected aside #
; #   from the usual bsf/bcf side-effects.    #
; #############################################
; # MEMORY CONSIDERATIONS		              #
; #   This function requires 4 bytes of RAM.  #
; #   bank-shared memory will not be modified #
; #   bank- and page-handling are included    #
; #############################################
; # FUNCTION CALL / RETURN CONTRACT	          #
; #   This function assumes FSR to contain    #
; #   the destination adress for the first    #
; #   byte of return data. The lower byte     #
; #   (T0-T7) will be written to the address  #
; #   contained in FSR. The higher byte	      #
; #   (T8-T15) will be written into the next  #
; #   memory location. At return the FSR will #
; #   contain the same value as when calling  #
; #   the function. On return any bank might  #
; #   be selected.                            #
; #############################################
    #include <p16f527.inc>
    global  expansion.in

    #define a0	RC3
    #define a1	RC2
    #define a2	RC1
    #define d0	RC4
    #define d1	RC5

EXPANSION_READ_DATA	  UDATA
expansion.read.index	  res 1
expansion.read.value_low  res 1
expansion.read.value_high res 1
expansion.read.target	  res 1

    #define index           expansion.read.index
    #define value_low	    expansion.read.value_low
    #define value_high	    expansion.read.value_high
    #define target_pointer  expansion.read.target
    
EXPANSION_READ_VECTOR CODE
expansion.in:
    pagesel expansion.in
    banksel target_pointer
    movf    FSR, w
    movwf   target_pointer
    movlw   PORTC
    movwf   FSR
    movlw   0x08
    movwf   index
expansion.read_0:
    decf  index, f
    movlw   b'11110001'
    andwf   INDF, f
    movlw   0x00
    btfsc   index, 0
    iorlw   b'00001000'
    btfsc   index, 1
    iorlw   b'00000100'
    btfsc   index, 2
    iorlw   b'00000010'
    iorwf   INDF, f
    nop
    nop
    bcf	    STATUS, C
    btfss   INDF, d0
    bsf	    STATUS, C
    rlf	    value_low, f
    bcf	    STATUS, C
    btfss   INDF, d1
    bsf	    STATUS, C
    rlf	    value_high, f
    movf    index, f
    btfsc   STATUS, Z
    goto    expansion.read_1
    goto    expansion.read_0
expansion.read_1:
    ; cleanup & result output
    movf    target_pointer, w
    movwf   FSR
    movf    value_low, w
    movwf   INDF
    incf    FSR, f
    movf    value_high, w
    movwf   INDF
    decf    FSR, f
    bcf	    STATUS, C
    return

    END