
import ij.plugin.*;
import ij.*;
import ij.process.*;
import ij.gui.*;
import ij.io.FileInfo;
import ij.io.ImageReader;
import java.awt.*;
import java.io.*;
import java.util.*;

public class TimeLapseRegTransform_ implements PlugIn 
{

	public void run(String arg) 
	{
		//if (!showDialog())
			//return;
		ImageStack stack = null;
		
		try {
			stack = transformThese();
		} catch (IOException e) {
			e.printStackTrace();
		}
		new ImagePlus("Aligned Stacks", stack).show();
		IJ.register(TimeLapseRegTransform_.class);
	}
	public ImageStack transformThese()throws IOException
	{
		String directry = "D:\\dataset\\";
		String path2 = "C:\\Users\\Raghavender Sahdev\\Desktop\\BITS\\GSoC2015\\ImageJ ICNF\\";
		String reference_img = "D:\\dataset\\img0000.tif";
		ImagePlus ip = new ImagePlus(reference_img);
		FileInfo infoF = ip.getFileInfo();
		
		
		int width = infoF.width;
		int height = infoF.height;
		
		//System.out.println(width+" width "+ height+" height" +infoF.url );
		
		
		FileReader transform = new FileReader(path2 + "transformations.csv");
		BufferedReader br = new BufferedReader(transform);
		
		
		
				
		ImageStack stack = new ImageStack(width, height);
		File folder = new File(directry);
		ArrayList<String> arr = listFilesForFolder(folder);
		int n = arr.size();
		String temp = br.readLine();
		//declare a double array to read the transformations from the file transformations.csv
		
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
			ImageProcessor ip3 = ip2.getProcessor();
			ip3.translate(values[i][0], values[i][1]);
			ip3.rotate(values[i][2]);
			
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
		GenericDialog gd = new GenericDialog("TimeLapseRegTransform_");
		gd.showDialog();
		
		if (gd.wasCanceled())
			return false;
		
		
		return true;
	}
	void error() 
	{
		IJ.showMessage("Random error goes here later", "error message comes here later.");
	}
}
