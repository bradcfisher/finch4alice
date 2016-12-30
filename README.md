# Finch 4 Alice

[![Build Status](https://travis-ci.org/bradcfisher/finch4alice.png)](https://travis-ci.org/bradcfisher/finch4alice)

Ever wanted to control your [Finch robot](http://www.finchrobot.com/) from within [Alice 3](http://www.alice.org/)?  Well, now you can!

Finch 4 Alice provides a simple extension to Alice 3 that adds methods for controlling a Finch robot to all Transport subclass instances.  Cross-platform support is provided out-of-box for Microsoft Windows, Apple Macintosh OS X, and Linux operating systems.

## Contents

* [Obtaining Finch 4 Alice](#obtaining-finch-4-alice)
* [Building the project](#building-the-project)
  * [Build prerequisites](#build-prerequisites)
    * [Alice 3](#alice-3)
    * [JDK](#jdk)
    * [A local copy of Finch 4 Alice](#a-local-copy-of-finch-4-alice)
  * [Common tasks](#common-tasks)
    * [Direct install](#direct-install)
    * [Multi-platform installer Jar](#multi-platform-installer-jar)
    * [Windows executable installer](#windows-executable-installer)
    * [Shell wrapper script](#shell-wrapper-script)
    * [Manual installation](#manual-installation)
    * [Building the Jar file only](#building-the-jar-file-only)
  * [Additional build tasks for developers](#additional-build-tasks-for-developers)
    * [Generating API documentation](#generating-api-documentation)
    * [Publishing documentation to GitHub](#publishing-documentation-to-github)
    * [Updating the Gradle wrapper version](#updating-the-gradle-wrapper-version)
    * [Checking Gradle plugins and dependencies for updates](#checking-gradle-plugins-and-dependencies-for-updates)
* [Using Finch 4 Alice](#using-finch-4-alice)
  * [Confirming installation](#confirming-installation)
  * [Connecting to a Finch](#connecting-to-a-finch)
    * [Prerequisites](#prerequisites)
    * [Example project walkthrough](#example-project-walkthrough)
  * [Bundled example projects](https://github.com/bradcfisher/finch4alice/examples)
* [How it works](#how-it-works)
  * [Additions to the STransport API](#additions-to-the-stransport-api)
* [Troubleshooting FAQ](#troubleshooting-faq)
* [Contributing](#contributing)
* [Bugs and feature requests](#bugs-and-feature-requests)
* [Disclaimers and license](#disclaimers-and-license)

## Obtaining Finch 4 Alice

Most users will probably be most interested in the [latest release](https://github.com/bradcfisher/finch4alice/releases/latest).  All binary and source code releases of Finch 4 Alice can be obtained from the project's [GitHub releases](https://github.com/bradcfisher/finch4alice/releases) page.

If you are a developer, or are interested in using the current pre-release code, you can make [a local copy of Finch 4 Alice](#a-local-copy-of-finch-4-alice) GitHub repository.

## Building the project

### Build prerequisites

#### Alice 3

You must have an installation of [Alice 3](http://www.alice.org/) on the build machine, preferrably installed in the default location.  This location varies by platform, as shown in the following table:

| Platform | Default Installation Location                    |
| -------- | ------------------------------------------------ |
| Windows  | C:\Program Files\Alice 3                         |
| Linux    | ~/Alice3                                         |
| Max OS X | /Applications/Alice 3.app/Contents/Resources/app |

Finch 4 Alice has been tested against Alice versions 3.2.5 and 3.3.0.  Previous versions used a different type of installer and are not compatible.

To download a copy of Alice for your environment, visit the [Alice website](http://www.alice.org/).

#### JDK

You must have the Java Development Kit (JDK) version 1.8 or newer installed and properly configured.  You can obtain a copy of the JDK [from Oracle](http://www.oracle.com/technetwork/java/javase/downloads/index.html), or from a distribution provided by your operating system vendor.  OpenJDK should work fine, if you don't feel like using the Oracle proprietary JDK.

#### A local copy of Finch 4 Alice

To build Finch 4 Alice, you mush have a local copy of the source code.  You can obtain the source from the [Finch 4 Alice GitHub repository](https://github.com/bradcfisher/finch4alice) using any [Git](https://git-scm.com/) client, or download a [zip archive of the current source](https://github.com/bradcfisher/finch4alice/archive/master.zip) and extract with your favorite archive application.

Using the standard git command line client, you can create a local clone of the Finch 4 Alice repository with the following command:

```
git clone https://github.com/bradcfisher/finch4alice.git
```

### Common tasks

Several common tasks are available for building and installing Finch 4 Alice, including installing the project [directly from the command line](#direct-install), [creating an interactive multi-platform installer](#multi-platform-installer-jar) to distribute and use on multiple machines, or [building the Jar only](#building-the-jar-file-only) for manual installation.

#### Direct install

If you wish to install Finch 4 Alice on the build machine, it can be installed directly from a shell prompt using the [Gradle](https://gradle.org/) build tool.  To accomplish this, execute the following command from the root of the Finch 4 Alice source:

```
./gradlew install
```

This will automatically perform the steps described under [Manual installation](#manual-installation), and is the lightest-weight installation option for installing from the Finch 4 Alice source.

Because Alice is installed system-wide by default on Windows and Macintosh OS X, you will need to ensure you execute the command as Administrator (root on OS X) or the installation will likely fail due to lack of permissions.

Under Linux, you should be able to run the install as a normal user, as long as Alice is installed in the default location under the current user's home directory.  If it is installed in a system-wide location like `/bin`, `/usr/bin`, or `/usr/local/bin` you will need to run the command as root using `sudo`: `sudo ./gradlew install`.

Two alternatives exist to the above method, both of which generate and invoke the [multi-platform installer](#multi-platform-installer-jar) automatically to perform the installation.  The first alternative method executes the generated installer Jar using the same Java runtime as used by Gradle, and is supported on all platforms.  Remember to execute the following command with administrator access on systems that require it.

```
./gradlew installIzPack
```

The second alternative executes a [shell script wrapper](#shell-wrapper-script) and is only supported on Linux and OS X, since Windows doesn't have native Unix shell support.  On OS X, the following following command will need to be executed with administrator access (sudo).

```
./gradlew installShellWrapper
```

#### Multi-platform installer Jar

The runnable installer Jar created using this method is the easiest to use for most end-users who do not have access to command line tools like Git and Gradle.  It provides an intuitive graphical installer that walks users through the installation process, doesn't require any special build tools to be installed on the target computer, and will run on all of the supported platforms with a properly configured [JRE](http://www.oracle.com/technetwork/java/javase/downloads/index-jsp-138363.html).

Executing the following command in a shell prompt under the root of the Finch 4 Alice source will create the installer Jar file:

```
./gradlew izPackCreateInstaller
```

Once generated, you can find the installer under `build/distributions/finch4alice-<version>-installer.jar`.

The installer Jar can be [wrapped in a shell script](#shell-wrapper-script) to be executed on Linux or OS X, bundled as a [Windows executable](#windows-executable-installer), or it can be executed using the following command in any supported environment:

```
java -jar finch4alice-<version>-installer.jar
```

Depending on your operating system configuration, you may also have alternative launch options.  For example, on Mac OS X systems with the Oracle JDK installed, you can launch the installer Jar directly from Finder by simply double-clicking on the icon.  Ubuntu Linux systems also support double-clicking the icon to launch the installer, but the Jar file must have the executable bit set for that to work.

#### Windows executable installer

A native Microsoft Windows executable installer can be created from the installer Jar with the following command:

```
./gradlew launch4j
```

The generated EXE will be placed under `build/distributions/finch4alice-<version>-setup.exe`.

#### Shell wrapper script

The following command can be used on Linux and OS X to create a shell wrapper script around the multi-platform installer Jar.

```
./gradlew createShellWrapper
```

The wrapper script can then be executed from terminal with:

```
sh ./build/distributions/finch4alice-<version>-installer.sh
```

For automating installations, the shell script will look for an `INSTALL_PATH` environment variable.  If such a variable is defined, the path conotained therein will be used as the destination folder for installing Finch 4 Alice.  The variable should contain the path to the Alice 3 installation which should be updated.  If `INSTALL_PATH` is not defined, the wrapper script will use the [standard Alice 3 installation path](#alice-3) for the current operating system.

#### Manual installation

If for some reason the above options don't work for your particular situation, or you simply enjoy getting into the nitty gritty aspects of software hacking, then this is the section for you.

To manually install Finch 4 Alice:

* First build the Jar as described below under [Building the Jar file only](#building-the-jar-file-only).
```
./gradlew jar
```

* Create a new subdirectory under the Alice `ext` directory named `ext/finch4alice`.
```
mkdir /path/to/alice/ext/finch4alice
```

* Copy the generated Jar file `build/distributions/finch4alice-<version>.jar` into the Alice `ext/finch4alice` directory, and rename it to `finch4alice.jar`.
```
cp ./build/libs/finch4alice-<version>.jar /path/to/alice/ext/finch4alice/finch4alice.jar
```

* Create an Install4J vmoptions configuration file to instruct Alice to load the new Jar when it starts up.
  * For Windows, copy the file `src/resources/Alice 3.vmoptions.windows` into the Alice main folder and rename it to `Alice 3.vmoptions`.
  * For Linux, copy `src/resources/Alice 3.vmoptions.linux`, renaming it to `Alice 3.vmoptions`:
```
cp "src/resources/Alice 3.vmoptions.linux" "/path/to/alice/Alice 3.vmoptions"
```
  * For Mac OS X, copy `src/resources/Alice 3.vmoptions.linux`, renaming it to `vmoptions.txt`:
```
cp "src/resources/Alice 3.vmoptions.linux" "/Applications/Alice 3.app/Contents/vmoptions.txt"
```

Because Alice is installed system-wide by default on Windows, you must use an Administrator account and will likely need to confirm a few UAC dialogs to copy and rename the files.

For Mac OS X, the commands will need to be executed by a user in the `admin` group, or as `root` (using sudo).

Under Linux, you should be able to run the install as a normal user, as long as Alice is installed in the default location under that user's home directory.  If it is installed in a system-wide location like `/bin`, `/usr/bin`, or `/usr/local/bin` you will need to run the command as root with `sudo`.

#### Building the Jar file only

If Alice 3 is installed in the default location, executing the following command from within the project folder will build the Finch 4 Alice extension Jar file.

```
./gradlew jar
```

If Alice is installed in a non-standard path, you can specify the correct path by providing it via the `aliceDir` command-line property value as follows:

```
./gradlew jar -PaliceDir=/path/to/alice3
```

### Additional build tasks for developers

This section describes several additional Gradle tasks that are of interest to developers that want to contribute to the project.  Most users will probably want to skip ahead to [Using Finch 4 Alice](#using-finch-4-alice).

#### Generating API documentation

Documentation of the Finch 4 Alice API can be generated using the following command:

```
./gradlew javadoc
```

The generated documentation is placed under `build\docs\javadoc`.

#### Publishing documentation to GitHub

The documentation generated by the `javadoc` task can be published to finch4alice.com using the following command:

```
./gradlew publishGhPages
```

For the above command to succeed, you must have push access to the Finch 4 Alice GitHub project and a [properly configured SSH key](https://help.github.com/articles/generating-an-ssh-key/).  If you want to use an SSH key other than the default key configured for the current user, you can specify the key to use as an option to the task:

```
./gradlew publishGhPages -PgitSshKey=<PATH TO YOUR SSH KEY>
```

#### Updating the Gradle wrapper version

The Gradle version used by `gradlew` can be updated by executing the following task:

```
./gradlew wrapper --gradle-version <NEW GRADLE VERSION>
```

#### Checking Gradle plugins and dependencies for updates

To check for out of date dependency versions, execute the following task:

```
./gradlew dependencyUpdates
```

## Using Finch 4 Alice

### Confirming installation

To ensure that everything is installed and configured properly:

* Launch Alice

* When prompted to load a project, simply close the dialog by clicking 'Cancel'.

![Select Project Dialog](https://github.com/bradcfisher/finch4alice/images/readme/select_project_dialog.png "Click the 'Cancel' button to close this dialog")

* Select the `Help -> Show System Properties` menu

![Show System Properties Menu](https://github.com/bradcfisher/finch4alice/images/readme/show_system_properties_menu.png "The 'Show System Properties' menu")

* Click `Show...` to the right of `java.class.path`

![Show System Properties Dialog](https://github.com/bradcfisher/finch4alice/images/readme/show_system_properties_dialog.png "The 'Show System Properties' dialog")

* Verify that the finch4alice Jar is listed at the beginning of the class path.

![System Property: java.class.path Dialog](https://github.com/bradcfisher/finch4alice/images/readme/java_class_path_dialog.png "The 'System Property: java.class.path' dialog")

### Connecting to a Finch

Now that you have Finch 4 Alice installed, what can you do with it?  In this section, we'll go through a simple example and "kick the tires", so to speak.

#### Prerequisites

* You need an installation of Alice 3, with Finch 4 Alice installed.

* You need a Finch robot, properly connected to your computer.  If you don't happen to have a Finch, they can be purchased from the [Finch Robot website](http://www.finchrobot.com/).

* You will need to install the BirdBrain Robot Server.  You can find installers for various operating systems in the [BirdBrain Robot Server GitHub repository](https://github.com/BirdBrainTechnologies/BirdBrainRobotServer/tree/master/Packages).  Perhaps surprisingly, Finch 4 Alice doesn't actually communicate directly with a Finch robot.  It relies on the BirdBrain Robot Server to do that.  This simplifies the implementation and reduces the number of dependencies that would need to be patched into Alice.

#### Example project walkthrough

- Start the BirdBrain Robot Server and connect your Finch robot.  The BirdBrain server window should look as follows when it detects a connected Finch:

![The BirdBrain Robot Server with a connected Finch](https://github.com/bradcfisher/finch4alice/images/readme/birdbrain_connected_finch.png "The BirdBrain Robot Server with a connected Finch")

- Launch Alice and when prompted to select a project, create a new project based on the 'GRASS' theme.

![Select Project Dialog](https://github.com/bradcfisher/finch4alice/images/readme/select_project_dialog.png "Select 'GRASS', then click the 'OK' button to create a new project")

- Click `Setup Scene`

- Click `Transport classes`

- Click `Watercraft classes`

- Click `new FishingBoat()`

- Click `Edit Code`

- Drag the `finchSetLED` procedure from the pane on the left into the code window for 'myFirstMethod'
- Select `Custom Whole Number...`, then `0` for the second parameter and `0` for the third parameter.
- Enter `255` when prompted for the custom value for the first parameter.

- Drag the `delay` procedure into 'myFirstMethod'
- Select `2.0` for the number of seconds to delay

- Drag the `finchSetLED` procedure into 'myFirstMethod'
- Select `0` for all three parameters.

- Drag the `say` procedure into 'myFirstMethod'
- Select `Custom TextString...` and enter `All done` when prompted.

- You are now ready to try running your test project.  Click the `Run` button and you should see the LED in the nose of your Finch turn red for two seconds, then turn off.

## Bundled example projects

> More example projects can be found in the [examples](https://github.com/bradcfisher/finch4alice/examples) subfolder.

## How it works

Alice 3 doesn't directly provide an extension mechanism for adding additional functionality such as access to external libraries or jars from within the Alice editor itself.

To work around this, Finch 4 Alice causes the Java runtime to load a custom Jar file into the class path before the other Alice classes are loaded.  Due to the way that the Java class loader works, classes found earlier in the class path will override classes of the same name that are found later (that is, it utilizes a first-come-first-served policy).

The Jar generated by Finch 4 Alice contains a replacement version of the `org.lgna.story.STransport` class within Alice which all "transport" objects (for example Automobile or Boat) are subclasses of.  Not counting inherited methods or properties, the implementation of that class within Alice itself is pretty minimal, consisting of a constructor and a single property.  That simplicity made it an ideal target for adding the additional methods to communicate with a Finch.

One may wonder "Why not add a new type of object within Alice to represent a Finch?"  In one word: Complexity.  Due to how the Alice code is currently structured, adding an additional object type that could be selected from within the UI would have had far-reaching implications in terms of the number of classes that would need to be patched.  In the interests of promoting [Occam's razor](https://en.wikipedia.org/wiki/Occam's_razor) and the principle of simplicity, we chose to take a simpler route.  Specifically, it was easier to reason about what was needed to accomplish the task and get on with playing around with a Finch from within Alice :)

### Additions to the STransport API

Finch 4 Alice works by extending the STransport parent class within Alice.  Several new procedures and functions are added that expose the full range of functionality provided by the Finch robot.

 Check out [the Javadoc API](https://bradcfisher.github.io/finch4alice/api/0.4/) for the full API details.

## Troubleshooting FAQ

*Symptom*: Alice raises an exception when one of the example projects is loaded.

*Cause*: This is most likely caused by trying to load a project into an installation of Alice that has not yet had Finch 4 Alice installed.  The example projects contain references to the new methods added by Finch 4 Alice, and exceptions are raised when those instructions are processed without the Finch 4 Alice code loaded.

---

*Symptom*: On Mac OS X, the Finch responds sluggishly or infrequently when the BirdBrain Robot Server is not the foreground application.

*Cause*: This behavior is usually the result of the "app nap" feature of Mac OS X, which pauses background applications to reduce power usage.  The app nap feature should be disabled for the BirdBrain Robot Server since it must maintain a continuous connection to the Finch, and the pauses introduced by app nap do not allow that.

To disable app nap for the BirdBrain Robot Server:
  - Open the Applications folder in Finder
  - Select the BirdBrain Robot Server application
  - Use Command-I or right-click and select Get Info to open the Info dialog
  - Check "Prevent App Nap" and close the Info dialog

## Contributing

Contributions are very welcome and are accepted through pull requests.  Simply create a [fork on GitHub](https://help.github.com/articles/fork-a-repo/), commit and push your updates to your fork under a new [branch](https://help.github.com/articles/managing-branches-in-your-repository/), and then [submit a pull request](https://help.github.com/articles/using-pull-requests/) for review.

## Bugs and feature requests

Please submit all bug reports and feature requests to the project [issue tracker](https://github.com/bradcfisher/finch4alice/issues/).

## Disclaimers and license

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