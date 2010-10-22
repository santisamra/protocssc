package org.cssc.prototpe.transformations;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

public class TransformationUtilities {

	public static byte[] transforml33t(byte[] text){
		String aux = new String(text);
		aux=aux.replace('a', '4').replace('e', '3').replace('i', '1').replace('o', '0');
		return aux.getBytes();
	}
	
	public static byte[] transform180Image(byte[] byteArray) throws IOException{
		InputStream in = new ByteArrayInputStream(byteArray);
		BufferedImage image = javax.imageio.ImageIO.read(in);
		double angleOfRotation=180.0;
		BufferedImage processedImage=rotateMyImage(image, angleOfRotation);
		ByteArrayOutputStream baos = new ByteArrayOutputStream( 1000 );
		ImageIO.write( processedImage, "jpeg" , baos );
		baos.flush();
		byte[] result = baos.toByteArray();
		baos.close();
		return result;
	}
		 
	public static BufferedImage rotateMyImage(BufferedImage img, double angle) {
		int w = img.getWidth();
		int h = img.getHeight();
		BufferedImage dimg =new BufferedImage(w, h, img.getType());
		Graphics2D g = dimg.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
		RenderingHints.VALUE_ANTIALIAS_ON);
		 
		g.rotate(Math.toRadians(angle), w/2, h/2);
		 
		g.drawImage(img, null, 0, 0);
		return dimg;
	}
}