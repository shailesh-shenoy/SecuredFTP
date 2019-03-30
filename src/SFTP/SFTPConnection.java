package SFTP;

import java.io.ByteArrayInputStream;
import java.io.PrintWriter;
import java.io.StringWriter;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

public class SFTPConnection
{
	String host;
	int port;
	String hostKey;
	String user;
	String password;
	JSch sftpClient;
	Session session;
	
	SFTPReturnValue createConnection(String host, int port, String hostKey, String user, String password)
	{
		SFTPReturnValue sftpReturnValue = null;
		try
		{
			this.host = host;
			this.port = port;
			this.hostKey = hostKey;
			this.user = user;
			this.password = password;
			this.sftpClient = new JSch();
			sftpClient.setKnownHosts(new ByteArrayInputStream(this.hostKey.getBytes()));
			this.session = sftpClient.getSession(this.user, this.host, this.port);
			this.session.setPassword(this.password);
			this.session.setConfig("StrictHostKeyChecking", "yes");
			this.session.connect();
			sftpReturnValue = new SFTPReturnValue(true, "Connection established successfully", "");
		} catch (Exception e)
		{
			// get stackTrace as string
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			String stackTrace = sw.toString();
			pw.close();
			
			sftpReturnValue = new SFTPReturnValue(false, "Could not establish connection: " + e.getLocalizedMessage(),
					stackTrace);
		} finally
		{
			if (this.session != null)
				System.out.println("session connected? : " + this.session.isConnected());
		}
		
		return sftpReturnValue;
	}
	
	SFTPReturnValue closeConnection()
	{
		SFTPReturnValue sftpReturnValue = null;
		if (this.session != null)
		{
			if (this.session.isConnected())
			{
				this.session.disconnect();
				sftpReturnValue = new SFTPReturnValue(true, "Connection closed successfully", "");
			} else
			{
				sftpReturnValue = new SFTPReturnValue(false, "Connection already disconnected/not established", "");
			}
		} else
		{
			sftpReturnValue = new SFTPReturnValue(false, "Connection does not exist/not initialized", "");
		}
		return sftpReturnValue;
	}
	
	public static void main(String args[])
	{
		System.out.println("Test SFTP");
		System.out.println();
		SFTPConnection sftpConnection = new SFTPConnection();
		SFTPReturnValue sftpReturnValue = sftpConnection.createConnection("192.168.0.104", 22,
				"192.168.0.104 ssh-rsa AAAAB3NzaC1yc2EAAAABIwAAAIEA53ay302T9H2S4sF3tg25zISIUnxQh/Pv0xqCakHENIRH8A6Nw4P3A62wt6kVpBGhXJjh7w5P5ZUZ872eicianiaJnwKiA/THxtZSxE5dOh2hRVpCLpWerne3izOL9+wN3obfMj0C+rEoglIK3aLiYm6EYBRQ2zgVoidOt2cJ91U=",
				"test", "Test@123");
		System.out.println(sftpReturnValue.toString());
		if (sftpConnection.session != null)
		{
			System.out.println("session connected? : " + sftpConnection.session.isConnected());
		}
		sftpReturnValue = sftpConnection.closeConnection();
		if (sftpConnection.session != null)
		{
			System.out.println("session connected? : " + sftpConnection.session.isConnected());
		}
		System.out.println(sftpReturnValue);
	}
	
}
