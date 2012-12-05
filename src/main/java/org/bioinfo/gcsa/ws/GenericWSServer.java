package org.bioinfo.gcsa.ws;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
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
import javax.ws.rs.core.UriInfo;

import nl.bitwalker.useragentutils.Browser;
import nl.bitwalker.useragentutils.OperatingSystem;
import nl.bitwalker.useragentutils.UserAgent;

import org.bioinfo.commons.Config;
import org.bioinfo.commons.log.Logger;
import org.bioinfo.gcsa.lib.users.CloudSessionManager;
import org.bioinfo.gcsa.lib.users.persistence.UserManagementException;
import org.bioinfo.gcsa.lib.users.persistence.UserManager;

@Path("/")
@Produces("text/plain")
public class GenericWSServer {

	protected static UserManager userManager;//TODO remove
	
	protected UriInfo uriInfo;
	protected Logger logger;
	protected ResourceBundle properties;
	protected Config config;

	protected String sessionId;
	protected String sessionIp;

	MultivaluedMap<String, String> params;
	
	/**
	 * Only one CloudSessionManager
	 */
	protected static CloudSessionManager cloudSessionManager;
	static{
		try {
			cloudSessionManager = new CloudSessionManager();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UserManagementException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("AccountWSServer: static cloudSessionManager");
		userManager = cloudSessionManager.getUserManager();//TODO remove
	}

	public GenericWSServer(@Context UriInfo uriInfo, @Context HttpServletRequest httpServletRequest) throws IOException {
		this.uriInfo = uriInfo;
		this.params = this.uriInfo.getQueryParameters();
		this.sessionId = (this.params.get("sessionid") != null) ? this.params.get("sessionid").get(0) : "";
		this.sessionIp = httpServletRequest.getRemoteAddr();
		logger = new Logger();
		logger.setLevel(Logger.INFO_LEVEL);

		UserAgent userAgent = UserAgent.parseUserAgentString(httpServletRequest.getHeader("User-Agent"));

		Browser br = userAgent.getBrowser();

		OperatingSystem op = userAgent.getOperatingSystem();

		logger.info("------------------->" + br.getName());
		logger.info("------------------->" + br.getBrowserType().getName());
		logger.info("------------------->" + op.getName());
		logger.info("------------------->" + op.getId());
		logger.info("------------------->" + op.getDeviceType().getName());


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

	protected Response createErrorResponse(Object o) {
		String objMsg = o.toString();
		if (objMsg.startsWith("ERROR:")) {
			return Response.ok("" + o).header("Access-Control-Allow-Origin", "*").build();
		} else {
			return Response.ok("ERROR: " + o).header("Access-Control-Allow-Origin", "*").build();
		}
	}

	protected Response createOkResponse(Object o) {
		return Response.ok(o).header("Access-Control-Allow-Origin", "*").build();
	}

	protected Response createOkResponse(Object o1, MediaType o2) {
		return Response.ok(o1, o2).header("Access-Control-Allow-Origin", "*").build();
	}

	protected Response createOkResponse(Object o1, MediaType o2, String fileName) {
		return Response.ok(o1, o2).header("content-disposition", "attachment; filename =" + fileName)
				.header("Access-Control-Allow-Origin", "*").build();
	}
}
