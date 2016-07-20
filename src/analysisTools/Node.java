package analysisTools;

import java.util.*;

public class Node
{
	// Node object fields
	private String nodeName;
	private double nodeRank;
	
	//Constructor
	public Node(String name, double rank)
	{
		nodeName = name;
		nodeRank = rank;
	}
	
	//Compare method used to sort nodes
	public int compare(Node n)
	{
		int c = 0;
		if(this.nodeRank > n.getRank())
		{
			c = 1;
		}
		
		return c;
	}
	
	//Getter Methods
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