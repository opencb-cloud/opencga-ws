package org.bioinfo.gcsa.ws;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
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
	private CloudSessionManager cloudSessionManager = null;
	public AccountWSServer(@Context UriInfo uriInfo,@Context HttpServletRequest httpServletRequest) throws IOException, UserManagementException {
		super(uriInfo,httpServletRequest); 
		
		System.out.println("HOST: "+uriInfo.getRequestUri().getHost());
		System.err.println("----------------------------------->");
		cloudSessionManager = new CloudSessionManager("GCSA_HOME");
		userManager = CloudSessionManager.userManager;
	}
	
	@GET
	@Path("/register/{accountId}/{password}/{accountName}/{email}")
	public Response register(@PathParam("accountId") String accountId,@PathParam("password") String password,@PathParam("accountName") String accountName, @PathParam("email") String email){

		Session session = new Session(sessionIp);
		
		try {
			userManager.createUser(accountId,password,accountName,email,session);
		} catch (UserManagementException e) {
			return createErrorResponse(e.toString());
		}
		return createOkResponse("OK");
	}

	@GET
	@Path("/login/{accountId}/{password}")
	public Response login(@PathParam("accountId") String accountId,@PathParam("password") String password){
		
		//Session session = new Session(sessionIp);
		System.out.println("ESTAMOS AQUI A VER SI ENTRA");
		return createOkResponse(userManager.login(accountId, password));
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
