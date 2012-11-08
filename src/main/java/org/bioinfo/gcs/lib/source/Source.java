package org.bioinfo.gcs.lib.source;

import java.io.InputStream;

public interface Source {
	public InputStream getInputStream(String path);
}
