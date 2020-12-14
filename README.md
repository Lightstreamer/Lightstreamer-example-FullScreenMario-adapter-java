# Lightstreamer - FullScreenMario Demo - Java Adapter
<!-- START DESCRIPTION lightstreamer-example-fullscreenmario-adapter-java -->

The *Full-Screen Mario Demo* implements a multiplayer, HTML5 remake of the original Super Mario Brothers, running in the browser and using [Lightstreamer](http://www.lightstreamer.com) for its real-time communication needs.

This project shows the Data Adapter and Metadata Adapters for the *Full-Screen Mario Demo* and how they can be plugged into Lightstreamer Server.

As an example of a client using this adapter, you may refer to the [Lightstreamer - FullScreenMario Demo - HTML Client](https://github.com/Lightstreamer/Lightstreamer-example-FullScreenMario-client-javascript).

## Details
This project includes the implementation of the [SmartDataProvider](https://lightstreamer.com/api/ls-adapter-inprocess/latest/com/lightstreamer/interfaces/data/SmartDataProvider.html) interface and the [MetadataProviderAdapter](https://lightstreamer.com/api/ls-adapter-inprocess/latest/com/lightstreamer/interfaces/metadata/MetadataProviderAdapter.html) interface for the *Full-Screen Mario Demo*. Please refer to [General Concepts](https://lightstreamer.com/docs/ls-server/latest/General%20Concepts.pdf) for more details about Lightstreamer Adapters.

### Java Data Adapter and MetaData Adapter
The Data Adapter publishes data into Lightstreamer Server with real-time information about the movement of every player.
The Metadata Adapter receives input commands from Lightstreamer server, which forwards messages arrived from clients to the Data Adapter, notifying each player position and last command. These messages are then published to all players.
<!-- END DESCRIPTION lightstreamer-example-fullscreenmario-adapter-java -->

### The Adapter Set Configuration

This Adapter Set is configured and will be referenced by the clients as `MARIO`. 

The `adapters.xml` file for the *Full-Screen Mario Demo*, should look like:

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

<i>NOTE: not all configuration options of an Adapter Set are exposed by the file suggested above. 
You can easily expand your configurations using the generic template, see the [Java In-Process Adapter Interface Project](https://github.com/Lightstreamer/Lightstreamer-lib-adapter-java-inprocess#configuration) for details.</i><br>
<br>
Please refer [here](https://lightstreamer.com/docs/ls-server/latest/General%20Concepts.pdf) for more details about Lightstreamer Adapters.

## Install

If you want to install a version of this demo in your local Lightstreamer Server, follow these steps:
* Download Lightstreamer Server (Lightstreamer Server comes with a free non-expiring demo license for 20 connected users; this should be preferred to using COMMUNITY edition, otherwise you would see a limit on the event rate) from [Lightstreamer Download page](https://lightstreamer.com/download/), and install it, as explained in the `GETTING_STARTED.TXT` file in the installation home directory.
* Get the `deploy.zip` file of the [latest release](https://github.com/Lightstreamer/Lightstreamer-example-FullScreenMario-adapter-java/releases), unzip it, and copy the just unzipped `FullScreenMario` folder into the `adapters` folder of your Lightstreamer Server installation.
* Launch Lightstreamer Server.
* Test the Adapter, launching the client listed in [Clients Using This Adapter](https://github.com/Lightstreamer/Lightstreamer-example-FullScreenMario-adapter-java#clients-using-this-adapter).

## Build

To build your own version of `Lexample-FullScreenMario-adapter-java-0.0.1-SNAPSHOT.jar`, instead of using the one provided in the `deploy.zip` file from the [Install](https://github.com/Lightstreamer/Lightstreamer-example-FullScreenMario-adapter-java#install) section above, you have two options:
either use [Maven](https://maven.apache.org/) (or other build tools) to take care of dependencies and building (recommended) or gather the necessary jars yourself and build it manually.

For the sake of simplicity only the Maven case is detailed here.

### Maven

You can easily build and run this application using Maven through the pom.xml file located in the root folder of this project. As an alternative, you can use an alternative build tool (e.g. Gradle, Ivy, etc.) by converting the provided pom.xml file.

Assuming Maven is installed and available in your path you can build the demo by running
```sh 
 mvn install dependency:copy-dependencies 
```


## See Also

### Clients Using This Adapter
<!-- START RELATED_ENTRIES -->

* [Lightstreamer - FullScreenMario Demo - HTML Client](https://github.com/Lightstreamer/Lightstreamer-example-FullScreenMario-client-javascript)

<!-- END RELATED_ENTRIES -->

### Related Projects

* [Lightstreamer - Room-Ball Demo - HTML Client](https://github.com/Lightstreamer/Lightstreamer-example-RoomBall-client-javascript#lightstreamer-room-ball-demo-for-javascript-client)

## Lightstreamer Compatibility Notes

- Compatible with Lightstreamer SDK for Java In-Process Adapters since 7.3.
- For a version of this example compatible with Lightstreamer SDK for Java Adapters version 6.0, please refer to [this tag](https://github.com/Lightstreamer/Lightstreamer-example-FullScreenMario-adapter-java/tree/pre_mvn).