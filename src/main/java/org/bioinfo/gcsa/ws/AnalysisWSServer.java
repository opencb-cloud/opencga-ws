package org.bioinfo.gcsa.ws;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.bioinfo.gcsa.lib.analysis.AnalysisJobExecuter;


@Path("/analysis")
public class AnalysisWSServer extends GenericWSServer {
	
	public AnalysisWSServer(@Context UriInfo uriInfo) throws IOException {
		super(uriInfo);
	}
	
	@GET
	@Path("/{plugin}")
	public Response analysisGet(@DefaultValue("") @PathParam("plugin") String plugin) {
		MultivaluedMap<String, String> params = this.uriInfo.getQueryParameters();
		System.out.println("**GET executed***");
		System.out.println("get params: "+params);
		params.add("tool", plugin);
		
		return this.analysis(params);
	}
	
	@POST
	@Path("/{plugin}")
	@Consumes({MediaType.MULTIPART_FORM_DATA, MediaType.APPLICATION_FORM_URLENCODED})
	public Response analysisPost(@DefaultValue("") @PathParam("plugin") String plugin, MultivaluedMap<String, String> params) {
		System.out.println("**POST executed***");
		System.out.println("post params: "+params);
		params.add("tool", plugin);
		
		return this.analysis(params);
	}
	
	private Response analysis(MultivaluedMap<String, String> params) {
		System.out.println("params: "+params.toString());

		//call to SW binary with params
		String jobId = "";
//		String jobId = execute("SW","HPG.SW", dataIds, params, "-d");
		try {
			AnalysisJobExecuter a = new AnalysisJobExecuter();
			jobId = a.execute(params);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return createOkResponse(jobId);
	}
	
	
}
