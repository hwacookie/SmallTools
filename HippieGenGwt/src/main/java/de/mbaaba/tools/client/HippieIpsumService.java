package de.mbaaba.tools.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import de.mbaaba.tools.shared.Style;

/**
 * The client side stub for the RPC service.
 * Note to self: the @RemoteServiceRelativePath value must be the same as the filename-part in the url-pattern in the web.xml!
 */
@RemoteServiceRelativePath("hippieIpsum")
public interface HippieIpsumService extends RemoteService {
	String[] getStyleNames() throws IllegalArgumentException;
	Style getStyle(String aName) throws IllegalArgumentException;
	void saveStyle(Style aStyle) throws IllegalArgumentException;
	void newStyle(Style aStyle) throws IllegalArgumentException;

}
