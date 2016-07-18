package analysisTools;

import java.util.*;
import java.io.*;

public class Analysis
{
	DirectedGraph graph;
	
	public static void main(String[] args)
	{
		Analysis analysis = new Analysis();
//		Parser parser = new Parser("/home/owais/Desktop/Results/gitlogvtk.txt");
//		analysis.createGraph("/home/owais/vtk/trace.gdf");
//		analysis.parsePageRanks("/home/owais/Desktop/Results/vtkAnalysisAfterRemovingPhony(whole).txt");
		
		try
		{
//			parser.parseLog("/home/owais/Desktop/Results/parsedCommits.txt", 25);
//			analysis.analyseParsedLog("/home/owais/Desktop/Results/parsedCommits.txt",3);
			System.out.print("Program started.");
			analysis.sortAnalysedLog("/home/owais/Desktop/Results/dependencies.txt");
			System.out.print("Program ended.");
			
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		

	}
	
	public Analysis()
	{
		this.graph = new DirectedGraph();
	}
	
	public void analyseParsedLog(String parsedLogFile, int noOfCommitsToAnalyse) throws IOException
	{
		BufferedReader br = new BufferedReader(new FileReader(new File(parsedLogFile)));
    	String curLine = br.readLine();

		for(int i = 0; curLine !=null && i<noOfCommitsToAnalyse+1; curLine = br.readLine(), i++)
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
			
			//This loop goes over each file changed during a commit and prints out all the files affected by changing it.	
			for(int j=0;j<cleanLineSplit.length;j++)
			{
				String realName = findNodeName(cleanLineSplit[j]);
				if(realName != null)
				{
					System.out.println("The "+lineSplit[0]+" made on the "+lineSplit[1]+" added/modified \n the "
							+ "file named "+cleanLineSplit[j]+". The files and their corresponding \n pageranks"
									+ " affected by adding/modifying "+cleanLineSplit[j]+" are as follows: ");
					
					LinkedList<String> dependencies = new LinkedList<String>();
					graph.findDependencies(realName, dependencies);
					
					try
					{
            // TODO: Hardcoded filename...
						BufferedWriter bw = new BufferedWriter(new FileWriter(new File("/home/owais/Desktop/Results/dependencies.txt")));
            // Write the token???
						bw.write(cleanLineSplit[j]);
						bw.newLine();

						Iterator<String> k = dependencies.iterator();
						for(String dep = k.next(); k.hasNext(); dep = k.next())
						{
							bw.write(dep);
							bw.newLine();
						}
						System.out.println("Iteration Completed");
						bw.write("\n \n \n"); // Why three blank lines???
						bw.close();
					}
					catch(Exception e)
					{
						e.printStackTrace();
					}
				}		
				else
				{
					System.out.println("The file "+cleanLineSplit[j]+" was not found to be a node"
							+ " in the build dependency graph of the software system under consideration.");
				}
			}

      // I don't understand why these three blank lines are printed
		System.out.println();
		System.out.println();
		System.out.println();
		}
		bw.close();
		br.close();
	}
	
	public String[] reduceArray(String[] a)
	{
		int length = a.length-2;
		String[] shortArr = new String[length];
    
    // throw away first two elements
		for(int i=0;i<length;i++)
		{
			shortArr[i]=a[i+2];

      // This is done to get the file names in the same format as they are in the file with the pageranks 
      // so that they can be compared and their scores can be retrieved.
      if (shortArr[i].startsWith("A") || shortArr[i].startsWith("M")) {
				shortArr[i] = shortArr[i].substring(2, shortArr[i].length());
				shortArr[i] = shortArr[i].replaceAll("\\s", "");
			}
		}
		return shortArr;
	}
	
	public String findNodeName(String a) throws IOException
	{
    // What is this hardcoded file name?
    // Also, this is really inefficient because it reads this whole file again and again for every token!
		BufferedReader br = new BufferedReader(new FileReader(new File("/home/owais/vtk/trace.gdf")));
		String newLine = "";
    String rtn = null;
		
		while(!(newLine = br.readLine()).startsWith("edgedef>"))
		{
			String[] lineSplit = newLine.split(",");
			if(a.equals(lineSplit[1]))
			{
				rtn = lineSplit[0];
        break;
			}
		}
		
		br.close();
		return rtn;
	}

	public void parsePageRanks(String ranksFile)
	{
		try
		{
			BufferedReader br = new BufferedReader(new FileReader(new File(ranksFile)));
			String curLine = br.readLine(); // Throw away first line????
			while((curLine = br.readLine()) != null)
			{
				String[] lineSplit = curLine.split(" ");
				graph.setPageRank(lineSplit[0], Double.parseDouble(lineSplit[2]));
			}
			System.out.println("Page Ranks Parsed.");
			br.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void createGraph(String traceFile)
	{
		try
		{
			BufferedReader br = new BufferedReader(new FileReader(new File(traceFile)));
			
      // Consume the nodedef line
			while(!br.readLine().startsWith("nodedef>"));
			
      // Process nodes
      for(String curLine = br.readLine(); !curLine.startsWith("edgedef>"); curLine = br.readLine())
			{
				String[] lineSplit = curLine.split(",");
				graph.addVertex(lineSplit[0]);
				
				if(lineSplit[8].equals("1"))
				{
					graph.setPageRank(lineSplit[0], -1.0);
				}
			}
			
      // Process edges
			for(String curLine = br.readLine(); curLine != null; curLine = br.readLine())
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
				//bw.write(Arrays.toString(temp));
				//bw.newLine();
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
