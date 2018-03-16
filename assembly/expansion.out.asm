; #############################################
; # WRITE FUNCTION FOR EXPANSION HEADER REV A #
; #############################################
; # TARGET HARDWARE			                  #
; #   - PIC16F527			                  #
; #   - MasterBoard Rev. A		              #
; #   - ExpansionHeader Rev. A		          #
; #############################################
; # EXPORTED LABELS			                  #
; #   expansion.write (main function)	      #
; #     writes a 16-bit value to the output   #
; #	    section of the expansion header	      #
; #############################################
; # DESCRIPTION 			                  #
; #   This function handles communication     #
; #   with the output section of the	      #
; #   expansion header (RB4-RB6).	          #
; #############################################
; # I/O CONSIDERATIONS 			              #
; #   This function assumes RB4, RB5 and RB6  #
; #   to be configured as outputs.	          #
; #   RB7 is not affected aside from the      #
; #   usual bsf/bcf side-effects.	          #
; #############################################
; # MEMORY CONSIDERATIONS		              #
; #   This function requires 4 bytes of RAM.  #
; #   bank-shared memory will not be modified #
; #   bank- and page-handling is included.    #
; #############################################
; # FUNCTION CALL / RETURN CONTRACT	          #
; #   This function assumes FSR to contain    #
; #   the adress for the first byte of data.  #
; #   The value pointed to by FSR will be     #
; #   written to L0-L7, and the subsequent    #
; #   value to L8-15.			              #
; #############################################
; # CHANGELOG				                  #
; #   1.0.0 - initial release		          #
; #############################################
    #include <p16f527.inc>
    global  expansion.out

    #define dat	    RB4
    #define clock   RB5
    #define strobe  RB6

EXPANSION_WRITE_DATA		UDATA
expansion.write.index		res 1
expansion.write.value_low	res 1
expansion.write.value_high	res 1
expansion.write.source_pointer	res 1

    #define index	    expansion.write.index
    #define value_low	    expansion.write.value_low
    #define value_high	    expansion.write.value_high
    #define source_pointer  expansion.write.source_pointer

EXPANSION_WRITE_VECTOR CODE
expansion.out:
    ; read values into own memory
    banksel value_low
    pagesel expansion.out
    movfw   INDF
    movwf   value_low
    incf    FSR, F
    movfw   INDF
    movwf   value_high
    decf    FSR, F
    movfw   FSR
    movwf   source_pointer
    ; load FSR with target port
    movlw   PORTB
    movwf   FSR
    movlw   0x10
    movwf   index
writeBit:
    bcf	    INDF, dat
    rlf	    value_low, F
    rlf	    value_high, F
    btfsc   STATUS, C
    bsf	    INDF, dat
    bsf	    INDF, clock
    bcf	    INDF, clock
    decfsz  index, F
    goto    writeBit
    bcf	    INDF, dat
    bsf	    INDF, strobe
    bcf	    INDF, strobe
    ; cleanup
    movfw   source_pointer
    movwf   FSR
    bcf    STATUS, C
    return


    END