; #############################################
; # SHARED RAM ALLOCATION FOR PIC16F527	      #
; #############################################
; # VERSION				      #
; #   1.0.0 (2015-12-13)		      #
; #############################################
; # TARGET HARDWARE			      #
; #   - PIC16F527			      #
; #############################################
; # EXPORTED LABELS			      #
; #   _global_x				      #
; #     the pointers to the 4 global memory   #
; #	locations. (x=0..3)		      #
; #   _globals				      #
; #	pointer to the first global memory    #
; #	location. (identical to _global_0)    #
; #############################################
; # DESCRIPTION				      #
; #   contains the memory allocation code for #
; #   the 4 shared bytes		      #
; #############################################
; # CHANGELOG				      #
; #   1.0.0 - initial release		      #
; #############################################
GLOBALS UDATA_SHR
_global_0   res	1
_global_1   res 1
_global_2   res 1
_global_3   res	1

    #define  _globals	_global_0

    global  _global_0
    global  _global_1
    global  _global_2
    global  _global_3
    global  _globals

    END