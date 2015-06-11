import ij.plugin.*;
import ij.*;
import ij.process.*;
import ij.gui.*;
import ij.io.ImageReader;
import java.awt.*;
import java.io.File;


public class TimeLapseReg_ implements PlugIn 
{
	ImagePlus imp1;
	ImagePlus imp2;

	public void run(String arg) 
	{
		//if (!showDialog())
			//return;
		//ImageStack stack1 = imp1.getStack();
		//ImageStack stack2 = imp2.getStack();
		ImageStack stack3 = transformThese();
		//imp1.changes = false;
		//imp1.close();
		//imp2.changes = false;
		//imp2.close();
		new ImagePlus("Aligned Stacks", stack3).show();
		IJ.register(TimeLapseReg_.class);
	}
	public ImageStack transformThese()
	{
		TurboReg_ reg = new TurboReg_();
		int width = 562;
		int height = 392;
		String mark1 = "100 100 ";
		String mark2 = "200 200 ";
		String mark3 = "300 300 ";
		int w=0,h=0;
		String target = "0";
		String reference = "0000";
		ImageProcessor temp_ip ;

		ImageStack stack = new ImageStack(562, 392);
		for(int i=0 ; i<8 ; i++)
		{
			String myTurboRegOptions = "-align -file D:\\dataset\\img"+reference+".tif 0 0 "+(width-1)+" "+(height-1)+ " -file D:\\dataset\\img000"+(i+1)+".tif 0 0 "+(width-1)+" "+(height-1)+ " -rigidBody " +mark1+mark1+mark2+mark2+mark3+mark3+"-hideOutput";
			//String myTurboRegOptions = "-transform -file D:\\dataset\\img000"+(i+1)+".tif 562 392 -rigidBody " +mark1+mark1+mark2+mark2+mark3+mark3+"-showOutput";
			reg.run(myTurboRegOptions);

			double spts[][] = reg.getSourcePoints();
			double tpts[][] = reg.getTargetPoints();
			
			IJ.log("Register : image"+i+".tif" );
			IJ.log(" Translation (" + (tpts[0][0] - spts[0][0]) + "," + (tpts[0][1] - spts[0][1]) + ") ");
			double sangle = Math.atan2(spts[2][1] - spts[1][1], spts[2][0] - spts[1][0]);
			double tangle = Math.atan2(tpts[2][1] - tpts[1][1], tpts[2][0] - tpts[1][0]);
			IJ.log(" Angle " + (180.0*(tangle-sangle)/Math.PI));

			
			System.out.println("Hello");
			ImagePlus imp = reg.getTransformedImage();
			stack.addSlice("image", imp.getProcessor());
		}
		//new ImagePlus("Aligned",stack).show();
		
		
		return stack;		
	}
	
	// following function not required yet will be used later to input the transformation settings
	public boolean showDialog() 
	{
		int[] wList = WindowManager.getIDList();
		if (wList==null || wList.length<2) 
		{
			error();
			return false;
		}
		String[] titles = new String[wList.length];
		for (int i=0; i<wList.length; i++) 
		{
			ImagePlus imp = WindowManager.getImage(wList[i]);
			titles[i] = imp!=null?imp.getTitle():"";
		}

		GenericDialog gd = new GenericDialog("ADDStack_");
		gd.addChoice("Stack1:", titles, titles[0]);
		gd.addChoice("Stack2:", titles, titles[1]);
		gd.showDialog();
		
		if (gd.wasCanceled())
			return false;
		
		int index1 = gd.getNextChoiceIndex();
		int index2 = gd.getNextChoiceIndex();
		
		imp1 = WindowManager.getImage(wList[index1]);
		imp2 = WindowManager.getImage(wList[index2]);
		return true;
	}
	void error() 
	{
		IJ.showMessage("Random error goes here later", "error message comes here later.");
	}

}

	
	
