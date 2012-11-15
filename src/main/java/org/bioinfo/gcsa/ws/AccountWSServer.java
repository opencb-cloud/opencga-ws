package org.bioinfo.gcsa.ws;

import java.io.IOException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.bioinfo.gcsa.lib.users.CloudSessionManager;
import org.bioinfo.gcsa.lib.users.persistence.UserManagementException;
import org.bioinfo.gcsa.lib.users.persistence.UserManager;



@Path("/account")
public class AccountWSServer extends GenericWSServer  {
	private UserManager userManager;
	private CloudSessionManager cloudSessionManager = null;
	public AccountWSServer(@Context UriInfo uriInfo) throws IOException, UserManagementException {
		super(uriInfo);
		System.err.println("----------------------------------->");
		cloudSessionManager = new CloudSessionManager("GCSA_HOME");
		userManager = CloudSessionManager.userManager;
	}
	
	@GET
	@Path("/register/{accountId}/{password}/{accountName}/{email}")
	public Response register(@PathParam("accountId") String accountId,@PathParam("password") String password,@PathParam("accountName") String accountName, @PathParam("email") String email){
		try {
			userManager.createUser(accountId,password,accountName,email);
		} catch (UserManagementException e) {
			return createErrorResponse(e.toString());
		}
		return createOkResponse("OK");
	}
	
	
}
