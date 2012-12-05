package org.bioinfo.gcsa.ws;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.bioinfo.gcsa.lib.users.beans.Project;
import org.bioinfo.gcsa.lib.users.beans.Session;
import org.bioinfo.gcsa.lib.users.persistence.UserManagementException;

@Path("/account")
public class AccountWSServer extends GenericWSServer {
	public AccountWSServer(@Context UriInfo uriInfo, @Context HttpServletRequest httpServletRequest)
			throws IOException, UserManagementException {
		super(uriInfo, httpServletRequest);

		logger.info("HOST: " + uriInfo.getRequestUri().getHost());
		logger.info("----------------------------------->");
	}

	@GET
	@Path("/{accountid}/create")
	public Response register(@PathParam("accountid") String accountId, @QueryParam("password") String password,
			@QueryParam("accountname") String accountName, @QueryParam("email") String email) {

		try {
			cloudSessionManager.createUser(accountId, password, accountName, email, sessionIp);
			return createOkResponse("OK");
		} catch (UserManagementException e) {
			return createErrorResponse(e.toString());
		}
	}

	@GET
	@Path("/{accountid}/login")
	public Response login(@PathParam("accountid") String accountId, @QueryParam("password") String password) {
		Session session = new Session(sessionIp);
		String res = userManager.login(accountId, password, session);
		if (res != null && res != "") {
			return createOkResponse(res);
		} else {
			return createErrorResponse(res);
		}
	}

	// @GET
	// @Path("/pipetest/{accountId}/{password}") //Pruebas
	// public Response pipeTest(@PathParam("accountId") String
	// accountId,@PathParam("password") String password){
	// return createOkResponse(userManager.testPipe(accountId, password));
	// }

	@GET
	@Path("/{accountId}/info")
	public Response getAccount(@PathParam("accountId") String accountId, @QueryParam("lastactivity") String lastActivity) {
		return createOkResponse(userManager.getAccountBySessionId(sessionId, lastActivity));
	}

	@GET
	@Path("/{accountId}/createproject")
	public Response createProject(@PathParam("accountId") String accountId,
			@QueryParam("projectname") String projectname, @QueryParam("description") String description) {
		Project project = new Project();
		project.setName(projectname);
		project.setDescripcion(description);
		return createOkResponse(userManager.createProject(project, accountId, sessionId));
	}

	@GET
	@Path("/{accountid}/logout")
	public Response logout(@PathParam("accountid") String accountId, @QueryParam("sessionid") String sessionId) {
		return createOkResponse(userManager.logout(accountId, sessionId));
	}

	@GET
	@Path("/getuserbyaccountid")
	public Response getUserByAccountId(@QueryParam("accountid") String accountId,
			@QueryParam("sessionid") String sessionId) {
		return createOkResponse(userManager.getUserByAccountId(accountId, sessionId));
	}

	@GET
	@Path("/getuserbyemail")
	public Response getUserByEmail(@QueryParam("email") String email, @QueryParam("sessionid") String sessionId) {
		return createOkResponse(userManager.getUserByEmail(email, sessionId));
	}

	@GET
	@Path("/getallprojectsbysessionid")
	public Response getAllprojectsBySessionId(@QueryParam("accountid") String accountId,
			@QueryParam("sessionid") String sessionId) {
		return createOkResponse(userManager.getAllProjectsBySessionId(accountId, sessionId));
	}

	@GET
	@Path("/{accountid}/changepassword")
	public Response changePassword(@PathParam("accountid") String accountId, @QueryParam("password") String password,
			@QueryParam("npassword1") String nPassword1, @QueryParam("npassword2") String nPassword2) {
		return createOkResponse(userManager.changePassword(accountId, sessionId, password, nPassword1, nPassword2));
	}

	@GET
	@Path("/{accountid}/changeemail")
	public Response changeEmail(@PathParam("accountid") String accountId, @QueryParam("sessionid") String sessionId,
			@QueryParam("nemail") String nEmail) {
		return createOkResponse(userManager.changeEmail(accountId, sessionId, nEmail));
	}

	@GET
	@Path("/{accountid}/resetpassword")
	public Response resetPassword(@PathParam("accountid") String accountId, @QueryParam("email") String email) {
		return createOkResponse(userManager.resetPassword(accountId, email));
	}

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
	// } catch (UserManagementException e) {
	// return createErrorResponse(e.toString());
	// }
	// return createOkResponse("OK");
	// }

}
