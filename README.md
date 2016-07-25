# Build-Impact-Analysis
This repository contains the code that analyses the build dependency graph and outputs the files effected by any given commit.

Things Completed:
1. Finished fixing the refactored code.
2. Added comments to clarify.
3. Data has been added in the data folder:
	dependencies.txt: This is the result of analysing the first few commits.
	sortedDependencies.txt: The same file as above but sorted.
	sorteduniq.txt: The same file as above but with repitions removed.
	gitlogvtk.txt: Git log redirected to a text file to be used in the program.
	parsedCommits.txt: The result of parsing through the git log to extract commitID, Date, and the files added/modified.

	vtkAnalysisAfterRemovingPhony(short).txt 	-\
	vtkAnalysisAfterRemovingPhony(whole).txt 	 |
	vtkAnalysisBeforeRemovingPhony(short).txt 	 | Text Files containing pageranks.
	vtkAnalysisBeforeRemovingPhony(whole).txt 	 |
	vtkAnalysisWithLocalNamesSorted.txt 		 |
	vtkAnalysisWithLocalNames.txt  				-/


Things Left to Complete:
1. Input arguments using configuration file.
2. Make the findNodeName() method more efficient.

HOW TO USE:
Since the configuration file has not been figured out yet the arguments have to be hardcoded.

1. Create the trace.txt file using MAKAO of the software system under consideration.
2. Using the generate_makao_graph.pl script create the trace.gdf file.
3. In bash use the command 
		git log --name-status > enter_file_name_here
   to create a text file with the git log in it.

4. Then in the analysis.java file in the main method input the following arguments:
	i.   On line 13 input the location of the git log file created in step 3.
	ii.	 On line 15 input the location of the trace.gdf file obtained in step 2.
	iii. On line 18 input the location of the file vtkAnalysisAfterRemovingPhony(whole).txt.
	iv.  On line 23 input the destination of where to store the parsedCommits.txt file and
		 the number of commits to parse.
	v.   On line 27 input the destination to store the results, then input the location of
		 the file parsedCommits.txt (same as step iv), and finally the number of commits to analyse taking care it does not exceed the number in step iv.
	vi.  On line 31 inpu the location of the results file created in step v.

