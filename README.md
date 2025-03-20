
![Maven Central](https://img.shields.io/maven-central/v/works.nuka/ModularKit)


# ModularKit

## What is ModularKit ?

**ModularKit**, an alternative to the OSGi framework, more lightweight and powerful Java toolkit for creating
modular apps.

With ModularKit, you can create modular apps with increased flexibility and
stability, designed to create projects without server shutdown and a good
maintainability.

## Getting started

First, you need to create a new project (maven recommended) and add **ModularKit** dependency :
or you can clone this project: [https://git.nuka.works/nukaworks/toolkits/ModuleTest](https://git.nuka.works/nukaworks/toolkits/ModuleTest)
### To implement ModularKit in your app (Client side):

```xml
<dependency>
    <groupId>works.nuka</groupId>
    <artifactId>ModularKit</artifactId>
    <version>{ Check maven-central badge }</version>
</dependency>
```

### For create your module (Module side):

#### Create manualy

```xml
<dependency>
    <groupId>works.nuka</groupId>
    <artifactId>ModularKit</artifactId>
    <version>{ Check maven-central badge }</version>
    <scope>provided</scope>
</dependency>
```

## Examples

### hello-world

```java
package com.example;

import works.nuka.modularkit.ModularModule;

public class ModuleTest extends ModularModule {

    public ModuleTest() throws Exception {
        super();
    }

    @Override
    protected void start() {
        System.out.println("Hello " + this.getModuleConfig().getName() + " !");
    }

    @Override
    protected void stop() {
        System.out.println("Goodbye " + this.getModuleConfig().getName() + " !");
    }

    @Override
    protected void load() {
        System.out.println("Loaded " + this.getModuleConfig().getName() + " !");

    }

    @Override
    protected void unload() {
        System.out.println("Unloaded " + this.getModuleConfig().getName() + " !");

    }
}
```

### For registering and run a module

For running an internal module, let's try this little example :

```java
import works.nuka.modularkit.ModularSource;
import ModuleTest;

public class Main {
    public static void main() {
        // Register the source first
        ModularSource mainSource = new ModularSource("e3640e55");

        // and the Module ...
        ModuleTest testMod = new ModuleTest();

        // ... and then let's run it !
        mainSource.getModuleManager().runModule(testMod);
    }
}
```

For sources and modules you need to generate a new uuid (Group 1).

#### MacOS/Linux and others unix systems

```shell
~$ uuidgen
**e3640e55**-cbaf-42c2-b053-52a2cfa2e0a5
~$
```

To run external modules (.jar), checkout [CoffeeLoader](https://git.nuka.works/nukaworks/servers/CoffeeLoader) or proceed:

Fork [ModuleTest](https://git.nuka.works/nukaworks/toolkits/ModuleTest) and edit modules.json under the resources folder:
```json
{
  "name": "ModuleTest",
  "uuid": "758ad8e8",
  "mainClass": "com.example.ModuleTest",
  "author": "Powerm1nt",
  "description": "HelloWorld Module",
  "version": "1.0.0"
}
```

In your app, implement an external source:
```java
// ...
    
ModularSource src = new ModularSource(generateLittleUuid(), sourceDir, ".jar", config.fileWatcher);
    
// ...
```

#### UUIDGEN Online

You can use **https://www.uuidgenerator.net/**

⚠ **You need to copy the group 1 of the uuid.**

### Need more?
Go on our **[developer documentation center](https://developer.nuka.works/)** and checkout **ModularKit**, you can find more
documentation, code example and support.
