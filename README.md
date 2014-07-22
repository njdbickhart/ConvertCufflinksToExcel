####ConvertCufflinksToExcel
=======================

This program converts several cufflinks output files into easily readable excel tables using Apache's POI library. Currently, only the "cuffdiff" output format is supported, but this may change in future versions.

###Installation
=======================

ConvertCufflinksToExcel is designed to run on low-moderate memory (> 8gb RAM) Linux servers, though it may require more memory with larger datasets.

The only prerequisite for running this program is to download the Java development kit (JDK), version 8:
[Download Java from Oracle.com](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)

In order to access the self-packaged executable (ConvertCufflinksToExcel.jar), download this repository and look within the "store" directory. 

Once you have installed the JDK, you can run the program using the following command:
```
/path/to/jdk8/bin/java -jar /path/to/ConvertCufflinksToExcel/store/ConvertCufflinksToExcel.jar
```

You should see a usage statement if you are using the proper JDK version.

###Usage
=========================

In order to view the usage statement, run the command without any arguments:
```
/path/to/jdk8/bin/java -jar /path/to/ConvertCufflinksToExcel/store/ConvertCufflinksToExcel.jar
```

Currently, only one mode is supported: "diff". In order to access this usage statement for this mode, type the following command:
```
/path/to/jdk8/bin/java -jar /path/to/ConvertCufflinksToExcel/store/ConvertCufflinksToExcel.jar diff
```

"Diff" mode converts a cufflinks-generated ".diff" file into an excel spreadsheet where all pairwise comparisons are listed in rows. Additionally, any significant results will be highlighted.
If you are looking to quickly convert a file with no special options, then here is the quick start usage of the command:
```
/path/to/jdk8/bin/java -jar /path/to/ConvertCufflinksToExcel/store/ConvertCufflinksToExcel.jar diff -i my_diff_file.diff -o my_excel_output.xlsx
```

Here is a more detailed outline of command line input options:

* -i (required) The input diff file that will be converted
* -o (required) The output excel file. Please note that you MUST include the ".xlsx" suffix to the file name!
* -n (optional; takes no arguments) Skip a gene location in the diff file if it has no FPKM value across all conditions
* -k (optional) An input "keys" file that will change the condition names. Useful if your samples are still labeled as "q1," "q2," etc...
* -g (optional) An input gene bed file that can be used to annotate locations. This is useful if you are dealing with custom transcript assemblies that are not annotated by your cufflinks gtf file

#Keys file
This tab-delimited file follows a simple format: column 1 is the name of the sample/condition you wish to change, and column 2 is the name that you would prefer the sample to be called.
An example:
```
q1  mammarystemcells
q2  hematopoieticstemcells
```

#Gene bed file
This tab-delimited file follows the bed file conventions found on the [UCSC genome browser](http://genome.ucsc.edu/FAQ/FAQformat.html#format1) (only the first 4 fields are required).
If this option is given to the program, it will annotate unannotated gene locations in your excel file (ie. regions that do not have a gene name) with the regions indicated in the bed file.
An example bed file:
```
chr1  1000  2000  Gene1
chr2  3000  10000 Gene2
chr2  10005 10300 Gene3
```

Here is an example command that uses all options:
```
/path/to/jdk8/bin/java -jar /path/to/ConvertCufflinksToExcel/store/ConvertCufflinksToExcel.jar diff -i my_diff_file.diff -o my_excel_output.xlsx -n -k my_keys_file.txt -g my_gene_annotations.bed
```
