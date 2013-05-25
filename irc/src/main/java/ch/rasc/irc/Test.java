package ch.rasc.irc;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.schwering.irc.lib.IRCConnection;
import org.schwering.irc.lib.IRCEventListener;
import org.schwering.irc.lib.IRCModeParser;
import org.schwering.irc.lib.IRCUser;

public class Test {
	public static void main(String[] args) throws IOException {
		IRCConnection conn = new IRCConnection("irc.wikimedia.org", 6667, 6669, null, "rcmon", "rcmon", "rcmon");

		conn.addIRCEventListener(new IRCEventListener() {
			public void onRegistered() {
				System.out.println("Connected");
			}

			public void onDisconnected() {
				System.out.println("Disconnected");
			}

			public void onError(String msg) {
				System.out.println("Error: " + msg);
			}

			public void onError(int num, String msg) {
				System.out.println("Error #" + num + ": " + msg);
			}

			public void onInvite(String chan, IRCUser u, String nickPass) {
				System.out.println(chan + "> " + u.getNick() + " invites " + nickPass);
			}

			public void onJoin(String chan, IRCUser u) {
				System.out.println(chan + "> " + u.getNick() + " joins");
			}

			public void onKick(String chan, IRCUser u, String nickPass, String msg) {
				System.out.println(chan + "> " + u.getNick() + " kicks " + nickPass);
			}

			public void onMode(IRCUser u, String nickPass, String mode) {
				System.out.println("Mode: " + u.getNick() + " sets modes " + mode + " " + nickPass);
			}

			public void onMode(String chan, IRCUser u, IRCModeParser mp) {
				System.out.println(chan + "> " + u.getNick() + " sets mode: " + mp.getLine());
			}

			public void onNick(IRCUser u, String nickNew) {
				System.out.println("Nick: " + u.getNick() + " is now known as " + nickNew);
			}

			public void onNotice(String target, IRCUser u, String msg) {
				System.out.println(target + "> " + u.getNick() + " (notice): " + msg);
			}

			public void onPart(String chan, IRCUser u, String msg) {
				System.out.println(chan + "> " + u.getNick() + " parts");
			}

			public void onPrivmsg(String chan, IRCUser u, String msg) {
				System.out.println(chan + "> " + u.getNick() + ": " + msg);
			}

			public void onQuit(IRCUser u, String msg) {
				System.out.println("Quit: " + u.getNick());
			}

			public void onReply(int num, String value, String msg) {
				System.out.println("Reply #" + num + ": " + value + " " + msg);
			}

			public void onTopic(String chan, IRCUser u, String topic) {
				System.out.println(chan + "> " + u.getNick() + " changes topic into: " + topic);
			}

			public void onPing(String p) {
				System.out.println("onPing: " + p);
			}

			public void unknown(String a, String b, String c, String d) {
				System.out.println("UNKNOWN: " + a + " b " + c + " " + d);
			}
		});

		conn.setEncoding("UTF-8");
		conn.setPong(true);
		conn.setDaemon(false);
		conn.setColors(false);
		conn.connect();
		// conn.doList();
		conn.doJoin("#en.wikipedia");
		// conn.doNames();
		try {
			TimeUnit.MINUTES.sleep(5);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		conn.doQuit();

	}
}
