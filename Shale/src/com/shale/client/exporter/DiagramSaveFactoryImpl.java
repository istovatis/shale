package com.shale.client.exporter;

import com.google.gwt.user.client.ui.Widget;
import com.orange.links.client.save.DiagramWidgetFactory;
import com.shale.client.element.Concept;
import com.shale.client.element.LinkingPhrase;

public class DiagramSaveFactoryImpl implements DiagramWidgetFactory{

    @Override
    public Widget getFunctionByType(String type, String content) {
            if(type.equals("concept")){
                    return new Concept(content);
            } else if(type.equals("linking phrase")){
                    return new LinkingPhrase(content);
            }
            return null;
    }
    
    public Widget getFunctionByType(String type, String content, String id) {
        if(type.equals("concept")){
                return new Concept(content, id);
        } else if(type.equals("linking phrase")){
                return new LinkingPhrase(content);
        }
        return null;
}

    @Override
    public Widget getDecorationByType(String type, String content) {
            if(type.equals("linking phrase")){
                    return new LinkingPhrase(content);
            }
            return null;
    }

}