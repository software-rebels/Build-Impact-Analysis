package analysisTools;

import java.util.*;
import java.io.*;

public class Parser
{
	private String inFile;

	private final int TYPE_IDX = 0;
	private final int TYPE_COMMIT = 1;
	private final int TYPE_DATE = 2;
	private final int TYPE_ADDEDMODIFIED = 3;

	private final int CID_IDX = 1;
	private final int CID_LENGTH = 40;

	private final String DELIM = ",";
		
	public Parser(String inFile)
	{
		this.inFile = inFile;
	}
	
	public void parseLog(String destinationFile, int noOfCommitsToParse) throws IOException
	{
		BufferedReader br = new BufferedReader(new FileReader(new File(inFile)));
		BufferedWriter bw = new BufferedWriter(new FileWriter(new File(destinationFile)));
		String readLine = br.readLine();

		for (int i=0; i<noOfCommitsToParse+1; readLine=br.readLine())
		{
			String[] lineSplit = readLine.split(" ");

			//Split the lines that cannot be split
			if(lineSplit.length<=0)
			{
				continue;
			}

			//Different cases handled using switch statement
			switch(setType(lineSplit))
			{
				//Handle the start of the commit
				case TYPE_COMMIT:
				if(lineSplit[CID_IDX].length()==CID_LENGTH)
				{
					if(i!=0)
					{
						bw.newLine();
					}
					bw.write(readLine+DELIM);
					i++;
				}
				break;

				//Handle the date part of the commit
				case TYPE_DATE:
				bw.write(readLine+DELIM);
				break;

				//Handle the files that were added/modified.
				case TYPE_ADDEDMODIFIED:
				bw.write(lineSplit[TYPE_IDX]+DELIM);

				//Ignore all other cases.
				default:
				break;
			}

		}
		
		br.close();
		bw.close();
		System.out.println("Log Parsed.");
	}

	//This method sets the case number to switch to.
	public int setType(String[] lineSplit)
	{
		if(lineSplit.length>0)
		{
			if(lineSplit[0].equals("commit"))
			{
				return 1;
			}
			else if(lineSplit[0].equals("Date:"))
			{
				return 2;
			}
			else if(lineSplit.length==1 && (lineSplit[0].contains("M") || lineSplit[0].contains("A")))
			{
				return 3;
			}
			else
			{
				return -1;
			}
		}
		else
		{
			return -1;
		}
	}
}
