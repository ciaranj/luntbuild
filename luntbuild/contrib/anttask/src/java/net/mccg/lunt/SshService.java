package net.mccg.lunt;

import java.io.InputStream;

public interface SshService {
	void sftp(InputStream content, String remoteDirectory, String remoteName) throws Exception;
}
