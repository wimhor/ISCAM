#
# Makefile: Makefile for the ISCAM program.
#
# Wim Hordijk   Last modified: 1 October 2026
#


#
# Main target.
#
iscam: Cell.java Dynamic.java CAColor.java CA.java Help.java ISCAM.java
	javac Cell.java Dynamic.java CAColor.java CA.java Help.java ISCAM.java


#
# jar file. Run with 'java -jar ISCAM'.
#

jar: iscam
	cd ..; jar cfe ISCAM.jar ISCAM.ISCAM ISCAM/*.class ISCAM/Help.html


#
# Clean up the mess.
#
clean:
	rm -f *~ *.class


#
# EoF: Makefile
#
