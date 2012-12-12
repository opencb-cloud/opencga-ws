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

import org.bioinfo.gcsa.lib.users.beans.Project;
import org.bioinfo.gcsa.lib.users.persistence.AccountManagementException;

@Path("/account")
public class AccountWSServer extends GenericWSServer {
	public AccountWSServer(@Context UriInfo uriInfo, @Context HttpServletRequest httpServletRequest)
			throws IOException, AccountManagementException {
		super(uriInfo, httpServletRequest);

		logger.info("HOST: " + uriInfo.getRequestUri().getHost());
		logger.info("----------------------------------->");
	}

	@GET
	@Path("/{accountid}/create")
	public Response create(@DefaultValue("") @PathParam("accountid") String accountId,
			@DefaultValue("") @QueryParam("password") String password,
			@DefaultValue("") @QueryParam("accountname") String accountName,
			@DefaultValue("") @QueryParam("email") String email) {
		try {
			cloudSessionManager.createAccount(accountId, password, accountName, email, sessionIp);
			return createOkResponse("OK");
		} catch (AccountManagementException e) {
			logger.error(e.toString());
			return createErrorResponse("could not create the account");
		}
	}

	@GET
	@Path("/{accountid}/login")
	public Response login(@DefaultValue("") @PathParam("accountid") String accountId,
			@DefaultValue("") @QueryParam("password") String password) {
		try {
			return createOkResponse(cloudSessionManager.login(accountId, password, sessionIp));
		} catch (AccountManagementException e) {
			logger.error(e.toString());
			return createErrorResponse("could not login");
		}
	}

	@GET
	@Path("/{accountId}/info")
	public Response getInfoAccount(@DefaultValue("") @PathParam("accountId") String accountId,
			@DefaultValue("") @QueryParam("lastactivity") String lastActivity) {
		try {
			return createOkResponse(cloudSessionManager.getAccountInfo(accountId, sessionId, lastActivity));
		} catch (AccountManagementException e) {
			logger.error(e.toString());
			return createErrorResponse("could get account information");
		}
	}

	@GET
	@Path("/{accountid}/{projectname}/create")
	public Response createProject(@DefaultValue("") @PathParam("accountid") String accountid,
			@DefaultValue("") @PathParam("projectname") String projectname,
			@DefaultValue("") @QueryParam("description") String description) {
		Project project = new Project();
		project.setName(projectname);
		project.setDescripcion(description);
		try {
			cloudSessionManager.createProject(project, accountid, sessionId);
			return createOkResponse("OK");
		} catch (AccountManagementException e) {
			logger.error(e.toString());
			return createErrorResponse("could not create project");
		}
	}


	@GET
	@Path("/{accountid}/logout")
	public Response logout(@DefaultValue("") @PathParam("accountid") String accountId) {
		try {
			cloudSessionManager.logout(accountId, sessionId);
			return createOkResponse("OK");
		} catch (AccountManagementException e) {
			logger.error(e.toString());
			return createErrorResponse("could not logout");
		}
	}

	@GET
	@Path("/{accountid}/projects")
	public Response projects(@DefaultValue("") @QueryParam("accountid") String accountId) {
		try {
			return createOkResponse(cloudSessionManager.getAccountProjects(accountId, sessionId));
		} catch (AccountManagementException e) {
			logger.error(e.toString());
			return createErrorResponse("could not get projects");
		}
	}

	@GET
	@Path("/{accountid}/changepassword")
	public Response changePassword(@DefaultValue("") @PathParam("accountid") String accountId,
			@DefaultValue("") @QueryParam("password") String password,
			@DefaultValue("") @QueryParam("npassword1") String nPassword1,
			@DefaultValue("") @QueryParam("npassword2") String nPassword2) {
		try {
			cloudSessionManager.changePassword(accountId, sessionId, password, nPassword1, nPassword2);
			return createOkResponse("OK");
		} catch (AccountManagementException e) {
			logger.error(e.toString());
			return createErrorResponse("could not change password");
		}
	}

	@GET
	@Path("/{accountid}/changeemail")
	public Response changeEmail(@DefaultValue("") @PathParam("accountid") String accountId,
			@DefaultValue("") @QueryParam("nemail") String nEmail) {
		try {
			cloudSessionManager.changeEmail(accountId, sessionId, nEmail);
			return createOkResponse("OK");
		} catch (AccountManagementException e) {
			logger.error(e.toString());
			return createErrorResponse("could not change email");
		}
	}

	@GET
	@Path("/{accountid}/resetpassword")
	public Response resetPassword(@DefaultValue("") @PathParam("accountid") String accountId,
			@DefaultValue("") @QueryParam("email") String email) {
		try {
			cloudSessionManager.resetPassword(accountId, email);
			return createOkResponse("OK");
		} catch (AccountManagementException e) {
			logger.error(e.toString());
			return createErrorResponse("could not reset password");
		}
		// return createOkResponse(userManager.resetPassword(accountId, email));
	}

	// @GET
	// @Path("/pipetest/{accountId}/{password}") //Pruebas
	// public Response pipeTest(@PathParam("accountId") String
	// accountId,@PathParam("password") String password){
	// return createOkResponse(userManager.testPipe(accountId, password));
	// }

	// @GET
	// @Path("/getuserbyaccountid")
	// public Response getUserByAccountId(@QueryParam("accountid") String
	// accountId,
	// @QueryParam("sessionid") String sessionId) {
	// return createOkResponse(userManager.getUserByAccountId(accountId,
	// sessionId));
	// }
	//
	// @GET
	// @Path("/getuserbyemail")
	// public Response getUserByEmail(@QueryParam("email") String email,
	// @QueryParam("sessionid") String sessionId) {
	// return createOkResponse(userManager.getUserByEmail(email, sessionId));
	// }

	// @GET
	// @Path("/{accountId}/createproject")
	// public Response createProject(@PathParam("accountId") String accountId,
	// @QueryParam("project") Project project, @QueryParam("sessionId") String
	// sessionId){
	// return createOkResponse(userManager.createProject(project, accountId,
	// sessionId));
	// }

	// @GET
	// @Path("/createproject/{accountId}/{password}/{accountName}/{email}")
	// public Response register(@Context HttpServletRequest
	// httpServletRequest,@PathParam("accountId") String
	// accountId,@PathParam("password") String
	// password,@PathParam("accountName") String accountName,
	// @PathParam("email") String email){
	// String IPaddr = httpServletRequest.getRemoteAddr().toString();
	// String timeStamp;
	// SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
	// Calendar calendar = Calendar.getInstance();
	// Date now = calendar.getTime();
	// timeStamp = sdf.format(now);
	// Session session = new Session(IPaddr);
	//
	// try {
	// userManager.createUser(accountId,password,accountName,email,session);
	// } catch (AccountManagementException e) {
	// return createErrorResponse(e.toString());
	// }
	// return createOkResponse("OK");
	// }

}