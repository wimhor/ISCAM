# ISCAM
Code for the ISCAM program to simulate spontaneous spirals in spatial systems.

This code was used to generate the results in the following publications:
- W. Hordijk, W. Lin, P. Serocka and A. W. J. Dress. [The ideal storage cellular automaton](https://drops.dagstuhl.de/opus/volltexte/2010/2728/pdf/10231.DressAndreas.Paper.2728.pdf).  In A. Apostolico, A. W. J. Dress and L. Parida (eds.), _Structure discovery in biology: Motifs, networks & phylogenies_, pp. 1-8, 2010.
- W. Hordijk [Spontaneous spirals in spatial systems](). _In preparation_.

This Java code comes without any warranty, but feel free to use it for your own purposes. If you do so, a reference to the [current repo](https://github.com/wimhor/ISCAM) will be appreciated.

## Compile
To compile, go into the `ISCAM` directory and type

    make

to compile the `ISCAM` program or

    make jar

to create a `.jar` file which will be placed in the parent directory.

## Run
To run, go back up to the parent directory and type

    java -jar ISCAM.jar

Requires a Java JRE to be installed (by default on most systems).

## Known issues
  - If you change any parameter settings by directly typing into an entry box, make sure to press `enter` afterwards to register the change (a Java "feature").
