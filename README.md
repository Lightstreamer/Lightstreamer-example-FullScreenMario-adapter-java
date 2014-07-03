# Lightstreamer - FullScreenMario Demo - Java Adapter
<!-- START DESCRIPTION lightstreamer-example-fullscreenmario-adapter-java -->

The *Full-Screen Mario Demo* implements a multiplayer, HTML5 remake of the original Super Mario Brothers, running in the browser and using [Lightstreamer](http://www.lightstreamer.com) for its real-time communication needs.

This project shows the Data Adapter and Metadata Adapters for the *Full-Screen Mario Demo* and how they can be plugged into Lightstreamer Server.

As example of a client using this adapter, you may refer to the [Lightstreamer - FullScreenMario Demo - HTML Client](https://github.com/Weswit/Lightstreamer-example-FullScreenMario-client-javascript).

## Details
This project includes the implementation of the [SmartDataProvider](http://www.lightstreamer.com/docs/adapter_java_api/com/lightstreamer/interfaces/data/SmartDataProvider.html) interface and the [MetadataProviderAdapter](http://www.lightstreamer.com/docs/adapter_java_api/com/lightstreamer/interfaces/metadata/MetadataProviderAdapter.html) interface for the Lightstreamer Chat Demos. Please refer to [General Concepts](http://www.lightstreamer.com/latest/Lightstreamer_Allegro-Presto-Vivace_5_1_Colosseo/Lightstreamer/DOCS-SDKs/General%20Concepts.pdf) for more details about Lightstreamer Adapters.

### Java Data Adapter and MetaData Adapter
The Data Adapter publishes data into Lightstreamer Server with real time information about the movement of every player.
The Metadata Adapter receives input commands from Lightstreamer server, which forwards messages arrived from clients to the Data Adapter, notifying each player position and last command. These messages are then published to all players.
<!-- END DESCRIPTION lightstreamer-example-fullscreenmario-adapter-java -->

## Install

* Download Lightstreamer Server Vivace (make sure you use Vivace edition, otherwise you will see a limit on the event rate; Lightstreamer Server comes with a free non-expiring demo license for 20 connected users) from [Lightstreamer Download page](http://www.lightstreamer.com/download.htm), and install it, as explained in the `GETTING_STARTED.TXT` file in the installation home directory.
* Get the `deploy.zip` file of the [latest release](https://github.com/Weswit/Lightstreamer-example-FullScreenMario-adapter-java/releases), unzip it and copy the just unzipped `FullScreenMario` folder into the `adapters` folder of your Lightstreamer Server installation.
* Launch Lightstreamer Server.
* Test the Adapter, launching the client listed in [Clients Using This Adapter](https://github.com/Weswit/Lightstreamer-example-FullScreenMario-adapter-java#clients-using-this-adapter).

## Build 
To build your own version of `LS_FullScreenMario_Demo_Adapters.jar`, instead of using the one provided in the `deploy.zip` file, follow these steps.
* Clone this project.
*  Get the `ls-adapter-interface.jar` file from the [latest Lightstreamer distribution](http://www.lightstreamer.com/download), and copy it into the `lib` directory.
* Build the jar `LS_FullScreenMario_Demo_Adapters.jar` with commands like these:
```sh
 >javac -source 1.7 -target 1.7 -nowarn -g -classpath lib/ls-adapter-interface.jar -sourcepath src/ -d tmp_classes src/com/lightstreamer/adapters/fullscreenmario_adapter/DataAdapter.java
 
 >jar cvf LS_FullScreenMario_Demo_Adapters.jar -C tmp_classes com
```
* Stop Lightstreamer Server; copy the just compiled `LS_FullScreenMario_Demo_Adapters.jar` in the `adapters/FullScreenMario/lib` folder of your Lightstreamer Server installation; restart Lightstreamer Server.

## See Also

### Clients Using This Adapter
<!-- START RELATED_ENTRIES -->

* [Lightstreamer - FullScreenMario Demo - HTML Client](https://github.com/Weswit/Lightstreamer-example-FullScreenMario-client-javascript)

<!-- END RELATED_ENTRIES -->

### Related Projects

* [Lightstreamer - Room-Ball Demo - HTML Client](https://github.com/Weswit/Lightstreamer-example-RoomBall-client-javascript#lightstreamer-room-ball-demo-for-javascript-client)

## Lightstreamer Compatibility Notes

- Compatible with Lightstreamer SDK for Java Adapters since 5.1
