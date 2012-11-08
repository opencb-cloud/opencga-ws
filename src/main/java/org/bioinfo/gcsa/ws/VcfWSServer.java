package org.bioinfo.gcsa.ws;

import java.io.IOException;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

public class VcfWSServer extends GenericWSServer {

	public VcfWSServer(@Context UriInfo uriInfo) throws IOException {
		super(uriInfo);
	}

}
