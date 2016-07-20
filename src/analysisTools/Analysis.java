package analysisTools;

import java.util.*;
import java.io.*;

public class Analysis
{
	DirectedGraph graph;
	
	public static void main(String[] args)
	{
		Analysis analysis = new Analysis();
		Parser parser = new Parser("/home/owais/Desktop/Results/gitlogvtk.txt");
		analysis.createGraph("/home/owais/vtk/trace.gdf");
		System.out.println("Graph Created.");
		analysis.parsePageRanks("/home/owais/Desktop/Results/vtkAnalysisAfterRemovingPhony(whole).txt");
		System.out.println("Page Ranks Loaded.");

		try
		{
			parser.parseLog("/home/owais/Desktop/parsedCommits.txt", 25);
			System.out.println("Commits parsed from git log.");

			System.out.println("Please Wait, The analysis is running, it might take some time to complete.");
			analysis.analyseParsedLog("/home/owais/Desktop/Results/parsedCommits.txt",3);
			System.out.println("Analysis Complete.");

			System.out.print("Starting sort.");
			analysis.sortAnalysedLog("/home/owais/Desktop/Results/dependencies.txt");
			System.out.print("Sort Completed.");
			
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		

	}
	
	//Constructor
	public Analysis()
	{
		this.graph = new DirectedGraph();
	}
	
	//This method analyses the parsed commits and prints out the files affected by each token
	//to a text file called dependencies.txt
	public void analyseParsedLog(String log, int noOfCommitsToAnalyse) throws IOException
	{
		BufferedReader br = new BufferedReader(new FileReader(new File(log)));
		BufferedWriter bw = new BufferedWriter(new FileWriter(new File("/home/owais/Desktop/Results/dependencies.txt")));
		String curLine = br.readLine();
		
		for(int i=0; curLine != null && i<noOfCommitsToAnalyse+1; curLine = br.readLine(), i++)
		{
			String[] lineSplit = curLine.split(",");

			//The first two elements are the commit id and date respectively so if there are only 2 elements in a line it means 
      		//no files were added or modified in that commit.
			if(lineSplit.length<=2)
			{
        		continue;
      		}

      		//Shorten the array to include only the names of the files that were changed in that commit.
			String[] cleanLineSplit = reduceArray(lineSplit);

			//For testing purposes. (Delete after testing)
			System.out.println(Arrays.toString(cleanLineSplit));

			for (int j=0; j<cleanLineSplit.length; j++)
			{
				String realName = findNodeName(cleanLineSplit[j]);

				if(realName != null)
				{
					bw.write("The "+lineSplit[0]+" made on the "+lineSplit[1]+" added/modified \n the "
							+ "file named "+cleanLineSplit[j]+". The files and their corresponding \n pageranks"
									+ " affected by adding/modifying "+cleanLineSplit[j]+" are as follows: ");
					bw.newLine();

					LinkedList<String> dependencies = new LinkedList<String>();
					graph.findDependencies(realName, dependencies);

					try
					{
						Iterator<String> k = dependencies.iterator();

						for (String dep = k.next(); k.hasNext(); dep=k.next()) 
						{
							bw.write(dep);
							bw.newLine();
						}

						System.out.println("Iteration Completed.");

						//Leave 3 blank lines to separate the dependencies of two tokens in the
						//text file produced so that it is easily read.
						bw.newLine();
						bw.newLine();
						bw.newLine();
					}
					catch(Exception e)
					{
						e.printStackTrace();
					}
				}
				else
				{
					bw.write("The file "+cleanLineSplit[j]+" was not found to be a node"
							+ " in the build dependency graph of the software system under consideration.");
					bw.newLine();
					bw.newLine();
					bw.newLine();
				}
			}
		}

		
		bw.close();
		br.close();
	}
	
	//This methods cleans up the file names array to remove any whitespace and extra characters.	
	public String[] reduceArray(String[] a)
	{
		int length = a.length-2;
		String[] shortArr = new String[length];
		for(int i=0;i<length;i++)
		{
			shortArr[i]=a[i+2];
		}
		
		for(int i=0;i<length;i++)
		{
			if(shortArr[i].charAt(0)=='A' || shortArr[i].charAt(0)=='M')
			{
				shortArr[i] = shortArr[i].substring(2, shortArr[i].length());
				shortArr[i] = shortArr[i].replaceAll("\\s", "");
			}
		}
		return shortArr;
	}
	
	//NOTE: When parsing the file names in git log, the names are listed in the local name
	//format whereas the names used to create the graph were in the node name foramat as created
	//by the makao script "generate_makao_graph.pl" so inorder to be able to trace dependencies both 
	//need to be in the same format. This method takes the local name and returns the node name.
	public String findNodeName(String traceGdfLocation,String localFileName) throws IOException
	{
		BufferedReader br = new BufferedReader(new FileReader(new File(traceGdfLocation)));
		String newLine = "";
		
		while((newLine = br.readLine()).contains("edgedef>")==false)
		{
			String[] temp = newLine.split(",");
			if(a.equals(temp[1]))
			{
				br.close();
				return temp[0];
			}
		}
		
		br.close();
		return null;
	}

	//This method loads in the pageranks calcualted previosuly.
	public void parsePageRanks(String ranksFileLocation)
	{
		try
		{
			BufferedReader br = new BufferedReader(new FileReader(new File(ranksFileLocation)));
			String curLine = "";
			
			while((readLine = br.readLine()) != null)
			{
				temp = readLine.split(" ");
				graph.setPageRank(temp[0], Double.parseDouble(temp[2]));
			}
			System.out.println("Page Ranks Parsed.");
			br.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	//This method parses the trace.gdf file created using makao and creates a graph using
	//hashmaps.
	public void createGraph(String traceFile)
	{
		String readLine = "";
		String[] temp = new String[15];
		try
		{
			BufferedReader br = new BufferedReader(new FileReader(new File(traceFile)));
			
			//Throw away nodedef line.
			while(readLine.contains("nodedef>")==false)
			{
				readLine = br.readLine();
			}
			
			//Process Nodes
			for (String curLine = br.readLine(); !curLine.startWith("edgedef>"); curLine = br.readLine())
			{
				String[] lineSplit = curLine.split(",");
				graph.addVertex(lineSplit[0]);
				
				if(lineSplit[8].equals("1"))
				{
					graph.setPageRank(lineSplit[0], -1.0);
				}
			}

			//Process Edges
			for (String curLine = br.readLine(); curLine != null ; curLine = br.readLine()) 
			{
				String[] lineSplit = curLine.split(",");
				graph.addEdge(lineSplit[0], lineSplit[1]);
			}
			
			
			System.out.println("Graph Created.");
			br.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	public void sortAnalysedLog(String analysedLogDestination)throws IOException
	{
		BufferedReader br = new BufferedReader(new FileReader(new File(analysedLogDestination)));
		BufferedWriter bw = new BufferedWriter(new FileWriter(new File("/home/owais/Desktop/Results/sortedDependencies.txt")));
		String newLine = "";
		String[] temp;
		LinkedList<Node> sortedList = new LinkedList<Node>();

		
		while(true)
		{
			newLine = br.readLine();
			
			if(newLine == null)
			{
				break;
			}
			else if(newLine.isEmpty())
			{
				sort(sortedList);
				System.out.print("List Done.");
				Node temp2;
				Iterator<Node> i = sortedList.iterator();
				while(i.hasNext())
				{
					temp2=i.next();
					bw.write(temp2.toString());
					bw.newLine();
				}
				bw.write("\n \n \n");
				sortedList.clear();
			}
			else if((temp=newLine.split(":")).length==1)
			{
				
				bw.write(newLine);
				bw.newLine();
			}
			else
			{
				Node temp4 = new Node(temp[0],Double.parseDouble(temp[1]));
				
				if(Double.parseDouble(temp[1])!=-1.0 && !sortedList.contains(temp4))
				{
					sortedList.add(temp4);
				}
			}
		}
		br.close();
		bw.close();
		
	}
	
	public void sort(LinkedList<Node> ll)
	{
		for(int i=0;i<ll.size()-1;i++)
		{
			for(int j=0;j<ll.size()-1-i;j++)
			{
				if(ll.get(j).compare(ll.get(j+1))==1)
				{
					Node temp = ll.get(j+1);
					ll.set(j+1, ll.get(j));
					ll.set(j, temp);
				}
			}
		}
	}
}
