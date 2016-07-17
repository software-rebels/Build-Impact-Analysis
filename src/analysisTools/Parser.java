package analysisTools;

import java.util.*;
import java.io.*;

public class Parser
{
	private String inFile;

  private final int TYPE_IDX = 0;
  private final String TYPE_COMMIT = "commit";
  private final String TYPE_DATE = "Date:";
  private final String TYPE_MODIFIED = "M";
  private final String TYPE_ADDED = "A";

  private final int CID_IDX = 1;
  private final int CID_LENGTH = 40;

  private final String DELIM = ",";
		
	public Parser(String inFile)
	{
		this.inFile= inFile;
	}
	
	public void parseLog(String destinationFile, int noOfCommitsToParse) throws IOException
	{
		BufferedReader br = new BufferedReader(new FileReader(new File(inFile)));
		BufferedWriter bw = new BufferedWriter(new FileWriter(new File(destinationFile)));
		String readLine = br.readLine();
		
		for(int i = 0; i<noOfCommitsToParse+1; readLine = br.readLine())
		{
			String[] lineSplit=readLine.split(" ");

      // Skip the lines that cannot be split
      if(lineSplit.length <= 0)
      {
        continue;
      }

      switch(lineSplit[TYPE_IDX])
			{
        case TYPE_COMMIT: // Handle the start of a commit
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

        case TYPE_DATE: // Handle the date part of a commit
          bw.write(readLine+DELIM);
          break;

        case TYPE_MODIFIED: // Handle the files in a commit
        case TYPE_ADDED:
				  if(lineSplit.length==1)
				  {
					  bw.write(lineSplit[TYPE_IDX]+DELIM);
				  }
          break;

        default:
          // TODO: Is there anything that needs to be done here?
          break;
		  }
    }
		
		br.close();
		bw.close();
		System.out.println("Log Parsed.");
	}
}
