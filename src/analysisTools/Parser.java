package analysisTools;

import java.util.*;
import java.io.*;

public class Parser
{
	private String log;
		
	public Parser(String log)
	{
		this.log = log;
	}
	
	public void parseLog(String destinationFile, int noOfCommitsToParse) throws IOException
	{
		BufferedReader br = new BufferedReader(new FileReader(new File(log)));
		BufferedWriter bw = new BufferedWriter(new FileWriter(new File(destinationFile)));
		String readLine = "";
		int i =0;
		
		while(i<noOfCommitsToParse+1)
		{
			readLine = br.readLine();
			String[] temp=readLine.split(" ");
			if(temp.length>0 && temp[0].equals("commit"))
			{
				if(temp[1].length()==40)
				{
					if(i!=0)
					{
						bw.newLine();
					}
					bw.write(readLine+",");
					i++;
				}
			}
			else if(temp.length>0 && temp[0].equals("Date:"))
			{
				bw.write(readLine+",");
			}
			else if(temp.length>0 && (temp[0].contains("M") || temp[0].contains("A")))
			{
				if(temp.length==1)
				{
					bw.write(temp[0]+",");
				}
			}
		}
		
		br.close();
		bw.close();
		System.out.println("Log Parsed.");
	}
}