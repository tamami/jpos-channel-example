package lab.aikibo.jpos_channel_eclipse;

import java.util.HashMap;
import java.util.Map;

import org.jpos.iso.ISOChannel;
import org.jpos.iso.ISOPackager;

import com.kabira.platform.DeleteTrigger;

public class ServerService extends com.kabira.io.Service implements DeleteTrigger {
	
	private int m_port;
	private String m_packagerName;
	private String m_channelName;
	private ServerEndpoint m_endpoint;
	private static final java.util.logging.Logger m_logger = java.util.logging.Logger.getLogger("");
	
	public ServerService(String name, String description, int port, Properties properties) {
		super(name, description);
		m_port = port;
		ListenerReference.m_default = this;
		m_packagerName = Endpoint.getPackagerName(properties);
		m_channelName = Endpoint.getChannelName(properties);
	}
	
	
	// ---- inner class
	private static class ListenerReference {
		static ServerService m_default;
		static Map<String, Listener> m_map = new HashMap<String, Listener>();
		static Map<String, ISOPackager> m_packagers = new HashMap<String, ISOPackager>();
		static Map<String, ISOChannel> m_channels = new HashMap<String, ISOChannel>();
	}

}
