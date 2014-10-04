package org.csdgn.rf.db;

import java.io.FileDescriptor;
import java.net.InetAddress;
import java.security.Permission;

import org.csdgn.rf.Engine;

public class SandboxSecurityManager extends SecurityManager {
	private void throwSandboxException() {
		throw new SecurityException("Illegal sandbox action.");
	}
	
	private final Engine engine;
	
	public SandboxSecurityManager(Engine engine) {
		this.engine = engine;
	}
	
	public boolean isSafe() {
		Thread t = Thread.currentThread();
		if(t.getName().contains("BattleThread")) {
			if(!engine.getCurrentBattle().isSafe()) {
				return false;
			}
		}
		return true;
	}

	@Override
	public void checkPermission(Permission p) {
		if(isSafe()) {
			return;
		}
		throwSandboxException();
	}

	@Override
	public void checkPermission(Permission p, Object o) {
		if(isSafe()) {
			return;
		}
		throwSandboxException();
	}

	@Override
	public void checkCreateClassLoader() {
		if(isSafe()) {
			return;
		}
		throwSandboxException();
	}

	@Override
	public void checkAccess(Thread t) {
		if(isSafe()) {
			return;
		}
		throwSandboxException();
	}

	@Override
	public void checkAccess(ThreadGroup tg) {
		if(isSafe()) {
			return;
		}
		throwSandboxException();
	}

	@Override
	public void checkExit(int i) {
		if(isSafe()) {
			return;
		}
		throwSandboxException();
	}

	@Override
	public void checkExec(String s) {
		if(isSafe()) {
			return;
		}
		throwSandboxException();
	}

	@Override
	public void checkLink(String s) {
		if(isSafe()) {
			return;
		}
		throwSandboxException();
	}

	@Override
	public void checkRead(FileDescriptor fd) {
		if(isSafe()) {
			return;
		}
		throwSandboxException();
	}

	@Override
	public void checkRead(String s) {
		if(isSafe()) {
			return;
		}
		throwSandboxException();
	}

	@Override
	public void checkRead(String s, Object o) {
		if(isSafe()) {
			return;
		}
		throwSandboxException();
	}

	@Override
	public void checkWrite(FileDescriptor fd) {
		if(isSafe()) {
			return;
		}
		throwSandboxException();
	}

	@Override
	public void checkWrite(String s) {
		if(isSafe()) {
			return;
		}
		throwSandboxException();
	}

	@Override
	public void checkDelete(String s) {
		if(isSafe()) {
			return;
		}
		throwSandboxException();
	}

	@Override
	public void checkConnect(String s, int p) {
		if(isSafe()) {
			return;
		}
		throwSandboxException();
	}

	@Override
	public void checkConnect(String s, int p, Object o) {
		if(isSafe()) {
			return;
		}
		throwSandboxException();
	}

	@Override
	public void checkListen(int i) {
		if(isSafe()) {
			return;
		}
		throwSandboxException();
	}

	@Override
	public void checkAccept(String s, int i) {
		if(isSafe()) {
			return;
		}
		throwSandboxException();
	}

	@Override
	public void checkMulticast(InetAddress a) {
		if(isSafe()) {
			return;
		}
		throwSandboxException();
	}

	@Override
	public void checkPropertiesAccess() {
		if(isSafe()) {
			return;
		}
		throwSandboxException();
	}

	@Override
	public void checkPropertyAccess(String s) {
		if(isSafe()) {
			return;
		}
		throwSandboxException();
	}

	@Override
	public boolean checkTopLevelWindow(Object o) {
		if(isSafe()) {
			return true;
		}
		return false;
	}

	@Override
	public void checkPrintJobAccess() {
		if(isSafe()) {
			return;
		}
		throwSandboxException();
	}

	@Override
	public void checkSystemClipboardAccess() {
		if(isSafe()) {
			return;
		}
		throwSandboxException();
	}

	@Override
	public void checkAwtEventQueueAccess() {
		if(isSafe()) {
			return;
		}
		throwSandboxException();
	}

	@Override
	public void checkPackageAccess(String pkg) {
		if(isSafe()) {
			return;
		}
		if (pkg.equals("java.lang")) {
			return;
		}
		super.checkPackageAccess(pkg);
		
		if(pkg.startsWith("org.csdgn.rf")
		|| pkg.startsWith("javax")
		|| pkg.startsWith("java.applet")
		|| pkg.startsWith("java.awt")
		|| pkg.startsWith("java.beans")
		|| pkg.startsWith("java.nio")
		|| pkg.startsWith("java.net")
		|| pkg.startsWith("java.rmi")
		|| pkg.startsWith("java.security")
		|| pkg.startsWith("java.sql")
		|| pkg.startsWith("java.text")
		|| pkg.startsWith("java.util.concurrent")
		|| pkg.startsWith("java.util.jar")
		|| pkg.startsWith("java.util.zip")
		|| pkg.startsWith("java.util.spi")
		|| pkg.startsWith("org.lwjgl")) {
			throwSandboxException();
		}
	}

	@Override
	public void checkPackageDefinition(String pkg) {
		if(isSafe()) {
			return;
		}
		if (pkg.startsWith("java.") || pkg.startsWith("javax.") ||pkg.startsWith("org.omg.")
				|| pkg.startsWith("org.w3c.") || pkg.startsWith("org.xml.")
				|| pkg.startsWith("org.csdgn.rf") || pkg.startsWith("roboflight")) {
			throwSandboxException();
		}
	}

	@Override
	public void checkSetFactory() {
		if(isSafe()) {
			return;
		}
		throwSandboxException();
	}

	@Override
	public void checkMemberAccess(Class<?> cls, int i) {
		if(isSafe()) {
			return;
		}
		throwSandboxException();
	}

	@Override
	public void checkSecurityAccess(String s) {
		if(isSafe()) {
			return;
		}
		throwSandboxException();
	}
}
