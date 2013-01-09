package org.bioinfo.gcsa.ws;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.bioinfo.gcsa.lib.account.beans.Acl;
import org.bioinfo.gcsa.lib.account.beans.AnalysisPlugin;
import org.bioinfo.gcsa.lib.account.db.AccountManagementException;
import org.bioinfo.gcsa.lib.account.io.IOManagementException;
import org.bioinfo.gcsa.lib.analysis.AnalysisExecutionException;
import org.bioinfo.gcsa.lib.analysis.AnalysisJobExecuter;
import org.bioinfo.gcsa.lib.analysis.beans.Analysis;
import org.bioinfo.gcsa.lib.analysis.beans.Execution;
import org.bioinfo.gcsa.lib.analysis.beans.InputParam;

@Path("/analysis")
public class AnalysisWSServer extends GenericWSServer {
	AnalysisJobExecuter aje;
	String baseUrl;

	public AnalysisWSServer(@Context UriInfo uriInfo, @Context HttpServletRequest httpServletRequest)
			throws IOException {
		super(uriInfo, httpServletRequest);
		baseUrl = uriInfo.getBaseUri().toString();
	}

	@GET
	@Path("/{analysis}")
	public Response help1(@DefaultValue("") @PathParam("analysis") String analysis) {
		try {
			aje = new AnalysisJobExecuter(analysis);
		} catch (Exception e) {
			logger.error(e.toString());
			return createErrorResponse("ERROR: analysis not found.");
		}
		return createOkResponse(aje.help(baseUrl));
	}

	@GET
	@Path("/{analysis}/help")
	public Response help2(@DefaultValue("") @PathParam("analysis") String analysis) {
		try {
			aje = new AnalysisJobExecuter(analysis);
		} catch (Exception e) {
			logger.error(e.toString());
			return createErrorResponse("ERROR: analysis not found.");
		}
		return createOkResponse(aje.help(baseUrl));
	}

	@GET
	@Path("/{analysis}/params")
	public Response showParams(@DefaultValue("") @PathParam("analysis") String analysis) {
		try {
			aje = new AnalysisJobExecuter(analysis);
		} catch (Exception e) {
			logger.error(e.toString());
			return createErrorResponse("ERROR: analysis not found.");
		}
		return createOkResponse(aje.params());
	}

	@GET
	@Path("/{analysis}/test")
	public Response test(@DefaultValue("") @PathParam("analysis") String analysis) {
		try {
			aje = new AnalysisJobExecuter(analysis);
		} catch (Exception e) {
			logger.error(e.toString());
			return createErrorResponse("ERROR: analysis not found.");
		}

		// Create job
		String jobId;
		try {
			jobId = cloudSessionManager.createJob("", null, "", "", new ArrayList<String>(), "", sessionId);
			String jobFolder = "/tmp/";
			return createOkResponse(aje.test(jobId, jobFolder));
		} catch (AccountManagementException | IOManagementException | AnalysisExecutionException e) {
			logger.error(e.toString());
			return createErrorResponse("ERROR: could not create job.");
		}
	}

	@GET
	@Path("/{analysis}/status")
	public Response status(@DefaultValue("") @PathParam("analysis") String analysis,
			@DefaultValue("") @QueryParam("jobid") String jobId) {
		try {
			aje = new AnalysisJobExecuter(analysis);
			return createOkResponse(aje.status(jobId));
		} catch (Exception e) {
			logger.error(e.toString());
			return createErrorResponse("ERROR: analysis not found.");
		}

	}

	@GET
	@Path("/{analysis}/run")
	public Response analysisGet(@DefaultValue("") @PathParam("analysis") String analysis) {
		// MultivaluedMap<String, String> params =
		// this.uriInfo.getQueryParameters();
		logger.debug("get params: " + params);

		return this.analysis(analysis, params);
	}

	@POST
	@Path("/{analysis}/run")
//	@Consumes({ MediaType.MULTIPART_FORM_DATA, MediaType.APPLICATION_FORM_URLENCODED })
	public Response analysisPost(@DefaultValue("") @PathParam("analysis") String analysis,
			MultivaluedMap<String, String> postParams) {
		logger.debug("post params: " + postParams);

		return this.analysis(analysis, postParams);
	}

	private Response analysis(String analysisStr, MultivaluedMap<String, String> params) {
		if (params.containsKey("sessionid")) {
			sessionId = params.get("sessionid").get(0);
			params.remove("sessionid");
		} else {
			return createErrorResponse("ERROR: session is not initialized yet.");
		}

		String accountId = null;
		if (params.containsKey("accountid")) {
			accountId = params.get("accountid").get(0);
			params.remove("accountid");
		} else {
			return createErrorResponse("ERROR: unknown account.");
		}

		String bucket = null;
		if (params.containsKey("jobdestinationbucket")) {
			bucket = params.get("jobdestinationbucket").get(0);
			params.remove("jobdestinationbucket");
		} else {
			return createErrorResponse("ERROR: unspecified destination bucket.");
		}

		// Jquery put this parameter and it is sent to the tool
		if (params.containsKey("_")) {
			params.remove("_");
		}

		String analysisName = analysisStr;
		if (analysisStr.contains(".")) {
			analysisName = analysisStr.split("\\.")[0];
		}

		
		String analysisOwner = "system";
		boolean hasPermission = false;
		try {
			List<AnalysisPlugin> userAnalysis = cloudSessionManager.getUserAnalysis(sessionId);
			for (AnalysisPlugin a : userAnalysis) {
				if (a.getName().equals(analysisName)) {
					analysisOwner = a.getOwnerId();
					// get execution permissions
					for(Acl acl : a.getAcl()) {
						if(acl.getAccountId().equals(accountId) && acl.isExecute()) {
							hasPermission = true;
							break;
						}
					}
					break;
				}
			}
		} catch (AccountManagementException e) {
			logger.error(e.toString());
			return createErrorResponse("ERROR: invalid session id.");
		}
		
		// check execution permissions
		if(!analysisOwner.equals("system") && !hasPermission) {
			return createErrorResponse("ERROR: invalid session id.");
		}

		Analysis analysis = null;
		try {
			aje = new AnalysisJobExecuter(analysisStr, analysisOwner);
			analysis = aje.getAnalysis();
		} catch (Exception e) {
			logger.error(e.toString());
			return createErrorResponse("ERROR: analysis not found.");
		}

		Execution execution = null;
		try {
			execution = aje.getExecution();
		} catch (AnalysisExecutionException e) {
			logger.error(e.toString());
			return createErrorResponse("ERROR: executable not found.");
		}

		String jobName = "";
		if (params.containsKey("jobname")) {
			jobName = params.get("jobname").get(0);
			params.remove("jobname");
		}

		String jobFolder = null;
		if (params.containsKey("outdir")) {
			jobFolder = params.get("outdir").get(0);
			params.remove("outdir");
		}

		boolean example = false;
		if (params.containsKey("example")) {
			example = Boolean.parseBoolean(params.get("example").get(0));
			params.remove("example");
		}
		String toolName = analysis.getId();

		// Set input param
		List<String> dataList = new ArrayList<String>();
		for (InputParam inputParam : execution.getInputParams()) {
			if (params.containsKey(inputParam.getName())) {
				List<String> dataIds = Arrays.asList(params.get(inputParam.getName()).get(0).split(","));
				List<String> dataPaths = new ArrayList<String>();
				for (String dataId : dataIds) {
					String dataPath = null;
					if (example) { // is a example
						dataPath = aje.getExamplePath(dataId);
					} else { // is a dataId
						dataPath = cloudSessionManager.getObjectPath(accountId, null, parseObjectId(dataId));
					}

					if (dataPath.contains("ERROR")) {
						return createErrorResponse(dataPath);
					} else {
						dataPaths.add(dataPath);
						dataList.add(dataPath);
					}
				}
				params.put(inputParam.getName(), dataPaths);
			}
		}

		String jobId;
		try {
			jobId = cloudSessionManager.createJob(jobName, jobFolder, bucket, toolName, dataList, null, sessionId);
		} catch (AccountManagementException | IOManagementException e) {
			logger.error(e.toString());
			return createErrorResponse("ERROR: could not create job.");
		}

		if (jobFolder == null) {
			jobFolder = cloudSessionManager.getJobFolder(bucket, jobId, sessionId);
		}

		// Set output param
		params.put(execution.getOutputParam(), Arrays.asList(jobFolder));

		// Create commmand line
		String commandLine = null;
		try {
			commandLine = aje.createCommandLine(execution.getExecutable(), params);
			cloudSessionManager.setJobCommandLine(accountId, jobId, commandLine);
		} catch (AccountManagementException | AnalysisExecutionException e) {
			logger.error(e.toString());
			return createErrorResponse(e.getMessage());
		}

		try {
			aje.execute(jobId, jobFolder, commandLine);
		} catch (AnalysisExecutionException e) {
			logger.error(e.toString());
			return createErrorResponse("ERROR: execution failed.");
		}

		return createOkResponse(jobId);
	}
}
