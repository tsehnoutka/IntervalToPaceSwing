import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

public class IntervalSplit {
	
	private static void calculatePace(String fileName, String outfile ){
	    int x=1;  // Interval Counter
        float totalDist = 0;  // Total Distance for this Interval
        int totalTime = 0;    // Total Time for this Interval
        String outputString = null;  // the string created to write to the output file
        List<Object[]> data = new ArrayList<Object[]>();
        Object[] row;
        String tmpPace;
 	
	    try {
	        // FileReader reads text files in the default encoding.
	    	FileReader fileReader = new FileReader(fileName);
	    	File file = new File(outfile);
	        file.createNewFile();  // creates the file
	        FileWriter fileWriter = new FileWriter(file); // creates a FileWriter Object
	        
	        fileWriter.write("Interval,Time,Distance,Pace\n"); // Writes the header to the file
	
	        BufferedReader bufferedReader =  new BufferedReader(fileReader);  // Always wrap FileReader in BufferedReader.
	        
	        //  I don't want to do anything with the first(header) and last(summary) lines in the file 
	        String next, line = bufferedReader.readLine();
	        for (boolean first = true, last = (line == null); !last; first = false, line = next) {
                last = ((next = bufferedReader.readLine()) == null);
                 
                //  if it is NOT the header or Summary rows
                if (!first && !last) {
                	List<String> items = Arrays.asList(line.split("\\s*,\\s*"));  // split the file's line
    	        	
    	        	String time = items.get(1);  //  get the interval time
    	        	List<String> minute = Arrays.asList(time.split("\\s*:\\s*"));
    	        	
    	        	//**************************************************************************************
    	        	//  
    	        	//  I am make TWO assumptions here.  If these change, this code has to change 
    	        	//
    	        	//**************************************************************************************
    	        	String strMin = minute.get(0);  //  I KNOW that the intervals are whole minutes.  If we change that , we would have to add sec to totalTime
    	        	if (strMin.length() == 0)  //  if the minute is zero, i am assuming it is the last interval where you are trying to turn the watch off
    	        		last = true;
    	        	else
    	        		totalTime = totalTime + Integer.valueOf(strMin);  //  add the time to the total time
    	        	    	        	    	        	
    	        	Float dist = Float.valueOf(items.get(3));  // Get the interval distance
    	        	totalDist = totalDist + dist; //  add the distance to the total Distance
    	        	
    	        	//  If the distance is greater than or equal to a mile.
    	        	if (totalDist >= 1){  
    	        		double pace = totalTime / totalDist;  // get pace
    	        		int min = (int) pace;  //  get the minute of the pace
    	        		int sec =  (int) ((pace - min)*100);  // get the percentage of a minutes left 
    	        		
    	        		//  write string to output file
    	        		tmpPace = String.format("%2d:%2d",min,(int)(sec*.6) );
    	        		outputString = String.format("%2d,%s,%.2f,%2s\n", x,totalTime,totalDist,tmpPace );
    	        	    fileWriter.write(outputString); 
    	        	    row = new Object[4];
    	        	    row[0] = x;
    	        	    row[1] = totalTime;
    	        	    row[2] = totalDist;
    	        	    row[3] = tmpPace;
    	        	    data.add(row);
    	        	    
    	        		//  Write file to Console   ( i put these on separate line for easy debugging )
    	        	    System.out.format("Interval: %2d\t",x++);  //  Print out interval
    	        		System.out.format("Time: %s\t",totalTime);  //  Print out Time
    	        		System.out.format("Distance: %.2f\t",totalDist);  //  Print out Distance
    	        		System.out.format("Pace: %2d:%2d\n" , min, (int)(sec*.6) );  //  Print out pace;  multiplying by .6 to convert percent of minute to seconds

    	        		//reset totals
    	        		totalTime = 0;
    	        		totalDist = 0;
    	        	}   //  end if total distance is greater than 1 mile             
                }  //  end if First or Last line
                
	        }  //  end for the lines in the file loop		
	        // Always close files.
	        bufferedReader.close();
	        fileWriter.flush();
	        fileWriter.close();	    	
	        System.out.println(data);
	    } //  end try
	    
	    //************************
	    //  need to update this to handle write file issues
	    catch(FileNotFoundException ex) {
	        System.out.println(
	            "Unable to open file '" + 
	            fileName + "'");                
	    }
	    catch(IOException ex) {
	        System.out.println(
	            "Error reading file '" 
	            + fileName + "'");                  
	        // Or we could just do this: 
	        // ex.printStackTrace();
	    }
	} //  end calculatePace
	
	
	public static void main(String[] args) {
		String inputFile  = null;  // CSV file From Garmin
		String outputFile = null;  // CSV file out put with pace  
		
		//  Ask user via dialog and Get file to Open 
		JFileChooser chooser = new JFileChooser();
		FileNameExtensionFilter filter = new FileNameExtensionFilter("Comma Delimited", "csv");  //  only open csv file
		chooser.setFileFilter(filter);
		
		if(chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
			inputFile = outputFile = chooser.getSelectedFile().getPath();
		   
			// Create path and file name of output file
		   int i = inputFile.lastIndexOf('.');
		   if (i > 0) {
			   outputFile = String.format("%sPace.csv", inputFile.substring(0,i));  //  Add Paceto input file naem
		   }
		}
		 		
		    calculatePace( inputFile, outputFile);
		    
	}
	

}
