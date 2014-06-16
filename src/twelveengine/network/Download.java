package twelveengine.network;

import java.io.FileOutputStream;
import java.io.IOException;

import twelveengine.Log;
import twelvelib.net.packets.Packet3Download;
import twelveutil.FileUtil;

public class Download {
	public NetworkCore net;
	
	public String file;
	
	public int hash;
	
	public int part;
	public int total;
	
	private FileOutputStream out;
	
	public int timeOut;
	public boolean done;
	
	public Download(NetworkCore n, String f, int h, int t) throws Exception {
		net = n;
		file = f;
		hash = h;
		part = 0;
		total = t;
		timeOut = 0;
		done = false;
		out = FileUtil.createFile(file, false);
		Log.log("Download started for: " + file, "Network", 0);
	}
	
	public void step() {
		timeOut++;
		if(timeOut > 9000)
			close();
	}
	
	public void write(Packet3Download d) {	
		try {
			timeOut = 0;
			if(d.part != part)
				throw new Exception("PART MISMATCH!");
			out.write(d.data);
			net.send(new Packet3Download(d.file, d.hash, new byte[0], d.part, d.total));
			if(d.part >= d.total) {
				Log.log("Download complete for: " + file, "Network", 0);
				done = true;
				out.close();
			}
			part++;
		} catch (Exception e) {
			Log.log("Failed to write download packet: " + file + " " + part + "/" + total, "Network", 3);
			done = true;
			e.printStackTrace();
		}
	}
	
	public void close() {
		try {
			Log.log("Download timed out: " + file + " "+ part + "/" + total, "Network", 2);
			out.close();
			done = true;
		} catch (IOException e) {
			Log.log("Failed to close download: " + file + " "+ part + "/" + total, "Network", 3);
			e.printStackTrace();
		}
	}
}