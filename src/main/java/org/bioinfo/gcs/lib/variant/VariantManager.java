package org.bioinfo.gcs.lib.variant;

import java.io.IOException;

public class VariantManager {

	public String getByRegion(final String fileName, final String chr, final int start, final int end) throws IOException{
		
		if(fileName.endsWith("vcf") || fileName.endsWith("vcf.gz")) {
			
			
			return "";
		}
		
		if(fileName.endsWith("gvf") || fileName.endsWith("gvf.gz")) {
			
			
			return "";
		}

		return "Format unknown";
	}
}
