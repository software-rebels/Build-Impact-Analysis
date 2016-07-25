# Build-Impact-Analysis
This repository contains the code that analyses the build dependency graph and outputs the files effected by any given commit.

HOW TO USE:

1. Create the trace.txt file using MAKAO of the software system under consideration. (For more information refer to http://mcis.polymtl.ca/makao.html)

2. Using the generate_makao_graph.pl script create the trace.gdf file.

3. In bash use the command: 

git log --oneline --name-only --diff-filter=AM --pretty=format:"Commit:%h%nDate:%cd" <revision_range> > enter_file_name_here
   
to create a text file with the git log in it.

4. Then in the config.properties file input the following arguments:
	i.   The location of trace.gdf.
	ii.  The location of the folder to store the results.
	iii. The location of the commit log obtained in step 3.
	iv.  The no. of commits to analyse.

OUTPUT:
	The program outputs a text file named after the commit ID for each commit analysed. In the text file the commit ID and date are written at the start for identification. This is followed by the token name that was added/modified by the change followed by a sorted list of all the files affected.

