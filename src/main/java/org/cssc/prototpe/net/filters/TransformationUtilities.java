package org.cssc.prototpe.net.filters;

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
		byte[] ret = new byte[text.length];

		for(int i = 0; i < text.length; i++) {
			switch(text[i]) {
			case 'a':
			case 'A':
				ret[i] = '4';
				break;
			case 'e':
			case 'E':
				ret[i] = '3';
				break;
			case 'i':
			case 'I':
				ret[i] = '1';
				break;
			case 'o':
			case '0':
				ret[i] = '0';
				break;
			default:
				ret[i] = text[i];
			}
		}

		return ret;
	}

	public static byte[] transform180Image(byte[] byteArray) throws IOException{
		InputStream in = new ByteArrayInputStream(byteArray);
		BufferedImage image = javax.imageio.ImageIO.read(in);
		if(image == null) {
			return null;
		}
		double angleOfRotation=180.0;
		BufferedImage processedImage=rotateMyImage(image, angleOfRotation);
		ByteArrayOutputStream baos = new ByteArrayOutputStream( 1000 );
		ImageIO.write( processedImage, "png" , baos );
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