package org.bioinfo.gcsa.ws;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.UriInfo;

import nl.bitwalker.useragentutils.Browser;
import nl.bitwalker.useragentutils.OperatingSystem;
import nl.bitwalker.useragentutils.UserAgent;

import org.apache.log4j.Logger;
import org.bioinfo.commons.Config;
import org.bioinfo.gcsa.lib.account.CloudSessionManager;

@Path("/")
@Produces("text/plain")
public class GenericWSServer {

	protected UriInfo uriInfo;
	protected Logger logger = Logger.getLogger(this.getClass());
	protected ResourceBundle properties;
	protected Config config;

	protected String accountId;
	protected String sessionId;
	protected String sessionIp;
	protected String of;

	protected MultivaluedMap<String, String> params;

	/**
	 * Only one CloudSessionManager
	 */
	protected static CloudSessionManager cloudSessionManager;
	static {
		try {
			cloudSessionManager = new CloudSessionManager();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("AccountWSServer: static cloudSessionManager");
	}

	public GenericWSServer(@Context UriInfo uriInfo, @Context HttpServletRequest httpServletRequest) throws IOException {
		this.uriInfo = uriInfo;
		this.params = this.uriInfo.getQueryParameters();
		this.sessionId = (this.params.get("sessionid") != null) ? this.params.get("sessionid").get(0) : "";
		this.of = (this.params.get("of") != null) ? this.params.get("of").get(0) : "";
		this.sessionIp = httpServletRequest.getRemoteAddr();

		UserAgent userAgent = UserAgent.parseUserAgentString(httpServletRequest.getHeader("User-Agent"));

		Browser br = userAgent.getBrowser();

		OperatingSystem op = userAgent.getOperatingSystem();

		logger.info("---------------------------------------");
		// logger.info("------------------->" + br.getName());
		// logger.info("------------------->" + br.getBrowserType().getName());
		// logger.info("------------------->" + op.getName());
		// logger.info("------------------->" + op.getId());
		// logger.info("------------------->" + op.getDeviceType().getName());

		properties = ResourceBundle.getBundle("org.bioinfo.gcs.ws.application");
		config = new Config(properties);

		File dqsDir = new File(properties.getString("DQS.PATH"));
		if (dqsDir.exists()) {
			File accountsDir = new File(properties.getString("ACCOUNTS.PATH"));
			if (!accountsDir.exists()) {
				accountsDir.mkdir();
			}
		}
	}

	@GET
	@Path("/echo/{message}")
	public Response echoGet(@PathParam("message") String message) {
		return createOkResponse(message);
	}

	protected java.nio.file.Path parseObjectId(String objectIdFromURL) {
		String[] tokens = objectIdFromURL.split(":");
		// if(tokens.length == 0){
		// return Paths.get(objectIdFromURL);
		// }
		java.nio.file.Path objectPath = Paths.get("");
		for (int i = 0; i < tokens.length; i++) {
			objectPath = objectPath.resolve(Paths.get(tokens[i]));
		}
		return objectPath;
	}

	protected Response createErrorResponse(Object o) {
		String objMsg = o.toString();
		if (objMsg.startsWith("ERROR:")) {
			return buildResponse(Response.ok("" + o));
		} else {
			return buildResponse(Response.ok("ERROR: " + o));
		}
	}

	protected Response createOkResponse(Object o) {
		return buildResponse(Response.ok(o));
	}

	protected Response createOkResponse(Object o1, MediaType o2) {
		return buildResponse(Response.ok(o1, o2));
	}

	protected Response createOkResponse(Object o1, MediaType o2, String fileName) {
		return buildResponse(Response.ok(o1, o2).header("content-disposition", "attachment; filename =" + fileName));
	}

	private Response buildResponse(ResponseBuilder responseBuilder) {
		return responseBuilder.header("Access-Control-Allow-Origin", "*").build();
	}
}
