/*
 *
 *  Copyright (c) Lightstreamer Srl
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
import java.util.Map;

import com.lightstreamer.interfaces.metadata.CreditsException;
import com.lightstreamer.interfaces.metadata.ItemsException;
import com.lightstreamer.interfaces.metadata.MetadataProviderAdapter;
import com.lightstreamer.interfaces.metadata.MetadataProviderException;
import com.lightstreamer.interfaces.metadata.NotificationException;
import com.lightstreamer.interfaces.metadata.SchemaException;

public class MetadataAdapter extends MetadataProviderAdapter {


	/////////////////////////////////////////////////////////////////////////
	// Initialization

	private Map<String, String> _userNamesBySessionId;
	private Map<String, String> _sessionIdsByUserName;

	public MetadataAdapter() {
		_userNamesBySessionId= new HashMap<String, String>();
		_sessionIdsByUserName= new HashMap<String, String>();
	}

	@Override
	@SuppressWarnings("rawtypes")
	public void init(Map params, File configDir) throws MetadataProviderException {}


	/////////////////////////////////////////////////////////////////////////
	// Items-related methods

	public String[] getItems(String user, String sessionID, String group) throws ItemsException {
		return new String [] { group };
	}

	public String[] getSchema(String user, String sessionID, String group, String schema) throws ItemsException, SchemaException {
		if (group.startsWith(DataAdapter.ITEM_NAME_USER_LIST_PREFIX))
			return new String [] { "key", "command" };

		return new String [] {
				DataAdapter.FIELD_NAME_INVOLVED_KEY, DataAdapter.FIELD_NAME_UP_OR_DOWN,
				DataAdapter.FIELD_NAME_X_LOCATION, DataAdapter.FIELD_NAME_Y_LOCATION,
				DataAdapter.FIELD_NAME_POWER,
				DataAdapter.FIELD_NAME_X_WINDOW_OFFSET, DataAdapter.FIELD_NAME_Y_WINDOW_OFFSET };
	}


	/////////////////////////////////////////////////////////////////////////
	// Session control methods

	@Override
	public void notifySessionClose(String sessionID) throws NotificationException {
		String user= null;

		synchronized (this) {

			// Retrieve user for this session ID
			user= _userNamesBySessionId.remove(sessionID);
			if (user == null)
				return;

			_sessionIdsByUserName.remove(user);
		}

		// Notify data adapter
		DataAdapter.getInstance().onUserDisconnected(user);
	}


	/////////////////////////////////////////////////////////////////////////
	// Message handling methods

	@Override
	public void notifyUserMessage(String user, String sessionID, String message) throws CreditsException, NotificationException {
	    if (message.startsWith("n|")) {
            notifyUserName(sessionID, message.substring(2));

		} else {
		    String userName= "";

		    synchronized (this) {
    	        userName= _userNamesBySessionId.get(sessionID);

    	        // Check if user is connected
    			if (userName == null)
    			    return; // We assume it is a late message, that can be ignored
    		}

    		// Extract user's data
    		String [] segments= message.split("\\|");
    		if (segments.length < 10)
    			throw new NotificationException("Maformed message received");

    		String involvedKey= (segments[0].trim().length() > 0) ? segments[0] : null;
    		String upOrDown= (segments[1].trim().length() > 0) ? segments[1] : null;
    		String xLoc= (segments[2].trim().length() > 0) ? segments[2] : null;
    		String yLoc= (segments[3].trim().length() > 0) ? segments[3] : null;
    		String power= (segments[4].trim().length() > 0) ? segments[4] : null;
    		String xWindowOffset= (segments[5].trim().length() > 0) ? segments[5] : null;
    		String yWindowOffset= (segments[6].trim().length() > 0) ? segments[6] : null;
    		String map1= (segments[7].trim().length() > 0) ? segments[7] : null;
    		String map2= (segments[8].trim().length() > 0) ? segments[8] : null;
    		String area= (segments[9].trim().length() > 0) ? segments[9] : null;

    		if ((xLoc == null) || (yLoc == null) || (power == null) || (xWindowOffset == null) || (yWindowOffset == null) || (map1 == null) || (map2 == null) || (area == null))
    			throw new NotificationException("Malformed message recevied");

    		// Notify data adapter
    		DataAdapter.getInstance().onUserUpdated(userName, involvedKey, upOrDown, xLoc, yLoc, power, xWindowOffset, yWindowOffset, map1, map2, area);
		}
	}

    private void notifyUserName(String sessionID, String userName) throws CreditsException {
        synchronized (this) {
        	if ((userName == null) || (userName.trim().length() == 0)) {
                throw new CreditsException(-100, "Nickname must be specified");

        	} else if (userName.startsWith(DataAdapter.ITEM_NAME_USER_LIST_PREFIX)) {
                throw new CreditsException(-109, "Nice try, man");

        	} else if (_userNamesBySessionId.containsKey(sessionID)) {
        		if (_userNamesBySessionId.get(sessionID).equals(userName))
        			return; // Received the same nickname more than once, just ignore it

        		// Different nickname on the same session it's evil
                throw new CreditsException(-2710, "User already logged on same session.");

        	} else if (_sessionIdsByUserName.containsKey(userName)) {

        		// Append a number (beginning with 2) to the user name
                int ik= 2;
                while (_sessionIdsByUserName.containsKey(userName + ik))
                    ik++;

                userName= userName + ik;
                this.initUser(sessionID, userName);

                // Client will parse this exception and update its user name
                throw new CreditsException(-2720, userName);

        	} else if (_sessionIdsByUserName.size() > 200)
                throw new CreditsException(-2700, "Too many users. Please try again later.");

            this.initUser(sessionID, userName);
        }
    }

    private void initUser(String sessionID, String userName) {
        _userNamesBySessionId.put(sessionID, userName);
        _sessionIdsByUserName.put(userName, sessionID);

        // Notify data adapter
        DataAdapter.getInstance().onUserConnected(userName);
    }
}
