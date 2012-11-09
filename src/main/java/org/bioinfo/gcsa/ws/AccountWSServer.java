package org.bioinfo.gcsa.ws;

import java.io.IOException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.bioinfo.gcsa.lib.users.persistence.UserManagementException;
import org.bioinfo.gcsa.lib.users.persistence.UserMongoDBManager;



@Path("/account")
public class AccountWSServer extends GenericWSServer  {
	private UserMongoDBManager userManager= new UserMongoDBManager();
	
	public AccountWSServer(@Context UriInfo uriInfo) throws IOException {
		super(uriInfo);
	}
	
	@GET
	@Path("/register/{accountId}/{password}/{accountName}/{email}")
	public Response register(@PathParam("accountId") String accountId,@PathParam("password") String password,@PathParam("accountName") String accountName, @PathParam("email") String email){
		try {
			userManager.createAccountId(accountId,password,accountName,email);
		} catch (UserManagementException e) {
			e.printStackTrace();
		}
		return createOkResponse("OK");
	}
	
	
}
