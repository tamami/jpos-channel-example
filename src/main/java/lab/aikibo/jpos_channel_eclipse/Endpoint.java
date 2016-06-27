package lab.aikibo.jpos_channel_eclipse;

import java.util.Properties;

import org.jpos.iso.BaseChannel;
import org.jpos.iso.ISOChannel;
import org.jpos.iso.ISOPackager;

import com.kabira.io.ChannelException;
import com.kabira.io.Service;
import com.kabira.platform.KeyFieldValueList;
import com.kabira.platform.KeyManager;
import com.kabira.platform.KeyQuery;
import com.kabira.platform.LockMode;

public abstract class Endpoint extends com.kabira.io.Endpoint {

	public static final String CHANNEL_NAME = "jposchannel";
	public static final String PACKAGER_NAME_PROPERTY = "lab.aikibo.jpos_channel_eclipse.jposchannel.packager";
	public static final String ISOCHANNEL_NAME_PROPERTY = "lab.aikibo.jpos_channel_eclipse.isochannel";
	public static final String NUMBER_OF_CLIENT_CONNECTIONS_PROPERTY = "lab.aikibo.jpos_channel_eclipse.numberOfClientConnections";
	public static final String DEFAULT_PACKAGER_NAME = "org.jpos.iso.packager.XMLPackager";
	public static final String DEFAULT_ISOCHANNEL_NAME = "lab.aikibo.jpos_channel_eclipse.CloseableXMLChannel";
	public static final int DEFAULT_NUMBER_OF_CLIENT_CONNECTIONS = 1;
	private static final java.util.logging.Logger m_logger = java.util.logging.Logger.getLogger("");
	
	enum ChannelType {
		CLIENT_CHANNEL,
		SERVER_CHANNEL
	};
	
	public Endpoint(Service service, String name, String description) {
		super(service, name, description);
	}
	
	public Endpoint(String name, String description) {
		super(name, description);
	}
	
	static String getPackagerName(Properties properties) {
		String str = null;
		if((properties == null) || ((str = properties.getProperty(PACKAGER_NAME_PROPERTY)) == null)) {
			str = DEFAULT_PACKAGER_NAME;
		}
		m_logger.fine("jposchannel: using JPOS packager " + str);
		return str;
	}
	
	static ISOPackager getPackager(Properties properties) throws ChannelException {
		String str = getPackagerName(properties);
		ISOPackager packager = null;
		
		try {
			Class c = Class.forName(str);
			packager = (ISOPackager) c.newInstance();
		} catch(ClassNotFoundException cnf) {
			throw new ChannelException(CHANNEL_NAME, "Failed to resolve packager class[" + str + "].", cnf);
		} catch(InstantiationException ie) {
			throw new ChannelException(CHANNEL_NAME, "Failed to instantiate packager [" + str + "]." +
					"Make sure that the Packager has a simple constructor.", ie);
		} catch(IllegalAccessException ex) {
			throw new ChannelException(CHANNEL_NAME, "Failed to instanciate packager [" + str + "].", ex);
		}
		
		if(packager == null) {
			throw new ChannelException(CHANNEL_NAME, "Failed to create packager [" + str + "].");
		}
		return packager;
	}
	
	static String getChannelName(Properties properties) {
		String str = null;
		if((properties == null) ||
				((str = properties.getProperty(ISOCHANNEL_NAME_PROPERTY)) == null)) {
			        str = DEFAULT_ISOCHANNEL_NAME;
		}
		m_logger.fine("jposchannel: using JPOS ISOChannel " + str);
		return str;
	}
	
	static ISOChannel getChannel (ChannelType channelType, String host, int port, ISOPackager packager, Properties properties)
			throws ChannelException {
		String str = getChannelName(properties);
		ISOChannel channel = null;
		assert(packager != null);
		try {
			Class c = Class.forName(str);
			BaseChannel baseChannel = (BaseChannel) c.newInstance();
			if(channelType == ChannelType.CLIENT_CHANNEL) {
				baseChannel.setHost(host);
				baseChannel.setPort(port);
			}
			channel = (ISOChannel) baseChannel;
			channel.setPackager(packager);
		} catch(ClassNotFoundException ex) {
			throw new ChannelException(CHANNEL_NAME, "Failed to resolve channel class [" + str + "].", ex);
		} catch(InstantiationException ex) {
			throw new ChannelException(CHANNEL_NAME, "Failed to instantiate channel [" + str + "]. " +
					"Make sure that the ISOChannel has a valid constructor.", ex);
		} catch(IllegalAccessException ex) {
			throw new ChannelException(CHANNEL_NAME, "Failed to instantiate channel [" + str + "].", ex);
		}
		
		if(channel == null) {
			throw new ChannelException(CHANNEL_NAME, "Failed to create ISOChannel [" + str + "].");
		}
		return channel;
	}
	
	public static Endpoint selectEndpoint(String name, LockMode lockMode) {
		KeyQuery<Endpoint> kq;
		KeyFieldValueList kfvl;
		
		kq = new KeyManager<Endpoint>().createKeyQuery(Endpoint.class, "ByName");
		kfvl = new KeyFieldValueList();
		kfvl.add("name", name);
		kq.defineQuery(kfvl);
		return kq.getSingleResult(lockMode);
	}
	
}
