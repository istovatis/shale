package com.shale.client.app;

import com.google.gwt.place.shared.PlaceHistoryMapper;
import com.google.gwt.place.shared.WithTokenizers;
import com.shale.client.conceptmap.MainPlace;
import com.shale.client.menu.MenuPlace;
import com.shale.client.user.insert.InsertPlace;

/*
 * Determines the tokens that connect Place to History
 */

@WithTokenizers ({MainPlace.Tokenizer.class, MenuPlace.Tokenizer.class, InsertPlace.Tokenizer.class})
public interface AppPlaceHistoryMapper extends PlaceHistoryMapper{

}
