
# Finch 4 Alice

Ever wanted to control your [Finch robot](http://www.finchrobot.com/) from within [Alice 3](http://www.alice.org/)?  Well, now you can!

Finch 4 Alice provides a simple extension to Alice 3 that adds methods for controlling a Finch robot to all Transport subclass instances.

## Building

You can build the Jar only for manual installation, or create several types of installers.

### Prerequisites

#### Alice 3

You must have an installation of Alice 3 on the build machine, preferrably installed in the default location.  This location varies by platform, as shown in the following table:

| Platform | Default Installation Location |
| -------- | ----------------------------- |
| Windows  | C:\Program Files\Alice 3      |
| Linux    | ~/Alice3/lib                  |
| Max OS X | ~/Alice3/lib                  |

#### JDK

You must have the Java Development Kit (JDK) version 1.7 or newer installed and properly configured.  You can obtain a copy of the JDK [from Oracle](http://www.oracle.com/technetwork/java/javase/downloads/index.html), or from a distribution provided by your operating system vendor.  OpenJDK should work fine, if you don't feel like using the Oracle proprietary JDK.

### Building the Jar file only

If Alice 3 is installed in the default location, executing the following command from within the project folder will build the Jar file.

```
./gradlew jar
```

If Alice is installed in a non-standard path, you can specify the correct path by providing it via the `aliceDir` command-line property value as follows:

```
./gradlew jar -PaliceDir=/path/to/alice3
```

### Installation

#### Direct install

If you wish to install Finch 4 Alice on the build machine, it can be installed directly by Gradle.  To accomplish this, execute the following command:

```
./gradlew install
```

This will automatically perform the steps described under [Manual installation](manual-installation).

#### Multi-platform / Windows Installer

This type of installer is the easiest to use for most end-users.  It provides an intuitive window-based installer that walks users through the installation process, and will run on all of the supported platforms.

```
./gradlew izPackCreateInstaller
```


#### RPM package

Users of Redhat Enterprise Linux or Fedora Linux may prefer to use RPM to install.  To generate a suitable RPM package:

```
./gradlew buildRpm
```

The generated RPM can be found under `build/distributions/`.

You can install the generated package using `rpm`, as shown below:

```
rpm -ivh build/distributions/finch4alice_0.1_noarch.rpm
```

OR, if you have already installed a previous version, use the upgrade command.  This is also safe to run if you haven't yet installed the package.

```
rpm -Uvh build/distributions/finch4alice_0.1_noarch.rpm
```

To install using `yum` and for distribution to multiple machines, it is recommended that you set up a local yum repository server.

> Note that the generated package will only install into the same folder structure as Alice is installed under on the build machine.  Since Alice is generally installed to each user's home directory, this type of packaging will generally not be the best option.  However, if you wish to install Alice in a system-wide configuration on multiple machines, this may be the route to go.

#### Debian (DEB) package

If you run a Debian-based system and prefer installing via `apt`, then this may be the installation type for you.  You can build a Debian package by running the following command:

```
./gradlew buildDeb
```

The generated DEB can be found under `build/distributions/`.

You can install the generated package using `dpkg`, as shown below:

```
dpkg -i build/distributions/finch4alice_0.1_all.deb
```

To install using `apt` and for distribution to multiple machines, it is recommended that you set up a local apt cache repository.  One such option is to use [reprepro](https://mirrorer.alioth.debian.org/).

> As stated above for the RPM package, the generated Debian paclage will only install into the same folder structure as Alice is installed under on the build machine.  Since Alice is generally installed to each user's home directory, this type of packaging will generally not be the best option.  However, if you wish to install Alice in a system-wide configuration on multiple machines, this may be the route to go.

#### Manual installation

If for some reason the above options don't work for your particular situation, or you simply enjoy getting into the nitty gritty aspects of software hacking, then this is the section for you.

To manually install Finch 4 Alice:

1. First build the Jar as described above under [Building the Jar file only](#building-the-jar-file-only).
```
./gradlew jar
```
2. Copy the generated Jar file from the build directory into the Alice `ext` directory
```
mkdir /path/to/alice/ext/finch4alice
cp ./build/finch4alice-0.1.jar /path/to/alice/ext/finch4alice/finch4alice.jar
```
3. Create an Install4J configuration to instruct Alice to load the new Jar.
  * For Windows, copy the file `src/resources/Alice 3.vmoptions.windows` into the Alice main folder and rename it to `Alice 3.vmoptions`.
  * For Linux and Mac OS X:
```
cp "src/resources/Alice 3.vmoptions.linux" "/path/to/alice/Alice 3.vmoptions"
```

## Using Finch 4 Alice

### Confirming installation

To ensure that everything is installed and configured properly:

1. Launch Alice
2. When prompted to load a project, simple close the dialog by clicking 'Cancel'.

TODO: image here

3. Select the `Help -> Show System Properties` menu

TODO: image here

4. Click `Show...` to the right of `java.class.path`

TODO: image here

5. Verify that the finch4alice jar is listed at the beginning of the class path.

TODO: image here

### Connecting to a Finch

TODO: Provide a simple example of how to interact with a connected Finch robot

### Additions to the STransport API

Finch 4 Alice works by extending the STransport parent class within Alice.  Several new procedures and functions are added that expose the full range of functionality provided by the Finch robot.

#### Finch procedures

`finchBuzz(int frequency, int duration)`

`finchPlayRTTTL(String rtttl, double frequencyMultiplier, double durationMultiplier, boolean useComputerSpeakers)`

`finchSetLED(int red, int green, int blue)`

`finchSetWheelVelocities(int leftVelocity, int rightVelocity)`

`finchStopWheels()`



#### Finch functions

`int[] finchGetLightSensors()`

`int finchGetLeftLightSensor()`

`int finchGetRightLightSensor()`

`boolean finchIsLeftLightSensor(int limit)`

`boolean finchIsRightLightSensor(int limit)`

`double finchGetXAcceleration()`

`double finchGetYAcceleration()`

`double finchGetZAcceleration()`

`boolean finchIsBeakDown()`

`boolean finchIsBeakUp()`

`boolean finchIsLevel()`

`boolean finchIsUpsideDown()`

`boolean finchIsLeftWingDown()`

`boolean finchIsRightWingDown()`

### Debugging and Logging

TODO: Add some details on debugging and logging here

## Contributing

Contributions are very welcome and are accepted through pull requests.

## Bugs and Features Requests

Please submit all bug reports and feature requests to the project [issue tracker](https://github.com/bradcfisher/finch4alice/issues).

## Disclaimers & License

The Finch 4 Alice project is not affiliated with either the Finch or Alice 3 projects or their respective intellectual property holders.

The Finch robot is produced by [BirdBrain Technologies LLC](http://www.birdbraintechnologies.com/).

Alice 3 is developed by [Carnegie Mellon University](http://www.cs.cmu.edu/).

This software interacts with the [BirdBrain Robot Server](https://github.com/BirdBrainTechnologies/BirdBrainRobotServer) which is covered by a [Creative Commons Attribution-ShareAlike 3.0 Unported License](http://creativecommons.org/licenses/by-sa/3.0/).

---

Finch 4 Alice is released under the BSD 2-Clause License

Copyright (c) 2015, Brad Fisher  
All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.

2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.