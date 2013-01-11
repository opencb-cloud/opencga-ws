package org.bioinfo.gcsa.ws;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.UriInfo;

import nl.bitwalker.useragentutils.Browser;
import nl.bitwalker.useragentutils.OperatingSystem;
import nl.bitwalker.useragentutils.UserAgent;

import org.bioinfo.commons.Config;
import org.bioinfo.commons.log.Logger;
import org.bioinfo.gcsa.lib.GcsaUtils;
import org.bioinfo.gcsa.lib.account.CloudSessionManager;
import org.bioinfo.gcsa.lib.account.beans.ObjectItem;
import org.bioinfo.gcsa.lib.account.db.AccountManagementException;

import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataParam;
//import org.bioinfo.gcsa.lib.users.CloudSessionManager;
//import org.bioinfo.gcsa.lib.users.beans.Data;
//import org.bioinfo.gcsa.lib.users.persistence.AccountManagementException;

/**
 * @author fsalavert
 * 
 */
@Path("/")
@Produces("text/plain")
public class GenericWSServer {

	protected UriInfo uriInfo;
	protected Logger logger;
	protected ResourceBundle properties;
	protected Config config;

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
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (AccountManagementException e) {
			// TODO Auto-generated catch block
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
		logger = new Logger();
		logger.setLevel(Logger.INFO_LEVEL);

		UserAgent userAgent = UserAgent.parseUserAgentString(httpServletRequest.getHeader("User-Agent"));

		Browser br = userAgent.getBrowser();

		OperatingSystem op = userAgent.getOperatingSystem();

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

	@POST
	@Path("/{accountid}/{bucketname}/{objectname}/upload")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response uploadData(@DefaultValue("") @PathParam("accountid") String accountId,
			@DefaultValue("") @PathParam("bucketname") String bucketId,
			@DefaultValue("") @PathParam("objectname") String objectIdFromURL,
			@FormDataParam("file") InputStream fileIs, @FormDataParam("file") FormDataContentDisposition fileInfo,
			@FormDataParam("name") @DefaultValue("undefined") String name, @FormDataParam("tags") String tags,
			@DefaultValue("r") @QueryParam("filetype") String filetype,
			@FormDataParam("responsible") @DefaultValue("-") String responsible,
			@FormDataParam("organization") @DefaultValue("-") String organization,
			@FormDataParam("date") @DefaultValue("-") String date,
			@FormDataParam("description") @DefaultValue("-") String description,
			@FormDataParam("jobid") @DefaultValue("-1") String jobid,
			@DefaultValue("false") @QueryParam("parents") boolean parents) {

		java.nio.file.Path objectId = parseObjectId(objectIdFromURL);

		ObjectItem objectItem = new ObjectItem(null, null, null);// TODO PAKO
																	// COMPROBAR
																	// CONSTRUCTOR
		objectItem.setFileFormat(tags);
		objectItem.setFileType(filetype);
		objectItem.setResponsible(responsible);
		objectItem.setOrganization(organization);
		objectItem.setDate(GcsaUtils.getTime());
		objectItem.setDescription(description);

		try {
			String res = cloudSessionManager.createObjectToBucket(accountId, bucketId, objectId, objectItem, fileIs,
					parents, sessionId);
			return createOkResponse(res);
		} catch (Exception e) {
			logger.error(e.toString());
			return createErrorResponse(e.getMessage());
		}
	}

	@GET
	@Path("/{accountid}/{bucketname}/{objectname}/createdirectory")
	public Response createDirectory(@DefaultValue("") @PathParam("accountid") String accountId,
			@DefaultValue("") @PathParam("bucketname") String bucketId,
			@DefaultValue("") @PathParam("objectname") String objectIdFromURL,
			@DefaultValue("dir") @QueryParam("filetype") String filetype,
			@DefaultValue("false") @QueryParam("parents") boolean parents) {

		java.nio.file.Path objectId = parseObjectId(objectIdFromURL);

		ObjectItem objectItem = new ObjectItem(null, null, null);
		objectItem.setFileType(filetype);
		objectItem.setDate(GcsaUtils.getTime());
		try {
			String res = cloudSessionManager.createFolderToBucket(accountId, bucketId, objectId, objectItem, parents,
					sessionId);
			return createOkResponse(res);
		} catch (Exception e) {
			logger.error(e.toString());
			return createErrorResponse(e.getMessage());
		}
	}

	@GET
	@Path("/{accountid}/{bucketname}/{objectname}/delete")
	public Response deleteData(@DefaultValue("") @PathParam("accountid") String accountId,
			@DefaultValue("") @PathParam("bucketname") String bucketId,
			@DefaultValue("") @PathParam("objectname") String objectIdFromURL) {

		java.nio.file.Path objectId = parseObjectId(objectIdFromURL);
		try {
			cloudSessionManager.deleteDataFromBucket(accountId, bucketId, objectId, sessionId);
			return createOkResponse("OK");
		} catch (Exception e) {
			logger.error(e.toString());
			return createErrorResponse(e.getMessage());
		}
	}

	@GET
	@Path("/{accountid}/{bucketname}/job/{jobid}/result.{format}")
	public Response getResultFile(@DefaultValue("") @PathParam("accountid") String accountId,
			@DefaultValue("") @PathParam("bucketname") String bucketId,
			@DefaultValue("") @PathParam("jobid") String jobId, @PathParam("format") String format) {
		try {
			String res = cloudSessionManager.getJobResultFromBucket(accountId, bucketId, jobId, sessionId);
			return createOkResponse(res);
		} catch (Exception e) {
			logger.error(e.toString());
			return createErrorResponse(e.getMessage());
		}
	}

	@GET
	@Path("/{accountid}/{bucketname}/job/{jobid}/table")
	public Response table(@DefaultValue("") @PathParam("accountid") String accountId,
			@DefaultValue("") @PathParam("bucketname") String bucketId,
			@DefaultValue("") @PathParam("jobid") String jobId,
			@DefaultValue("") @QueryParam("filename") String filename,
			@DefaultValue("") @QueryParam("start") String start, @DefaultValue("") @QueryParam("limit") String limit,
			@DefaultValue("") @QueryParam("colNames") String colNames,
			@DefaultValue("") @QueryParam("colVisibility") String colVisibility,
			@DefaultValue("") @QueryParam("callback") String callback,
			@QueryParam("sort") @DefaultValue("false") String sort) {

		try {
			String res = cloudSessionManager.getFileTableFromJob(accountId, bucketId, jobId, filename, start, limit,
					colNames, colVisibility, callback, sort, sessionId);
			return createOkResponse(res);
		} catch (Exception e) {
			logger.error(e.toString());
			return createErrorResponse(e.getMessage());
		}
	}

	// @GET
	// @Path("{jobId}/poll")
	// public Response pollJobFile(@PathParam("jobId") String jobId,
	// @QueryParam("filename") String filename, @DefaultValue("true")
	// @QueryParam("zip") String zip) {
	// logger.debug("POLLING "+ filename + "...");
	//
	@GET
	@Path("/{accountid}/{bucketname}/job/{jobid}/poll")
	public Response pollJobFile(@DefaultValue("") @PathParam("accountid") String accountId,
			@DefaultValue("") @PathParam("bucketname") String bucketId,
			@DefaultValue("") @PathParam("jobid") String jobId,
			@DefaultValue("") @QueryParam("filename") String filename,
			@DefaultValue("true") @QueryParam("zip") String zip) {

		try {

			DataInputStream is = cloudSessionManager.getFileFromJob(accountId, bucketId, jobId, filename, zip,
					sessionId);
			String name = null;
			if (zip.compareTo("true") != 0) {// PAKO zip != true
				name = filename;
			} else {
				name = filename + ".zip";
			}
			return createOkResponse(is, MediaType.APPLICATION_OCTET_STREAM_TYPE, name);
		} catch (Exception e) {
			logger.error(e.toString());
			return createErrorResponse(e.getMessage());
		}
	}

	@GET
	@Path("/{accountid}/{bucketname}/job/{jobid}/status")
	public Response getJobStatus(@DefaultValue("") @PathParam("accountid") String accountId,
			@DefaultValue("") @PathParam("bucketname") String bucketId,
			@DefaultValue("") @PathParam("jobid") String jobId) {
		try {
			String res = cloudSessionManager.checkJobStatus(accountId, jobId, sessionId);
			return createOkResponse(res);
		} catch (Exception e) {
			logger.error(e.toString());
			return createErrorResponse(e.getMessage());
		}
	}

	@GET
	@Path("/{accountid}/{bucketname}/{objectname}/{region}/region/")
	public Response region(@DefaultValue("") @PathParam("accountid") String accountId,
			@DefaultValue("") @PathParam("bucketname") String bucketId,
			@DefaultValue("") @PathParam("objectname") String objectIdFromURL,
			@DefaultValue("") @PathParam("region") String regionStr) {
		java.nio.file.Path objectId = parseObjectId(objectIdFromURL);
		try {
			String res = cloudSessionManager.region(accountId, bucketId, objectId, regionStr, params, sessionId);
			return createOkResponse(res);
		} catch (Exception e) {
			logger.error(e.toString());
			return createErrorResponse(e.getMessage());
		}
	}

	protected java.nio.file.Path parseObjectId(String objectIdFromURL) {
		String[] tokens = objectIdFromURL.split(":");
		java.nio.file.Path objectPath = Paths.get("");
		for (int i = 0; i < tokens.length; i++) {
			objectPath = objectPath.resolve(Paths.get(tokens[i]));
		}
		return objectPath;
	}

	/*****************************/

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

	/*******************/
	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Path("/subir")
	public Response subir(@FormDataParam("chunkData") byte[] chunkData) {
		System.out.println("---------->  subir!!!");
		System.out.println();
		try {
			Files.write(Paths.get("tmp","imagen.png"+"_"+System.currentTimeMillis()), chunkData);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return createOkResponse("ok");
	}
}
