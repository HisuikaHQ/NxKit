package works.nuka.modularkit;

import java.net.URLClassLoader;

public class ModuleConfigFile {
    private final URLClassLoader classLoader;
    private final ModuleConfigModel moduleConfig;

    protected ModuleConfigFile(URLClassLoader classLoader, ModuleConfigModel moduleConfig) {
        this.classLoader = classLoader;
        this.moduleConfig = moduleConfig;
    }

    protected URLClassLoader getClassLoader() {
        return classLoader;
    }

    protected ModuleConfigModel getModuleConfig() {
        return moduleConfig;
    }
}
