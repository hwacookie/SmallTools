package de.mbaaba.tools.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import de.mbaaba.tools.shared.Style;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("greet")
public interface GreetingService extends RemoteService {
	String[] getStyleNames() throws IllegalArgumentException;

	Style getStyle(String aName) throws IllegalArgumentException;

	void saveStyle(Style aStyle) throws IllegalArgumentException;

}
