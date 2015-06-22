import ij.plugin.*;
import ij.*;
import ij.process.*;
import ij.gui.*;
import ij.io.FileInfo;
import ij.io.ImageReader;
import java.awt.*;
import java.io.*;
import java.util.*;

public class TimeLapseReg_ implements PlugIn 
{

	public void run(String arg) 
	{
		//if (!showDialog())
		//	return;
		//ImageStack stack1 = imp1.getStack();
		//ImageStack stack2 = imp2.getStack();
		ImageStack stack3 = null;
		try {
			stack3 = transformThese();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//imp1.changes = false;
		//imp1.close();
		//imp2.changes = false;
		//imp2.close();
		new ImagePlus("Aligned Stacks", stack3).show();
		IJ.register(TimeLapseReg_.class);
	}
	public ImageStack transformThese()throws IOException
	{
		String directry = "D:\\dataset\\";
		String reference_img = "D:\\dataset\\img0000.tif";
		ImagePlus ip = new ImagePlus(reference_img);
		FileInfo infoF = ip.getFileInfo();
		
		TurboReg_ reg = new TurboReg_();
		
		int width = infoF.width;
		int height = infoF.height;
		
		//System.out.println(width+" width "+ height+" height" +infoF.url );
		
		String mark1 = "0 "+height/2+" ";
		String mark2 = width/2+" " + height/2 +" ";
		String mark3 = width +" "+ height/2 + " ";
		FileWriter transform = new FileWriter("C:\\Users\\Raghavender Sahdev\\Desktop\\BITS\\GSoC2015\\ImageJ ICNF\\transformations.csv");
		BufferedWriter br = new BufferedWriter(transform);
		//br.write("hello worldddzz..!!");
		
		
				
		ImageStack stack = new ImageStack(width, height);
		File folder = new File(directry);
		ArrayList<String> arr = listFilesForFolder(folder);
		
		for(int i=0 ; i<arr.size()-250 ; i++)
		{
			String myTurboRegOptions = "-align -file "+reference_img+" 0 0 "+(width-1)+" "+(height-1)+ " -file "+directry+arr.get(i)+ " 0 0 "+(width-1)+" "+(height-1)+ " -rigidBody " +mark1+mark1+mark2+mark2+mark3+mark3+"-hideOutput";
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
			br.newLine();
			
			
			System.out.println("Hello: "+(i+1));
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
		// code for inputing various parameters goes here
		return true;
	}
	void error() 
	{
		IJ.showMessage("Random error goes here later", "error message comes here later.");
	}
}
