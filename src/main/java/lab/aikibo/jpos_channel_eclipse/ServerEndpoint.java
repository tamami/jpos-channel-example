package lab.aikibo.jpos_channel_eclipse;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.jpos.iso.BaseChannel;
import org.jpos.iso.ISOSource;

import com.kabira.application.Message;
import com.kabira.io.ChannelException;
import com.kabira.platform.LockMode;
import com.kabira.platform.Transaction;

public class ServerEndpoint extends Endpoint {
	
	private static final java.util.logging.Logger m_logger = java.util.logging.Logger.getLogger("");
	
	public ServerEndpoint(ServerService service, String name, String description) {
		super(service, name, description);
	}
	
	public ServerEndpoint(String name, String description) {
		super(name, description);
	}
	
	@Override
	protected void onStart() {
		
	}
	
	@Override
	protected void onStop() throws ChannelException {
		SourceSessionMap.clear(this);
	}
	
	
	// --- inner class
	private static class SourceSessionMap {
		private static Map<String, ISOSource> m_map = new ConcurrentHashMap<String, ISOSource>();
		
		private static String put(final String endpointName, ISOSource source) {
			final BaseChannel base = (BaseChannel) source;
			assert(base != null);
			
			final String sourceName = base.getName();
			assert(sourceName != null);
			ConcurrentHashMap myMap = (ConcurrentHashMap) m_map;
			ISOSource src = (ISOSource) myMap.put(sourceName, source);
			assert(src == null);
			
			new Transaction() {
				@Override 
				public void run() throws Rollback {
					try {
						ServerEndpoint endpoint = (ServerEndpoint) selectEndpoint(
								endpointName, LockMode.READLOCK);
						endpoint.addSession(sourceName, base.getRealm());
					} catch(ChannelException ex) {
						m_logger.warning("Failed adding session [" + sourceName + "] to endpoint [" + endpointName + "] : " +
								exceptionString(ex));
					}
				}
			}.execute();
			return sourceName;
		}
		
		private static ISOSource get(String sessionName) {
			return m_map.get(sessionName);
		}
		
		private static void remove(String sessionName) {
			m_map.remove(sessionName);
		}
		
		private static void clear(final ServerEndpoint endpoint) {
			try {
				ConcurrentHashMap myMap = (ConcurrentHashMap) m_map;
				java.util.Enumeration<String> names = myMap.keys();
				while(names.hasMoreElements()) {
					String sessionName = (String) names.nextElement();
					endpoint.stopSession(sessionName);
				}
			} catch(ChannelException ex) {}
			
			m_map.clear();
		}
	}


	@Override
	public boolean isActive() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected void onSend(Message arg0) throws ChannelException {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onStopSession(String arg0) throws ChannelException {
		// TODO Auto-generated method stub
		
	}

}
