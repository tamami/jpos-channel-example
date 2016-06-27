package lab.aikibo.jpos_channel_eclipse;

import org.jpos.iso.ISOMsg;

import com.kabira.application.Message;
import com.kabira.application.ProcessingException;
import com.kabira.platform.LockMode;

import static java.lang.System.out;

public class ServerApplication extends com.kabira.application.MessageHandler {
	
	@Override
	public void accept(final Message message) throws ProcessingException {
		assert(message.source != null);
		assert(message.source.hasSession());
		String name = message.source.endpointName;
		String session = message.source.sessionName;
		System.out.println("ServerApplication.accept(): Received message" +
		        " from endpoint[" + name + "] session [" + session + "]");
		
		ISOMsg msg = (ISOMsg) message.getPayload();
		assert(msg != null);
		try {
			out.println("ServerApplication.accept(): Inbound MTI: " + msg.getMTI());
			int direction = msg.getDirection();
			out.println("ServerApplication.accept(): Inbound Direction: " +
					(direction == ISOMsg.INCOMING ? "INCOMING" : "OUTGOING"));
			out.println("ServerApplication.accept(): Inbound Message dump: \n");
			msg.dump(System.out,  "\t");
			
			if(msg.hasField(3)) {
				String field = msg.getString(3);
				if(field.equals("666")) {
					out.println("ServerApplication: throwing instrumented failure");
					throw new ProcessingException(field);
				}
			}
		} catch(org.jpos.iso.ISOException e) {
			out.println("ServerApplication: Failed to parse inbound message.");
			throw new ProcessingException("Failed to parse message", e);
		}
		
		try {
			msg.setResponseMTI();
			msg.setDirection(ISOMsg.OUTGOING);
			msg.set(39, "00");
		} catch(org.jpos.iso.ISOException e) {
			out.println("ServerApplication: Failed to set message fields.");
			throw new ProcessingException("Failed to parse message", e);
		}
		
		ServerEndpoint e = (ServerEndpoint)Endpoint.selectEndpoint(name, LockMode.READLOCK);
		
	}

}
