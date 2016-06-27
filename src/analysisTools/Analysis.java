package analysisTools;

import java.util.*;
import java.io.*;

public class Analysis
{
	directedGraph graph;
	
	public static void main(String[] args)
	{
		Analysis analysis = new Analysis();
		Parser parser = new Parser("/home/owais/Desktop/Results/gitlogvtk.txt");
		analysis.createGraph("/home/owais/vtk/trace.gdf");
		analysis.parsePageRanks("/home/owais/Desktop/Results/vtkAnalysisAfterRemovingPhony(whole).txt");
		
		try
		{
			parser.parseLog("/home/owais/Desktop/Results/parsedCommits.txt", 25);
			analysis.analyseParsedLog("/home/owais/Desktop/Results/parsedCommits.txt",3);
			System.out.print("Program ended.");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public Analysis()
	{
		this.graph = new directedGraph();
	}
	
	public void findDependencies(String name, LinkedList<String> dependencies)
	{
		graph.findDependencies(name, dependencies);
	}
	
	public void analyseParsedLog(String log, int noOfCommitsToAnalyse) throws IOException
	{
		BufferedReader br = new BufferedReader(new FileReader(new File(log)));
		String readLine = "";
		String temp[];
		int i = 0;
		
		while((readLine = br.readLine())!=null && i<noOfCommitsToAnalyse+1)
		{
			temp = readLine.split(",");
			if(temp.length>2)
			{
				String[] temp2 = reduceArray(temp);
				System.out.println(Arrays.toString(temp2));
				
				for(int j=0;j<temp2.length;j++)
				{
					String realName = findNodeName(temp2[j]);
					if(realName != null)
					{
						System.out.println("The "+temp[0]+" made on the "+temp[1]+" added/modified \n the "
								+ "file named "+temp2[j]+". The files and their corresponding \n pageranks"
										+ " affected by adding/modifying "+temp2[j]+" are as follows: ");
						
						LinkedList<String> dependencies = new LinkedList<String>();
						findDependencies(realName, dependencies);
						
						try
						{
							String t = "";
							BufferedWriter bw = new BufferedWriter(new FileWriter(new File("/home/owais/Desktop/Results/dependencies.txt")));
							bw.write(temp2[j]);
							bw.newLine();
							Iterator<String> k = dependencies.iterator();
							while(k.hasNext())
							{
								t = k.next();
								bw.write(t);
								bw.newLine();
							}
							System.out.println("Iteration Completed");
							bw.write("\n \n \n");
							bw.close();
						}
						catch(Exception e)
						{
							e.printStackTrace();
						}
					}		
					else
					{
						System.out.println("The file "+temp2[j]+" was not found to be a node"
								+ " in the build dependency graph of the software system under consideration.");
					}
				}
				print3Lines();
				i++;
				
			}
		}
		br.close();
	}
	
	public void print3Lines()
	{
		System.out.println();
		System.out.println();
		System.out.println();
	}
	
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
	
	public String findNodeName(String a) throws IOException
	{
		BufferedReader br = new BufferedReader(new FileReader(new File("/home/owais/vtk/trace.gdf")));
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

	public void parsePageRanks(String ranksFile)
	{
		String[] temp;
		String readLine ="";
		
		try
		{
			BufferedReader br = new BufferedReader(new FileReader(new File(ranksFile)));
			readLine = br.readLine();
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
	
	public void createGraph(String traceFile)
	{
		String readLine = "";
		String[] temp = new String[15];
		try
		{
			BufferedReader br = new BufferedReader(new FileReader(new File(traceFile)));
			
			while(readLine.contains("nodedef>")==false)
			{
				readLine = br.readLine();
			}
			
			while(true)
			{
				readLine = br.readLine();
				if(readLine.contains("edgedef>"))
				{
					break;
				}
				temp = readLine.split(",");
				graph.addVertex(temp[0]);
				
				if(temp[8].equals("1"))
				{
					graph.setPageRank(temp[0], -1.0);
				}
			}
			
			while(true)
			{
				readLine = br.readLine();
				if(readLine == null)
				{
					break;
				}
				
				temp = readLine.split(",");
				graph.addEdge(temp[0], temp[1]);
			}
			
			System.out.println("Graph Created.");
			br.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	
	
}