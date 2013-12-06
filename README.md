# Lightstreamer Full-Screen-Mario Demo Adapter #

This project includes the resources needed to develop the Metadata and Data Adapters for the [FullScreenMario Demo]() that is pluggable into Lightstreamer Server. Please refer [here](http://www.lightstreamer.com/latest/Lightstreamer_Allegro-Presto-Vivace_5_1_Colosseo/Lightstreamer/DOCS-SDKs/General%20Concepts.pdf) for more details about Lightstreamer Adapters.
The <b>FullScreenMario Demo</b> implements a multiplayer HTML5 remake of the original Super Mario Brothers.<br>
<br>
The project is comprised of source code and a deployment example.

## Java Data Adapter and MetaData Adapter ##
A Java Adapter implementing both the [SmartDataProvider](http://www.lightstreamer.com/docs/adapter_java_api/com/lightstreamer/interfaces/data/SmartDataProvider.html) interface and the [MetadataProviderAdapter](http://www.lightstreamer.com/docs/adapter_java_api/com/lightstreamer/interfaces/metadata/MetadataProviderAdapter.html) interface, to publish data into Lightstreamer server with real time information about the movement of every player.
The adapter receives input commands from Lightstreamer server, which forwards messages arrived from clients to the adapter notifying each player position and last command. These messages are then published to all players.

# Build #

If you want to skip the build process of this Adapter please note that in the [deploy release]() of this project you can find the "deploy.zip" file that contains a ready-made deployment resource for the Lightstreamer server. <br>
Otherwise follow these steps:

* Get the `ls-adapter-interface.jar`, file from the [latest Lightstreamer distribution](http://www.lightstreamer.com/download).
* Build the jar `LS_FullScreenMario_Demo_Adapters.jar` with commands like these:
```sh
 >javac -source 1.7 -target 1.7 -nowarn -g -classpath lib/ls-adapter-interface.jar -sourcepath src/ -d tmp_classes src/com/lightstreamer/adapters/fullscreenmario_adapter/DataAdapter.java
 
 >jar cvf LS_FullScreenMario_Demo_Adapters.jar -C tmp_classes com
```

# Deploy #

You have to create a specific folder to deploy the FullScreenMario Demo Adapters otherwise get the ready-made `FullScreenMario` deploy folder from `deploy.zip` of the [latest release]() of this project.<br>
If you choose to create you own folder, follow the next steps, otherwise skip them. 

1. Create a new folder, let's call it `FullScreenMario`, and a `lib` folder inside it.
2. Copy the jar file of the adapter `LS_FullScreenMario_Demo_Adapters.jar`, compiled in the previous section, in the newly created `lib` folder.
3. Create an `adapters.xml` file inside the `FullScreenMario` folder and use the following content (this is an example configuration, you can modify it to your liking):
```xml      
<?xml version="1.0"?>

<!-- Mandatory. Define an Adapter Set and its unique ID. -->
<adapters_conf id="MARIO">

    <!-- Mandatory. Define the Metadata Adapter. -->
    <metadata_provider>

        <!-- Mandatory. Java class name of the adapter. -->
        <adapter_class>fullscreenmario_adapter.MetadataAdapter</adapter_class>

    </metadata_provider>

    <!-- Mandatory. Define a Data Adapter. -->
    <data_provider name="USERS">

        <!-- Mandatory. Java class name of the adapter. -->
        <adapter_class>fullscreenmario_adapter.DataAdapter</adapter_class>

    </data_provider>

</adapters_conf>
```
<br> 

Now your `FullScreenMario` folder is ready to be deployed in the Lightstreamer server, please follow these steps:<br>

1. Make sure you have installed Lightstreamer Server, as explained in the GETTING_STARTED.TXT file in the installation home directory.
2. Make sure that Lightstreamer Server is not running.
3. Copy the `FullScreenMario` directory and all of its files to the `adapters` subdirectory in your Lightstreamer Server installation home directory.
4. Lightstreamer Server is now ready to be launched.

Please test your Adapter with the [client](https://github.com/Weswit/Lightstreamer-example-FullScreenMario-adapter-java#clients-using-this-adapter) below.

# See Also #

## Clients using this Adapter ##

* [FullScreenMario Demo Client for JavaScript]()

## Related projects ##

* [Lightstreamer Room-Ball Demo](https://github.com/Weswit/Lightstreamer-example-RoomBall-client-javascript#lightstreamer-room-ball-demo-for-javascript-client)

# Lightstreamer Compatibility Notes #

- Compatible with Lightstreamer SDK for Java Adapters since 5.1

