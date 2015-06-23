import ij.plugin.*;
import ij.*;
import ij.process.*;
import ij.gui.*;
import ij.io.FileInfo;
import ij.io.ImageReader;
import java.awt.*;
import java.io.*;
import java.util.*;

public class TimeLapseRegAlign_ implements PlugIn 
{
	public void run(String arg) 
	{
		if (!showDialog())
			return;
		ImageStack stack3 = null;
		try {
			stack3 = transformThese();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		new ImagePlus("Aligned Stacks", stack3).show();
		IJ.register(TimeLapseRegAlign_.class);
	}
	public ImageStack transformThese()throws IOException
	{
		String directry = "D:\\dataset\\";
		String reference_img = "D:\\dataset\\img0000.tif";
		String path2 = "C:\\Users\\Raghavender Sahdev\\Desktop\\BITS\\GSoC2015\\ImageJ ICNF\\"; //path where transformations file will be stored
		ImagePlus ip = new ImagePlus(reference_img);
		FileInfo infoF = ip.getFileInfo();
		
		TurboReg_ reg = new TurboReg_();
		
		int width = infoF.width;
		int height = infoF.height;
		
		//System.out.println(width+" width "+ height+" height" +infoF.url );
		
		String mark1 = "0 "+height/2+" ";
		String mark2 = width/2+" " + height/2 +" ";
		String mark3 = width/2 +" "+ height + " ";
		FileWriter transform = new FileWriter(path2 + "transformations.csv");
		BufferedWriter br = new BufferedWriter(transform);
		//br.write("hello worldddzz..!!");
		
		
				
		ImageStack stack = new ImageStack(width, height);
		File folder = new File(directry);
		ArrayList<String> target = listFilesForFolder(folder);
		
		for(int i=0 ; i<target.size() ; i++)
		{
			String myTurboRegOptions = "-align -file "+reference_img+" 0 0 "+(width-1)+" "+(height-1)+ " -file "+directry+target.get(i)+ " 0 0 "+(width-1)+" "+(height-1)+ " -rigidBody " +mark1+mark1+mark2+mark2+mark3+mark3+"-hideOutput";
			//String myTurboRegOptions = "-transform -file D:\\dataset\\img000"+(i+1)+".tif 562 392 -rigidBody " +mark1+mark1+mark2+mark2+mark3+mark3+"-showOutput";
			reg.run(myTurboRegOptions);

			double spts[][] = reg.getSourcePoints();
			double tpts[][] = reg.getTargetPoints();
			
			IJ.log("Register : image"+i+".tif" );
			IJ.log(" Translation (" + (tpts[0][0] - spts[0][0]) + "," + (tpts[0][1] - spts[0][1]) + ") ");
			double sangle = Math.atan2(spts[2][1] - spts[1][1], spts[2][0] - spts[1][0]);
			double tangle = Math.atan2(tpts[2][1] - tpts[1][1], tpts[2][0] - tpts[1][0]);
			IJ.log(" Angle " + (180.0*(tangle-sangle)/Math.PI));
			br.write((tpts[0][0] - spts[0][0]) + "," + (tpts[0][1] - spts[0][1]) + ","+(180.0*(tangle-sangle)/Math.PI));
			// uncomment to store the points in the csv files
			//br.write(tpts[0][0]+","+ tpts[0][1]) + "," + spts[0][0]+","+spts[0][1]);
			br.newLine();
			
			
			//System.out.println("Hello: "+(i+1));
			ImagePlus imp = reg.getTransformedImage();
			stack.addSlice("image", imp.getProcessor());
		}
		br.close();
		
		//new ImagePlus("Aligned",stack).show();
		return stack;
	}
	//to be used in the transformThese function to iterate over the files
	public ArrayList<String> listFilesForFolder(final File folder) 
	{
	    ArrayList<String> arr = new ArrayList<String>();
		for (final File fileEntry : folder.listFiles()) {
	        if (fileEntry.isDirectory()) {
	            listFilesForFolder(fileEntry);
	        } else {
	            //System.out.println(fileEntry.getName());
	            arr.add(fileEntry.getName());
	        }
	    }
		return arr;
	}
	
	// following function not required yet will be used later to input the transformation settings
	public boolean showDialog() 
	{
		
		GenericDialog gd = new GenericDialog("ADDStack_");
		gd.addTextAreas("Enter File Path here", "", 1, 1);
		
		
		if (gd.wasCanceled())
			return false;
		
		return true;
	}
	void error() 
	{
		IJ.showMessage("Random error goes here later", "error message comes here later.");
	}
}
