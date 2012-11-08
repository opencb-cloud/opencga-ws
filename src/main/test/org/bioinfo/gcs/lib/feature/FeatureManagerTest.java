package org.bioinfo.gcs.lib.feature;

import java.io.IOException;

import org.junit.Test;



public class FeatureManagerTest {

	@Test
	public void test() {
		FeatureManager fm = new FeatureManager("/home/echirivella/Downloads/testbed/","Homo.bed.gz");
		try {
			
			System.out.println(fm.getByRegion("Homo.bed.gz","chr10", 80000, 100000));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
