package analysisTools;

import java.util.*;

public class Node
{
	private String nodeName;
	private double nodeRank;
	
	public Node(String name, double rank)
	{
		nodeName = name;
		nodeRank = rank;
	}
	
	public int compare(Node n)
	{
		int c = 0;
		if(this.nodeRank > n.getRank())
		{
			c = 1;
		}
		
		return c;
	}
	
	public String toString()
	{
		return nodeName + " : " + nodeRank;
	}
	
	public double getRank()
	{
		return this.nodeRank;
	}
	
	public String getName()
	{
		return this.nodeName;
	}
}