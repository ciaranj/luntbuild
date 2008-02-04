package net.mccg.lunt;

import java.io.InputStream;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UIKeyboardInteractive;
import com.jcraft.jsch.UserInfo;

public class SshServiceImpl implements SshService{
	private ChannelSftp sftpChannel;
	private Session session;
	private static String password;

	public SshServiceImpl(final String host, final String username, final String password) throws Exception {
		SshServiceImpl.password = password;
		JSch jsch = new JSch();
		session = jsch.getSession(username, host, 22);
		UserInfo ui = new MyUserInfo();
		session.setUserInfo(ui);
		try {
			session.connect();
		} catch (JSchException e) {
			System.out.println("Error connecting to host:" + username + "@" + host);
			throw e;
		}
	}

	public void sftp(final InputStream content, final String remoteDirectory, final String remoteName) throws Exception {
		Channel channel = session.openChannel("sftp");
		channel.connect();
		sftpChannel = (ChannelSftp) channel;
		sftpChannel.cd(remoteDirectory);
		sftpChannel.put(content, remoteName, ChannelSftp.OVERWRITE);
		sftpChannel.exit();		
	}

	public static class MyUserInfo implements UserInfo, UIKeyboardInteractive{
		public String getPassword(){ return password; }
		public boolean promptYesNo(String str) { return true; }
		public String getPassphrase() { return null; }
		public boolean promptPassphrase(String message) { return true; } // leave true
		public boolean promptPassword(String message){ return true; } // leave true
		public void showMessage(String message) {}
		public String[] promptKeyboardInteractive(String destination, String name,
        String instruction, String[] prompt,
		boolean[] echo) { return new String[] { password }; }
	} 
}
