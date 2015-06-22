
import ij.plugin.*;
import ij.*;
import ij.process.*;
import ij.gui.*;
import ij.io.FileInfo;
import ij.io.ImageReader;
import java.awt.*;
import java.io.*;
import java.util.*;

public class TimeLapseTranform_ implements PlugIn 
{
	ImagePlus imp1;
	ImagePlus imp2;

	public void run(String arg) 
	{
		//if (!showDialog())
			//return;
		//ImageStack stack1 = imp1.getStack();
		//ImageStack stack2 = imp2.getStack();
		ImageStack stack = null;
		
		try {
			stack = transformThese();
		} catch (IOException e) {
			e.printStackTrace();
		}
		//imp1.changes = false;
		//imp1.close();
		//imp2.changes = false;
		//imp2.close();
		new ImagePlus("Aligned Stacks", stack).show();
		IJ.register(TimeLapseTranform_.class);
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
		FileReader transform = new FileReader("C:\\Users\\Raghavender Sahdev\\Desktop\\BITS\\GSoC2015\\ImageJ ICNF\\transformations.csv");
		BufferedReader br = new BufferedReader(transform);
		
		
		
				
		ImageStack stack = new ImageStack(width, height);
		File folder = new File(directry);
		ArrayList<String> arr = listFilesForFolder(folder);
		int n = arr.size();
		String temp = br.readLine();
		//declare a double array to read the transformations from the file transformations.txt
		double values[][] = new double[n][3]; // 3 for 2 translations and 1 rotation angle
		for(int i=0 ; (temp = br.readLine()) != null ; i++)
		{
			
			StringTokenizer tkn = new StringTokenizer(temp,",");
			values[i][0] = Double.parseDouble(tkn.nextToken()); // stores the translation in x ditection
			values[i][1] = Double.parseDouble(tkn.nextToken()); // stores the translation in y direction
			values[i][2] = Double.parseDouble(tkn.nextToken()); // stores the rotation angle
			
		}
		br.close(); // close reading the transformations file
		
		
		for(int i=0 ; i<arr.size() ; i++)
		{
			
			ImagePlus ip2 = new ImagePlus(directry+arr.get(i));
			FileInfo infoF2 = ip2.getFileInfo();
			ImageProcessor ip3 = ip2.getProcessor();
			ip3.rotate(values[i][2]);
			ip3.translate(values[i][0], values[i][1]);

			System.out.println("Hello: "+(i+1));
			stack.addSlice("image", ip2.getProcessor());
		}
		
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
		// code for inputing parameters goes here
		return true;
	}
	void error() 
	{
		IJ.showMessage("Random error goes here later", "error message comes here later.");
	}
}
