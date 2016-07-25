package analysisTools;

import java.util.*;
import java.io.*;

public class Analysis
{
	DirectedGraph graph;
	HashMap<String, String> fileNames;

	public String traceGdfLocation;
	public String destinationFolderForAnalysedCommits;
	public String commitLogLocation;
	public int noOfCommitsToAnalyse;

	
	public static void main(String[] args)
	{
		try
		{
			Analysis analysis = new Analysis();
			analysis.getPropValues();

			System.out.println("Creating Graph from trace file.");
			analysis.createGraph(analysis.traceGdfLocation);
			System.out.println("Graph Created.");

			System.out.println("Computing Page Ranks.");
			analysis.pageRanks();
			System.out.println("Page Ranks Computed.");

			System.out.println("Analysing Commits.");
			analysis.analyseCommits(analysis.destinationFolderForAnalysedCommits,analysis.commitLogLocation,analysis.noOfCommitsToAnalyse);
			System.out.println("Commits Analysed.");

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
		this.fileNames = new HashMap<String, String>();
	}
	
	public void analyseCommits(String destinationFolder, String commitLog, int noOfCommitsToAnalyse)
	{
		try
		{
			BufferedReader br = new BufferedReader(new FileReader(new File(commitLog)));
			int i = 0;
			String curLine = "";

			while(i<noOfCommitsToAnalyse)
			{
				curLine = br.readLine();

				if(curLine.contains("Commit:"))
				{
					String[] temp = curLine.split(":");
					BufferedWriter bw = new BufferedWriter(new FileWriter(new File(destinationFolder+"/"+temp[1])));
					
					bw.write(curLine);
					bw.newLine();
					curLine = br.readLine();
					bw.write(curLine);
					bw.newLine();

					while((curLine=br.readLine()).isEmpty() == false)
					{
						String nodeName = findNodeName(curLine);
						if(nodeName.equals("na"))
						{
							bw.write("The target "+curLine+" could not be found.");
							bw.newLine();
						}
						else
						{
							bw.write("The target "+curLine+" has the following dependencies: ");
							bw.newLine();
							LinkedList<Node> dependencies = graph.findDep(nodeName);
							Iterator iter = dependencies.iterator();
							
							LinkedList<Node> repeats = new LinkedList<Node>();

							while(iter.hasNext())
							{
								Object element = iter.next();
								if(((Node)element).getRank() != -1.0 && !(repeats.contains((Node)element)))
								{
									bw.write(((Node)element).toString());
									bw.newLine();
									repeats.add((Node)element);
								}
							}
						}
						bw.newLine();
					}
					bw.close();
					i++;
				}
			}

		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	public String findNodeName(String localName)
	{
		Iterator<String> iter = fileNames.keySet().iterator();
		while(iter.hasNext())
		{
			String temp = iter.next();
			if(temp.contains(localName))
			{
				return fileNames.get(temp);
			}
		}

		return "na";
	}

	//Traverses the trace.gdf file and creates a graph using hashmaps.
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
				graph.setVisited(temp[0]);
				this.fileNames.put(temp[1],temp[0]);
				
				if(temp[8].equals("1"))
				{
					graph.setPageRank(temp[0], (double)(-1));
				}
				else if(temp[8].equals("0"))
				{
					graph.setPageRank(temp[0], (double)(1));
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
				graph.addInDegree(temp[0], temp[1]);
			}
			
			br.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	//Calculates pageranks.
	public void pageRanks()
	{		
		LinkedList<String> vertices = graph.getVertices();
		
		double d = 0.85;
		
		for(int j=0; j<100; j++)
		{
			Iterator<String> k = vertices.iterator();
			
			while(k.hasNext())
			{
				String curNode = k.next();
				double pagerank = 1-d;
				
				if(graph.getPageRank(curNode) != -1.0)
				{
					//LinkedList<String> inDegrees = graph.getInDegrees(curNode);
					LinkedList<String> inDegrees = graph.getNeighbours(curNode);

					if(inDegrees != null)
					{				
						Iterator<String> iter = inDegrees.iterator();
						
						while(iter.hasNext())
						{
							String neighIn = iter.next();
							if(graph.getPageRank(neighIn) != -1.0)
							{
								//Two Different Formulas
								//First one is the one used by google to rank webpages according to their authority.
								//Second one is a slight modification of the first since the direction of arrows have different meanings
								//in file dependency graphs than they do in graph for the www.
								//pagerank += d*((graph.getPageRank(neighIn))/(graph.getOutDegree(neighIn)));
								pagerank += d*((graph.getPageRank(neighIn))/(graph.getInDegree(neighIn)));
							}
						}
					}	
					graph.setPageRank(curNode, pagerank);
				}
			}
		}
		
	}
	
	public void getPropValues() throws IOException
	{
		Properties prop = new Properties();
		String propFileName = "config.properties";
		InputStream inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);

		if(inputStream != null)
		{
			prop.load(inputStream);
		}
		else
		{
			throw new FileNotFoundException("property file '"+propFileName+"' npt found in the classpath.");
		}

		inputStream.close();

		this.traceGdfLocation = prop.getProperty("traceGdfLocation");
		this.destinationFolderForAnalysedCommits = prop.getProperty("destinationFolderForAnalysedCommits");
		this.commitLogLocation = prop.getProperty("commitLogLocation");
		this.noOfCommitsToAnalyse = Integer.parseInt(prop.getProperty("noOfCommitsToAnalyse"));

	}
}