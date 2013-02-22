package org.bioinfo.opencga.ws;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

@Path("/geolocation")
public class GeocodingAddressService extends GenericWSServer {

	public GeocodingAddressService(@Context UriInfo uriInfo, @Context HttpServletRequest httpServletRequest)
			throws IOException {
		super(uriInfo, httpServletRequest);
	}

	@GET
	@Path("/test")
	public String getAddress(String latLong) {
		String returned = null;
		try {
			URL mapsUrl = new URL("http://maps.googleapis.com/maps/api/geocode/xml?latlng=" + latLong + "&sensor=true");
			InputStream openStream = mapsUrl.openStream();
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(openStream);
			NodeList formattedAddress = doc.getElementsByTagName("formatted_address");
			Element formattedAddressElement = (Element) formattedAddress.item(0);
			returned = formattedAddressElement.getTextContent();
		} catch (Exception e) {
			returned = null;
		}
		return returned;
	}
}
