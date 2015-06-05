import ij.plugin.*;
import ij.*;
import ij.process.*;
import ij.gui.*;
import java.awt.*;


public class ADDStack_ implements PlugIn 
{
	ImagePlus imp1;
	ImagePlus imp2;
	static boolean vertical;

	public void run(String arg) 
	{
		if (!showDialog())
			return;
		ImageStack stack1 = imp1.getStack();
		ImageStack stack2 = imp2.getStack();
		ImageStack stack3 = addTwoStacks(stack1, stack2);
		imp1.changes = false;
		//imp1.close();
		imp2.changes = false;
		//imp2.close();
		new ImagePlus("Added Stacks", stack3).show();
		IJ.register(ADDStack_.class);
	}
	public ImageStack addTwoStacks(ImageStack stack1, ImageStack stack2) 
	{
		int w1 = stack1.getWidth();
		int w2 = stack2.getWidth();
		int h1 = stack1.getHeight();
		int h2 = stack2.getHeight();
		int size1 = stack1.getSize();
		int size2 = stack2.getSize();
		
		int width = Math.max(w1,w2);
		int height = Math.max(h1,h2);
		int size = size1 + size2;
		
		ImageProcessor ip = stack1.getProcessor(1);
		ImageProcessor temp_ip ;
		Color background = Toolbar.getBackgroundColor();
		
		ImageStack stack3 = new ImageStack(width, height, stack1.getColorModel());
		String labels1[] = stack1.getSliceLabels();
		String labels2[] = stack2.getSliceLabels();
		
		for(int i=0 ; i<size1 ; i++)
		{
			IJ.showProgress((double)i/size);
 			temp_ip = ip.createProcessor(width, height);
 			temp_ip.setColor(background);
 			temp_ip.fill();	
 			temp_ip.insert(stack1.getProcessor(i+1), 0, 0);
			String temp = labels1[i];
			//stack1.deleteSlice(1);
			
			stack3.addSlice(temp,temp_ip);
			
		}
		for(int i=0 ; i<size2 ; i++)
		{
			IJ.showProgress((double)i/size);
 			temp_ip = ip.createProcessor(width, height);
 			temp_ip.setColor(background);
 			temp_ip.fill();
			
 			temp_ip.insert(stack2.getProcessor(i+1), 0, 0);
			String temp = labels2[i];
			stack3.addSlice(temp,temp_ip);
			
		}
		
		return stack3;
	}
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
		IJ.showMessage("Addition of two stacks", "ADDStack_ plugin requires two stacks.");
	}

}
