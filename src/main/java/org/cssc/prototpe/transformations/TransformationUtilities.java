package org.cssc.prototpe.transformations;
//import javax.media.jai.*;

public class TransformationUtilities {

	public static byte[] transforml33t(byte[] text){
		String aux = new String(text);
		aux=aux.replace('a', '4').replace('e', '3').replace('i', '1').replace('o', '0');
		return aux.getBytes();
	}
	
	public static byte[] transform180Image(byte[] image){
		String filename = "images/Trees.gif";
		//ver http://java.sun.com/products/java-media/jai/forDevelopers/jai1_0_1guide-unc/Geom-image-manip.doc.html#51140
		
//	     PlanarImage im = (PlanarImage)JAI.create("fileload",filename);
		//http://www.oracle.com/technetwork/java/current-142188.html
	     //http://download.java.net/media/jai/builds/release/1_1_3/INSTALL.html
	     //https://jai.dev.java.net/binary-builds.html#Release_builds
	     
	     
		return image;
	}
}
