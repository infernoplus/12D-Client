package twelveengine.network;

import java.io.IOException;
import java.util.ArrayList;

import twelveengine.Engine;
import twelveengine.Log;
import twelveengine.Settings;
import twelveengine.actors.*;
import twelveengine.data.*;
import twelvelib.net.*;
import twelvelib.net.packets.*;
import twelveutil.FileUtil;
import twelveutil.MathUtil;

import com.esotericsoftware.kryo.*;
import com.esotericsoftware.kryonet.*;

public class NetworkCore {
	public Engine engine;
	
	public Client client;
	
	public boolean online;
	
	public ArrayList<Packet> in; //Packets recieved from the server.
	public ArrayList<Download> dls; //These handle download packets.

	public NetworkCore(Engine a) {
		engine = a;
		online = false;
	}
	
	public void step() {
		if(online && !client.isConnected())
			close();
		if(online) {
			while(in.size() > 0) {
				Packet p = in.get(0);
				try {
					int i = p.packetType();
					switch(i) {
						case 0:		packet0(p); break;
						case 1:		packet1(p); break;
						case 2:		packet2(p); break;
						case 3:		packet3(p); break;
						case 4:		packet4(p); break;
						case 5:		packet5(p); break;
						case 10: 	packet10(p); break;
						case 11:	packet11(p); break;
						case 12:	packet12(p); break;
						case 13:	packet13(p); break;
						case 20:	packet20(p); break;
						case 21:	packet21(p); break;
						case 30:	packet30(p); break;
						case 31:	packet31(p); break;
						case 32:	packet32(p); break;
						case 41:	packet41(p); break;
						default: Log.log("Unknown packet from server! Type:" + i, "Network", 2); break;
					}
				}
				catch(Exception e) {
					Log.log("Bad packet from server threw Exception!", "Network", 2);
					Log.log("Reason:" + e.getMessage(), "Network", 2);
					e.printStackTrace();
				}
				in.remove(0);
			}
			int i = 0;
			while(i < dls.size()) {
				dls.get(i).step();
				if(dls.get(i).done) {
					dls.remove(i);
					downloadsComplete();
					i--;
				}
				i++;
			}
		}
	}

	public void register() {
		Kryo k = client.getKryo();
		Registrar.registerPackets(k);
	}
	
    public boolean connect(String ip, int tcp, int udp, String pass) {
    	close();
    	
    	try {
    		in = new ArrayList<Packet>();
    		dls = new ArrayList<Download>();
    		dfe = true;
    		wtl = "";
    		ml = new String[0];
	    	client = new Client();
	    	client.start();
	    	register();
	    	
			client.connect(5000, ip, tcp, udp);
			if(!client.isConnected())
				throw new Exception("Failed to connect.");
		    
			PacketListener l = new PacketListener(this);
		    client.addListener(l);
		    
			client.sendTCP(new Packet0Login(Settings.getString("userName"), pass));
		    
		    online = true;
		    
	    	return true;
		} 
    	catch (Exception e) {
    		Log.log("Failed to connect to server @ " + ip + " : " + tcp + "-" + udp, "Network", 2);
    		Log.log("Reason: " + e.getMessage(), "Network", 2);
    		client.close();
    		return false;
		}
    	
    }
    
	public void close() {
    	if(client != null)
    		client.close();
    	in = new ArrayList<Packet>();
		dls = new ArrayList<Download>();
		dfe = true;
		wtl = "";
		ml = new String[0];
    	online = false;
    }
	
	
	private String wtl = "";
	private String ml[] = new String[0];
	private boolean dfe = false;
	private void downloadsComplete() {
		if(dfe && dls.size() == 0) {
			if(engine.loadGame(wtl, ml))
				send(new Packet4Join(true));
			else {
				send(new Packet4Join(false));
				close();
			}
			wtl = "";
			dfe = false;
		}
	}
	
	/**Send a message to everyone on the server**/
	public void message(String m) {
		send(new Packet5Message(m));
	}
	/**Whisper a message to a specific player on the server by their name**/
	public void whisper(String m, String w) {
		send(new Packet5Message(m, w));
	}
	
	//Sends a packet to the server.
	public void send(Packet p) {
		client.sendTCP(p);
	}

    //When the listener recieves a new packet it gives it to this method that then decides what to do with it.
    public void recieved(Packet p) {
		in.add(p);
	}
    
    /** Login **/
	private void packet0(Packet p) {
		
	}
    /** Load **/
	private void packet1(Packet p) {
		Packet1Load l = (Packet1Load) p;
		if(!l.loginSucsess) {
			//The server rejected our login so we close the connection.
			close();
		}

		//Check to see if we have mods, if we dont' then download them or diconnect. config.cfg > set allowdownload <boolean>
		int i = 0;
		boolean b = false;
		while(i < l.modlist.length) {
			if(!FileUtil.haveMod(l.modlist[i])) {
				b = true;
				if(Settings.getBool("allowdownload")) {
					Log.log("Requesting download of mod: " + l.modlist[i], "Network", 0);
					send(new Packet2RequestDownload(l.modlist[i]));
				}
				else {
					Log.log("Cannot connect to server! Missing mod: " + l.modlist[i] + ". Mod downloading is disabled, enable in config.cfg > set allowdownload true", "Network", 2);
					send(new Packet4Join(false));
					close();
					return;
				}
					
			}
			i++;
		}
		
		if(!b) {
			if(engine.loadGame(l.scenario, l.modlist)) {
				send(new Packet4Join(true));
				return;
			}
			else {
				send(new Packet4Join(false));
				close();
				return;
			}
		}
		else {
			wtl = l.scenario;
			ml = l.modlist;
			dfe = true;
		}
	}
    /** Request Download **/
	private void packet2(Packet p) {
		
	}
    /** Download **/
	private void packet3(Packet p) throws IOException {
		Packet3Download d = (Packet3Download) p;
		int i = 0;
		while(i < dls.size()) {
			if(dls.get(i).hash == d.hash) {
				dls.get(i).write(d);
				return;
			}
			i++;
		}
		if(d.part == 0) {
			try {
				Download dwn = new Download(this, d.file, d.hash, d.total);
				dls.add(dwn);
				dwn.write(d);
			} catch (Exception e) {
				Log.log("Failed to start download: " + d.file, "Network", 3);
				e.printStackTrace();
			}
		}
	}
    /** Join **/
	private void packet4(Packet p) {
		
	}
    /** Message **/
	private void packet5(Packet p) {
		Packet5Message m = (Packet5Message) p;
		Log.log(m.message, "Chat", 0);
	}
	
	/** Instantiate **/
	private void packet10(Packet p) {
		Packet10Instantiate i = (Packet10Instantiate) p;
		if(engine.game.getActor(i.nid) != null)
			return;
		Actor a = engine.game.createTag(i.tag, i.nid, new Vertex(i.x, i.y, i.z), new Vertex(i.a, i.b, i.c), new Quat(i.i, i.j, i.k, i.l));
		engine.game.addActor(a);
	}
	
	/** Request Actor **/
	private void packet11(Packet p) {
		
	}
	
	/** Destroy **/
	private void packet12(Packet p) {
		Packet12Destroy d = (Packet12Destroy) p;
		Actor a = engine.game.getActor(d.nid);
		if(a != null)
			a.destroy();
	}
	
	/** Kill **/
	private void packet13(Packet p) {
		Packet13Kill k = (Packet13Kill) p;
		Actor a = engine.game.getActor(k.nid);
		if(a != null)
			a.kill();
	}
	
	/** Location **/
	private void packet20(Packet p) {
		Packet20Location l = (Packet20Location) p;
		Actor a = getActor(l.nid);
		//System.out.println("1: " + (a != null)  + " 2: " + (engine.game.player.pawn != null) + " 3: " + (a.nid == engine.game.player.pawn.nid) + " 4: " + (!l.force));
		if(a != null) {
			if(engine.game.player.pawn != null)
				if(a.nid == engine.game.player.pawn.nid)
					if(!l.force)
						return;
			a.setLocation(new Vertex(l.x, l.y, l.z));
			a.setVelocity(new Vertex(l.a, l.b, l.c));
		}
	}
	
	/** Rotation **/
	private void packet21(Packet p) {
		Packet21Rotation r = (Packet21Rotation) p;
		Actor a = getActor(r.nid);
		if(a != null) {
			if(engine.game.player.pawn != null)
				if(a.nid == engine.game.player.pawn.nid)
					if(!r.force)
						return;	
			a.setRotation(new Quat(r.i, r.j, r.k, r.l));
		}
	}
	
	/** Inventory **/
	private void packet30(Packet p) {
		
	}
	
	/** Pickup **/
	private void packet31(Packet p) {
		Packet31Pickup i = (Packet31Pickup) p;
		Actor a = getActor(i.nid);
		Pawn b = null;
		if(a.getType().contains("Pawn"))
			b = (Pawn) a;
		Actor c = getActor(i.item);
		Item d = null;
		if(c.getType().contains("Item"))
			d = (Item) c;
		if(b != null && d != null)
			b.pickup(d);
	}
	
	/** Drop **/
	private void packet32(Packet p) {
		Packet32Drop i = (Packet32Drop) p;
		Actor a = getActor(i.nid);
		Pawn b = null;
		if(a.getType().contains("Pawn"))
			b = (Pawn) a;
		Actor c = getActor(i.item);
		Item d = null;
		if(c.getType().contains("Item"))
			d = (Item) c;
		if(b != null && d != null)
			b.drop(d, MathUtil.add(b.location, new Vertex(0,0,b.eye)), MathUtil.multiply(b.look, b.toss), b.rotation);
	}
	
	/** Control **/
	private void packet41(Packet p) {
		System.out.println("hello");
		Packet41Control i = (Packet41Control) p;
		Actor a = getActor(i.nid);
		Pawn b = null;
		if(a.getType().contains("Pawn"))
			b = (Pawn) a;
		if(b != null)
			engine.game.player.givePawn(b);
	}
	
	//Gets a game actor by it's NID. If it fails to find an actor matching the NID it sends and actor request to the server to get it.
	public Actor getActor(int n) {
		Actor a = engine.game.getActor(n);
		if(a != null)
			return a;
		send(new Packet11RequestActor(n));
		return null;
	}
	
}