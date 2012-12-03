package org.bioinfo.gcsa.ws;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.bioinfo.commons.utils.StringUtils;
import org.bioinfo.gcsa.lib.users.CloudSessionManager;
import org.bioinfo.gcsa.lib.users.beans.Project;
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
	@Path("/{accountId}/create")
	public Response register(@PathParam("accountId") String accountId,@QueryParam("password") String password,@QueryParam("accountName") String accountName, @QueryParam("email") String email){

		Session session = new Session(sessionIp);
		
		try {
			userManager.createUser(accountId,password,accountName,email,session);
		} catch (UserManagementException e) {
			return createErrorResponse(e.toString());
		}
		return createOkResponse("OK");
	}

	@GET
	@Path("/{accountId}/login")
	public Response login(@PathParam("accountId") String accountId,@QueryParam("password") String password){
		Session session = new Session(sessionIp);
		String res = userManager.login(accountId, password, session);
		if(res!=null && res!=""){
			return createOkResponse(res);
		}else{
			return createErrorResponse(res);
		}
	}
	
//	@GET
//	@Path("/pipetest/{accountId}/{password}") //Pruebas 
//	public Response pipeTest(@PathParam("accountId") String accountId,@PathParam("password") String password){
//		return createOkResponse(userManager.testPipe(accountId, password));
//	}

	@GET
	@Path("/{accountId}/logout") 
	public Response logout(@PathParam("accountId") String accountId){
		return createOkResponse(userManager.logout(accountId, sessionId));
	}
	
	@GET
	@Path("/{accountId}/info")
	public Response getAccount(@PathParam("accountId") String accountId, @QueryParam("lastactivity") String lastActivity){
		return createOkResponse(userManager.getAccountBySessionId(sessionId, lastActivity));
	}
	@GET
	@Path("/{accountId}/createproject")
	public Response createProject(@PathParam("accountId") String accountId, @QueryParam("projectname") String projectname, @QueryParam("description") String description){
		Project project = new Project();
		project.setName(projectname);
		project.setDescripcion(description);
		return createOkResponse(userManager.createProject(project,accountId,sessionId));
	}
	
	
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
