package twelveengine.network;

import twelvelib.net.packets.Packet;
import com.esotericsoftware.kryonet.*;

public class PacketListener extends Listener {
	public NetworkCore network;
	public PacketListener(NetworkCore n) {
		super();
		network = n;
	}
	
	public void received (Connection connection, Object object) {
		if(object instanceof Packet) {
			Packet p = (Packet) object;
			p.hash = connection.hashCode();
			network.recieved(p);
		}
	}
}