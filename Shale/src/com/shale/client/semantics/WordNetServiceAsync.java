package com.shale.client.semantics;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async counterpart of <code>GreetingService</code>.
 */
public interface WordNetServiceAsync {
	void greetServer(String concepts, String rootw, String radius, AsyncCallback<String> callback)
			throws IllegalArgumentException;
}
