package org.bioinfo.gcs.lib.source;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;

public class S3Source implements Source{
 
	@Override
	public InputStream getInputStream(String path) {
		String ak = "AKIAI3BZQ2VG6GPWQBVA";
		String sk = "oDDIv+OAQeQVj9sy1CcWeeJsOMAhbh9KIpJ7hiDK";
		String bucket = "nacho-s3";
		AWSCredentials myCredentials = new BasicAWSCredentials(ak,sk); 
		AmazonS3Client s3Client = new AmazonS3Client(myCredentials);
		
		S3Object object = s3Client.getObject(new GetObjectRequest(bucket, path));
		
		return object.getObjectContent();
	}

}
