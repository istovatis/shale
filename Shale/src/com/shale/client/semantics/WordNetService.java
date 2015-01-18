package com.shale.client.semantics;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client-side stub for the RPC service.
 */
@RemoteServiceRelativePath("greet")
public interface WordNetService extends RemoteService {
	String greetServer(String concepts, String rootw, String radius) throws IllegalArgumentException;
}
