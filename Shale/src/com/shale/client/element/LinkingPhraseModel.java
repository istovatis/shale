package com.shale.client.element;

import com.google.gwt.user.client.rpc.IsSerializable;
import com.orange.links.client.save.LinkModel;

public class LinkingPhraseModel extends LinkModel implements IsSerializable {

	protected String id;
	protected String left;
	protected String top;
	
	public String getLeft() { return left; }
	public void setLeft(String left) { this.left = left; }
	
	public String getTop() { return top; }
	public void setTop(String top) { this.top = top; }
	
	public void setId(String id) { this.id = id; }
	public String getId() { return id; }
	
}
