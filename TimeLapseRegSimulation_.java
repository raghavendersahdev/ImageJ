import ij.plugin.*;
import ij.*;
import ij.process.*;
import ij.gui.*;
import ij.io.FileInfo;
import ij.io.FileSaver;
import ij.io.ImageReader;
import ij.io.ImageWriter;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.*;

public class TimeLapseRegSimulation_ implements PlugIn, ActionListener 
{
	ImageStack stack = null;
	public void run(String arg) 
	{
		if(!showDialog())
			return;
		
		IJ.register(TimeLapseRegSimulation_.class);
	}
	public ImageStack transformThese()throws IOException
	{
		String directry = "D:\\dataset\\";
		String dir2 = "C:\\Users\\Raghavender Sahdev\\Desktop\\BITS\\GSoC2015\\ImageJ ICNF\\transformed\\";
		String reference_img = "D:\\dataset\\img0000.tif";
		ImagePlus ip = new ImagePlus(reference_img);
		FileInfo infoF = ip.getFileInfo();
		
		
		int width = infoF.width;
		int height = infoF.height;
		
		//System.out.println(width+" width "+ height+" height" +infoF.url );
		
		
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
			ip3.translate(values[i][0], values[i][1]);
			ip3.rotate(values[i][2]);
			
			System.out.println("Hello: "+(i+1));
			stack.addSlice("image", ip2.getProcessor());
			FileSaver fs = new FileSaver(ip2);
			String path = dir2+"img"+i+".tiff";
			fs.saveAsTiff(path);
			
			/*FileInfo fi = ip2.getFileInfo();
			fi.pixels = ip2.getFileInfo();
			ImageWriter img_wrt= new ImageWriter(fi);
		
			OutputStream outStream = new FileOutputStream("C:\\Users\\Raghavender Sahdev\\Desktop\\BITS\\GSoC2015\\ImageJ ICNF\\sample22.tif");
			img_wrt.write(outStream);
			
			outStream.close();
			break;*/
		}
		
		//new ImagePlus("Aligned",stack).show();
		return stack;
	}
	public ImageStack alignThese()throws IOException
	{
		String directry = "D:\\Datasets2\\transformed\\";
		String reference_img = "D:\\Datasets2\\transformed\\img0.tiff";
		ImagePlus ip = new ImagePlus(reference_img);
		FileInfo infoF = ip.getFileInfo();
		
		TurboReg_ reg = new TurboReg_();
		
		int width = infoF.width;
		int height = infoF.height;
		
		//System.out.println(width+" width "+ height+" height" +infoF.url );
		
		String mark1 = "0 "+height/2+" ";
		String mark2 = width/2+" " + height/2 +" ";
		String mark3 = width/2 +" "+ height + " ";
		FileWriter transform = new FileWriter("C:\\Users\\Raghavender Sahdev\\Desktop\\BITS\\GSoC2015\\ImageJ ICNF\\transformations_simulation.csv");
		BufferedWriter br = new BufferedWriter(transform);
		//br.write("hello worldddzz..!!");
		
		//BufferedWriter br2 = new BufferedWriter(new FileWriter("fff.jpeg"));
				
		ImageStack stack = new ImageStack(width, height);
		File folder = new File(directry);
		ArrayList<String> arr = listFilesForFolder(folder); // stores the target file names
		
		for(int i=0 ; i<arr.size() ; i++)
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
			// uncomment to store the points in the csv files
			//br.write(tpts[0][0]+","+ tpts[0][1]) + "," + spts[0][0]+","+spts[0][1]);
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
		
		GenericDialog gd = new GenericDialog("TimeLapseRegSimulation");
		Button comp = new Button("Apply Transform");
		comp.addActionListener(this);
		
		gd.add(comp);
		Button comp2 = new Button("Retrieve Transforms");
		gd.add(comp2);
		comp2.addActionListener(this);
		gd.showDialog();
		
		if (gd.wasCanceled())
			return false;
		
		return true;
	}
	@Override
	public void actionPerformed(ActionEvent e) 
	{
		if(e.getActionCommand() == "Apply Transform")
		{	
			try {
				stack = transformThese();
			} catch (IOException e2) {
				e2.printStackTrace();
			}
			new ImagePlus("Aligned Stacks", stack).show();
			System.out.println("applying transforms!!");
			return;
		}
		else if(e.getActionCommand() == "Retrieve Transforms")
		{
			try {
				stack = alignThese();
			} catch (IOException e2) {
				e2.printStackTrace();
			}
			new ImagePlus("Transformed Stacks", stack).show();
			
			System.out.println("Retrieve Transforms ");
			return;
		}
	}
	void error() 
	{
		IJ.showMessage("Random error goes here later", "error message comes here later.");
	}
	
}
