package org.bioinfo.gcsa.ws;

import java.io.IOException;
import java.util.List;
import java.util.Map;

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
	AnalysisJobExecuter aje;
	String baseUrl;
	
	public AnalysisWSServer(@Context UriInfo uriInfo) throws IOException {
		super(uriInfo);
		baseUrl = uriInfo.getBaseUri().toString();
	}
	
	@GET
	@Path("/{analysis}/help")
	public Response help(@DefaultValue("") @PathParam("analysis") String analysis) {
		try {
			aje = new AnalysisJobExecuter(analysis);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return createOkResponse(aje.help(baseUrl));
	}
	
	@GET
	@Path("/{analysis}/params")
	public Response showParams(@DefaultValue("") @PathParam("analysis") String analysis) {
		try {
			aje = new AnalysisJobExecuter(analysis);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return createOkResponse(aje.params());
	}
	
	@GET
	@Path("/{analysis}/test")
	public Response test(@DefaultValue("") @PathParam("analysis") String analysis) {
		try {
			aje = new AnalysisJobExecuter(analysis);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return createOkResponse(aje.test());
	}
	
	@GET
	@Path("/{jobId}/status")
	public Response status(@DefaultValue("") @PathParam("jobId") String jobId) {
		try {
			aje = new AnalysisJobExecuter();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return createOkResponse(aje.status(jobId));
	}
	
	@GET
	@Path("/{analysis}/run")
	public Response analysisGet(@DefaultValue("") @PathParam("analysis") String analysis) {
		try {
			aje = new AnalysisJobExecuter(analysis);
		} catch (IOException e) {
			e.printStackTrace();
		}
		MultivaluedMap<String, String> params = this.uriInfo.getQueryParameters();
		System.out.println("**GET executed***");
		System.out.println("get params: "+params);
//		params.add("analysis", analysis);
		
		return this.analysis(params);
	}
	
	@POST
	@Path("/{analysis}/run")
	@Consumes({MediaType.MULTIPART_FORM_DATA, MediaType.APPLICATION_FORM_URLENCODED})
	public Response analysisPost(@DefaultValue("") @PathParam("analysis") String analysis, MultivaluedMap<String, String> params) {
		try {
			aje = new AnalysisJobExecuter(analysis);
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("**POST executed***");
		System.out.println("post params: "+params);
//		params.add("analysis", analysis);
		
		return this.analysis(params);
	}
	
	private Response analysis(MultivaluedMap<String, String> params) {
		System.out.println("params: "+params.toString());
		Map<String, List<String>> paramsMap = params;

		String jobId = "";
//		String jobId = execute("SW","HPG.SW", dataIds, params, "-d");
		jobId = aje.execute(paramsMap);
		
		return createOkResponse(jobId);
	}
}
