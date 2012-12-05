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

import org.bioinfo.gcsa.lib.users.CloudSessionManager;
import org.bioinfo.gcsa.lib.users.beans.Session;
import org.bioinfo.gcsa.lib.users.persistence.UserManagementException;
import org.bioinfo.gcsa.lib.users.persistence.UserManager;



@Path("/account")
public class AccountWSServer extends GenericWSServer  {
	private UserManager userManager;
//	private CloudSessionManager cloudSessionManager = null;
	public AccountWSServer(@Context UriInfo uriInfo,@Context HttpServletRequest httpServletRequest) throws IOException, UserManagementException {
		super(uriInfo,httpServletRequest); 
		
		System.out.println("HOST: "+uriInfo.getRequestUri().getHost());
		System.err.println("----------------------------------->");
		CloudSessionManager cloudSessionManager = new CloudSessionManager(System.getenv("GCSA_HOME"));
		userManager = cloudSessionManager.getUserManager();
	}
	
	@GET
	@Path("/{accountid}/create")
	public Response register(@PathParam("accountid") String accountId,@QueryParam("password") String password,@QueryParam("accountname") String accountName, @QueryParam("email") String email){

		Session session = new Session(sessionIp);
		
		try {
			userManager.createUser(accountId,password,accountName,email,session);
		} catch (UserManagementException e) {
			return createErrorResponse(e.toString());
		}
		return createOkResponse("OK");
	}

	@GET
	@Path("/{accountid}/login")
	public Response login(@PathParam("accountid") String accountId,@QueryParam("password") String password){
		Session session = new Session(sessionIp);
		return createOkResponse(userManager.login(accountId, password, session));
	}
	
//	@GET
//	@Path("/pipetest/{accountId}/{password}") //Pruebas 
//	public Response pipeTest(@PathParam("accountId") String accountId,@PathParam("password") String password){
//		return createOkResponse(userManager.testPipe(accountId, password));
//	}

	@GET
	@Path("/{accountid}/logout") 
	public Response logout(@PathParam("accountid") String accountId){
		return createOkResponse(userManager.logout(accountId, sessionId));
	}
	
	@GET
	@Path("/getuserbyaccountid") 
	public Response getUserByAccountId(@QueryParam("accountid") String accountId){
		return createOkResponse(userManager.getUserByAccountId(accountId, sessionId));
	}
	
	@GET
	@Path("/getuserbyemail") 
	public Response getUserByEmail(@QueryParam("email") String email){
		return createOkResponse(userManager.getUserByEmail(email, sessionId));
	}
	
	@GET
	@Path("/getallprojectsbysessionid") 
	public Response getAllprojectsBySessionId(@QueryParam("accountid") String accountId){
		return createOkResponse(userManager.getAllProjectsBySessionId(accountId, sessionId));
	}
	
	@GET
	@Path("/{accountid}/changepassword") 
	public Response changePassword(@PathParam("accountid") String accountId, @QueryParam("password") String password,@QueryParam("npassword1") String nPassword1,@QueryParam("npassword2") String nPassword2){
		return createOkResponse(userManager.changePassword(accountId, password, nPassword1,nPassword2));
	}
	
	@GET
	@Path("/{accountid}/changeemail") 
	public Response changeEmail(@PathParam("accountid") String accountId,@QueryParam("nemail") String nEmail){
		return createOkResponse(userManager.changeEmail(accountId, sessionId, nEmail));
	}
	
	
//	@GET
//	@Path("/{accountId}/createproject") 
//	public Response createProject(@PathParam("accountId") String accountId, @QueryParam("project") Project project, @QueryParam("sessionId") String sessionId){
//		return createOkResponse(userManager.createProject(project, accountId, sessionId));
//	}
	
//	@GET
//	@Path("/createproject/{accountId}/{password}/{accountName}/{email}")
//	public Response register(@Context HttpServletRequest httpServletRequest,@PathParam("accountId") String accountId,@PathParam("password") String password,@PathParam("accountName") String accountName, @PathParam("email") String email){
//		String IPaddr = httpServletRequest.getRemoteAddr().toString();
//		String timeStamp;
//		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
//		Calendar calendar = Calendar.getInstance();
//		Date now = calendar.getTime();
//		timeStamp = sdf.format(now);
//		Session session = new Session(IPaddr);
//		
//		try {
//			userManager.createUser(accountId,password,accountName,email,session);
//		} catch (UserManagementException e) {
//			return createErrorResponse(e.toString());
//		}
//		return createOkResponse("OK");
//	}

	
}
