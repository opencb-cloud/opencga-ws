package org.bioinfo.gcs.lib.feature;

import java.io.File;
import java.io.IOException;

public class FeatureManager {

	public String getByRegion(final String fileName, final String chr, final int start, final int end) throws IOException{
		
		if(fileName.endsWith("gff") || fileName.endsWith("gff.gz")) {
			
		
			return "";
		}
		
		if(fileName.endsWith("bed") || fileName.endsWith("bed.gz")) {
			
			
			return "";
		}

		return "Format unknown";
	}
	
	private String getByRegionGff(String fileName, final String chr, final int start, final int end) {
		
		if(!new File(fileName+".idx").exists()) {
			// crea
			// fileName.gff.gz
			// fileName.gff.tb.gz
			// fileName.gff.idx
			
		}
		
		
		return "";
	}
	
	
}
