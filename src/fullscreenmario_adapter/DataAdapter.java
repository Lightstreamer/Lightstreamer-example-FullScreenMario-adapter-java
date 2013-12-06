/*
 *
 *  Copyright 2013 Weswit s.r.l.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *
 */

package fullscreenmario_adapter;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import com.lightstreamer.interfaces.data.DataProviderException;
import com.lightstreamer.interfaces.data.FailureException;
import com.lightstreamer.interfaces.data.ItemEventListener;
import com.lightstreamer.interfaces.data.SmartDataProvider;
import com.lightstreamer.interfaces.data.SubscriptionException;

public class DataAdapter implements SmartDataProvider {
	public static final String ITEM_NAME_USER_LIST_PREFIX= "UserList_";

	public static final String FIELD_NAME_INVOLVED_KEY= "involvedKey";
	public static final String FIELD_NAME_UP_OR_DOWN= "upOrDown";
	public static final String FIELD_NAME_X_LOCATION= "xLoc";
	public static final String FIELD_NAME_Y_LOCATION= "yLoc";
	public static final String FIELD_NAME_POWER= "power";
	public static final String FIELD_NAME_X_WINDOW_OFFSET= "xWindowOffset";
	public static final String FIELD_NAME_Y_WINDOW_OFFSET= "yWindowOffset";


	/////////////////////////////////////////////////////////////////////////
	// Singleton management

	private static DataAdapter __instance= null;

	public static DataAdapter getInstance() {
		return __instance;
	}


	/////////////////////////////////////////////////////////////////////////
	// Data

	private static class UserData {
		public Object itemHandle;
		public String involvedKey;
		public String upOrDown;
		public String xLocation;
		public String yLocation;
		public String power;
		public String xWindowOffset;
		public String yWindowOffset;
		public String mapAreaKey;
	}

	private static class MapAreaData {
		public Object itemHandle;
		public Set<String> users;

		public MapAreaData() {
			users= new LinkedHashSet<String>();
		}
	}


	/////////////////////////////////////////////////////////////////////////
	// Initialization

	private Map<String, MapAreaData> _mapAreaData;
	private Map<String, UserData> _userData;
	private ItemEventListener _listener;

	public DataAdapter() {
		__instance= this;

		_mapAreaData= new HashMap<String, MapAreaData>();
		_userData= new HashMap<String, UserData>();
	}

	@SuppressWarnings("rawtypes")
	public void init(Map params, File configDir) throws DataProviderException {}

	public void setListener(ItemEventListener listener) {
		_listener= listener;
	}


	/////////////////////////////////////////////////////////////////////////
	// Interface with metadata adapter

	public void onUserConnected(String user) {

		// Create user's data
		synchronized (this) {
			UserData userData= _userData.get(user);
			if (userData == null) {
				userData= new UserData();
				_userData.put(user, userData);
			}
		}
	}

	public void onUserUpdated(String user, String involvedKey, String upOrDown, String xLoc, String yLoc, String power, String xWindowOffset, String yWindowOffset, String map1, String map2, String area) {

		// Prepare map-area key (e.g. "11Overworld")
		String mapAreaKey= map1 + map2 + area;

		synchronized (this) {
			MapAreaData oldMapAreaData= null;
			MapAreaData newMapAreaData= null;

			// Retrieve user's data
			UserData userData= _userData.get(user);
			if (userData == null)
				throw new IllegalArgumentException("Unknown user: " + user);

			// Check if user changed map-area
			if (userData.mapAreaKey != null) {
				String oldMapAreaKey= userData.mapAreaKey;
				userData.mapAreaKey= mapAreaKey;

				if (!mapAreaKey.equals(oldMapAreaKey)) {

					// Map-area changed
					oldMapAreaData= _mapAreaData.get(oldMapAreaKey);
					if (oldMapAreaData == null)
						throw new IllegalStateException("User: " + user + " in unknown map-area: " + oldMapAreaKey);

					oldMapAreaData.users.remove(user);

					newMapAreaData= _mapAreaData.get(mapAreaKey);
					if (newMapAreaData == null) {
						newMapAreaData= new MapAreaData();
						_mapAreaData.put(mapAreaKey, newMapAreaData);
					}

					newMapAreaData.users.add(user);
				}

			} else {
				userData.mapAreaKey= mapAreaKey;

				// It's a new user entering a map-area for the first time
				newMapAreaData= _mapAreaData.get(mapAreaKey);
				if (newMapAreaData == null) {
					newMapAreaData= new MapAreaData();
					_mapAreaData.put(mapAreaKey, newMapAreaData);
				}

				newMapAreaData.users.add(user);
			}

			if ((oldMapAreaData != null) && (oldMapAreaData.itemHandle != null)) {

				// Send update with a delete command for previous map
		        Map<String, String> update= new HashMap<String, String>();
		        update.put("key", user);
		        update.put("command", "DELETE");

		        _listener.smartUpdate(oldMapAreaData.itemHandle, update, false);
			}

			if ((newMapAreaData != null) && (newMapAreaData.itemHandle != null)) {

				// Send update with an add command for current map
		        Map<String, String> update= new HashMap<String, String>();
		        update.put("key", user);
		        update.put("command", "ADD");

		        _listener.smartUpdate(newMapAreaData.itemHandle, update, false);
			}

			// Update user data
        	userData.involvedKey= involvedKey;
        	userData.upOrDown= upOrDown;
        	userData.xLocation= xLoc;
        	userData.yLocation= yLoc;
        	userData.power= power;
        	userData.xWindowOffset= xWindowOffset;
        	userData.yWindowOffset= yWindowOffset;

        	if (userData.itemHandle != null) {

    	        // Send user update
    	        Map<String, String> update= new HashMap<String, String>();
	        	update.put(FIELD_NAME_INVOLVED_KEY, userData.involvedKey);
	        	update.put(FIELD_NAME_UP_OR_DOWN, userData.upOrDown);
	        	update.put(FIELD_NAME_X_LOCATION, userData.xLocation);
	        	update.put(FIELD_NAME_Y_LOCATION, userData.yLocation);
	        	update.put(FIELD_NAME_POWER, userData.power);
	        	update.put(FIELD_NAME_X_WINDOW_OFFSET, userData.xWindowOffset);
	        	update.put(FIELD_NAME_Y_WINDOW_OFFSET, userData.yWindowOffset);

	            _listener.smartUpdate(userData.itemHandle, update, false);
        	}
		}
	}

	public void onUserDisconnected(String user) {
		synchronized (this) {

			// Remove user
			UserData userData= _userData.remove(user);

			if (userData.mapAreaKey != null) {
				MapAreaData mapAreaData= _mapAreaData.get(userData.mapAreaKey);
				if (mapAreaData == null)
					throw new IllegalStateException("User: " + user + " in unknown map-area: " + userData.mapAreaKey);

				mapAreaData.users.remove(user);

				// Send update with a delete command for current map
				if (mapAreaData.itemHandle != null) {
			        Map<String, String> update= new HashMap<String, String>();
			        update.put("key", user);
			        update.put("command", "DELETE");

			        _listener.smartUpdate(mapAreaData.itemHandle, update, false);
				}
			}
		}
	}


	/////////////////////////////////////////////////////////////////////////
	// SmartDataProvider interface

	public boolean isSnapshotAvailable(String itemName) throws SubscriptionException {
		return true;
	}

	public void subscribe(String itemName, boolean needsIterator) throws SubscriptionException, FailureException {
		assert(false); // Never called for a SmartDataProvider
	}

	public void subscribe(String itemName, Object itemHandle, boolean needsIterator) throws SubscriptionException, FailureException {

		// Check if item is a map-area's user list or a user's data
		if (itemName.startsWith(ITEM_NAME_USER_LIST_PREFIX)) {

			// Prepare map-area key (e.g. "11Overworld")
			String mapAreaKey= itemName.substring(ITEM_NAME_USER_LIST_PREFIX.length());

			synchronized (this) {

				// Store item handle in map-area data
				MapAreaData mapAreaData= _mapAreaData.get(mapAreaKey);
				if (mapAreaData == null) {
					mapAreaData= new MapAreaData();
					_mapAreaData.put(mapAreaKey, mapAreaData);
				}

				mapAreaData.itemHandle= itemHandle;

				// Send snapshot of current users
				for (String user : mapAreaData.users) {
			        Map<String, String> update= new HashMap<String, String>();
			        update.put("key", user);
			        update.put("command", "ADD");

			        _listener.smartUpdate(mapAreaData.itemHandle, update, true);
				}
			}

		} else {
			synchronized (this) {

				// Retrieve user's data (itemName is user's name)
				UserData userData= _userData.get(itemName);
				if (userData == null) {
					userData= new UserData();
					_userData.put(itemName, userData);
				}

				userData.itemHandle= itemHandle;

				// Send snapshot
		        Map<String, String> update= new HashMap<String, String>();
		        update.put(FIELD_NAME_INVOLVED_KEY, null);
		        update.put(FIELD_NAME_UP_OR_DOWN, null);
		        update.put(FIELD_NAME_X_LOCATION, userData.xLocation);
		        update.put(FIELD_NAME_Y_LOCATION, userData.yLocation);
		        update.put(FIELD_NAME_POWER, userData.power);
	        	update.put(FIELD_NAME_X_WINDOW_OFFSET, userData.xWindowOffset);
	        	update.put(FIELD_NAME_Y_WINDOW_OFFSET, userData.yWindowOffset);

		        _listener.smartUpdate(userData.itemHandle, update, true);
			}
		}

		_listener.endOfSnapshot(itemName);
	}

	public void unsubscribe(String itemName) throws SubscriptionException, FailureException {
		synchronized (this) {

			// Retrieve user's data (itemName is user's name)
			UserData userData= _userData.get(itemName);
			if (userData != null)
				userData.itemHandle= null;
		}
	}
}
