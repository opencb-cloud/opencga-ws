package org.bioinfo.gcsa.ws;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.bioinfo.gcsa.lib.account.beans.Bucket;
import org.bioinfo.gcsa.lib.account.beans.Project;
import org.bioinfo.gcsa.lib.account.db.AccountManagementException;
import org.bioinfo.gcsa.lib.account.io.IOManagementException;

@Path("/account/{accountId}/manage")
public class AdminWSServer extends GenericWSServer {
	private String accountId;

	public AdminWSServer(@Context UriInfo uriInfo, @Context HttpServletRequest httpServletRequest,
			@DefaultValue("") @PathParam("accountId") String accountId) throws IOException, AccountManagementException {
		super(uriInfo, httpServletRequest);
		this.accountId = accountId;
	}

	/********************
	 * 
	 * BUCKET WEB SERVICES
	 * 
	 ********************/
	@GET
	@Path("/buckets/list")
	public Response getBucketsList() {
		try {
			String res = cloudSessionManager.getBucketsList(accountId, sessionId);
			return createOkResponse(res);
		} catch (AccountManagementException e) {
			logger.error(e.toString());
			return createErrorResponse("could not get buckets");
		}
	}

	@GET
	@Path("/buckets/{bucketId}/create")
	public Response createBucket(@DefaultValue("") @PathParam("bucketId") String bucketId,
			@DefaultValue("") @QueryParam("description") String description) {
		Bucket bucket = new Bucket(bucketId);
		bucket.setId(bucketId.toLowerCase());
		bucket.setDescripcion(description);
		try {
			cloudSessionManager.createBucket(accountId, bucket, sessionId);
			return createOkResponse("OK");
		} catch (AccountManagementException | IOManagementException e) {
			logger.error(e.toString());
			return createErrorResponse("could not create bucket");
		}
	}

	// TODO
	// @GET
	// @Path("/{bucketname}/rename/{newName}")
	// public Response renameBucket(@DefaultValue("") @PathParam("bucket_name")
	// String bucketId,
	// @DefaultValue("") @PathParam("newName") String newName) {
	// try {
	// cloudSessionManager.renameBucket(accountId, bucketId, newName,
	// sessionId);
	// return createOkResponse("OK");
	// } catch (AccountManagementException | IOManagementException e) {
	// logger.error(e.toString());
	// return createErrorResponse("could not rename bucket");
	// }
	// }

	// TODO
	// @GET
	// @Path("/{bucketname}/delete")
	// public Response deleteBucket(@DefaultValue("") @PathParam("bucketname")
	// String bucketId) {
	// try {
	// cloudSessionManager.deleteBucket(accountId, bucketId, sessionId);
	// return createOkResponse("OK");
	// } catch (AccountManagementException | IOManagementException e) {
	// logger.error(e.toString());
	// return createErrorResponse("could not delete the bucket");
	// }
	// }

	// TODO
	// @GET
	// @Path("/{bucketname}/share/{accountList}")
	// public Response shareBucket(@DefaultValue("") @PathParam("bucketname")
	// String bucketId,
	// @DefaultValue("") @PathParam("accountList") String accountList) {
	// try {
	// cloudSessionManager.shareBucket(accountId, bucketId,
	// StringUtils.toList(accountList, ","), sessionId);
	// return createOkResponse("OK");
	// } catch (AccountManagementException | IOManagementException e) {
	// logger.error(e.toString());
	// return createErrorResponse("could not share the bucket");
	// }
	// }

	/********************
	 * 
	 * PROJECT WEB SERVICES
	 * 
	 ********************/
	@GET
	@Path("/projects/list")
	public Response getProjectsList() {
		try {
			String res = cloudSessionManager.getProjectsList(accountId, sessionId);
			return createOkResponse(res);
		} catch (AccountManagementException e) {
			logger.error(e.toString());
			return createErrorResponse("could not get projects list");
		}
	}

	@GET
	@Path("/projects/{projectId}/create")
	public Response createProject(@DefaultValue("") @PathParam("projectId") String projectId,
			@DefaultValue("") @QueryParam("description") String description) {
		Project project = new Project();
		project.setName(projectId);
		project.setId(projectId.toLowerCase());
		try {
			cloudSessionManager.createProject(accountId, project, sessionId);
			return createOkResponse("OK");
		} catch (AccountManagementException | IOManagementException e) {
			logger.error(e.toString());
			return createErrorResponse("could not create project");
		}
	}
}