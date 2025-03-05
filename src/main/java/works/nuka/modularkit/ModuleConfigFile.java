package works.nuka.modularkit;

import java.net.URLClassLoader;
import java.util.Properties;

public class ModuleConfigFile {
    private final URLClassLoader classLoader;
    private final Properties moduleProperties;

    protected ModuleConfigFile(URLClassLoader classLoader, Properties moduleProperties) {
        this.classLoader = classLoader;
        this.moduleProperties = moduleProperties;
    }

    protected URLClassLoader getClassLoader() {
        return classLoader;
    }

    protected Properties getModuleProperties() {
        return moduleProperties;
    }
}
