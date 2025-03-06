package works.nuka.modularkit;

import works.nuka.modularkit.events.ModuleStatus;
import works.nuka.modularkit.ex.*;

@SuppressWarnings("unused")

public abstract class ModularModule {
    private final ModuleConfigModel moduleConfig;
    private ModuleStatus modStatus = ModuleStatus.STOPPED; // Default module execution status.
    private ModularSource modSource;
    private final ModularModule[] moduleDependencies;

    // Thread naming conventions : Mod_$name#$dynUuid_$uuid
    private String threadName;
    private Thread modThread;

    /**
     * The ModularModule Module Object, the fabulous "ModularModule" !
     *
     * @param moduleConfig - Pass the module configuration object.
     * @param modDeps - (Optional) Add Module Dependencies.
     * @throws ModUuidEx - Can return a ModUuidEx if the uuid is incorrect or null.
     * @since 1.0
     */

    public ModularModule(
            ModuleConfigModel moduleConfig,
            ModularModule... modDeps
    ) throws ModUuidEx, ModRegisterEx {
        this.moduleDependencies = modDeps;
        this.moduleConfig = moduleConfig;

        if (moduleConfig.getUuid() == null)
            throw new ModUuidEx("uuid cannot be null.");
        else if (moduleConfig.getUuid().length() != 8)
            throw new ModUuidEx("uuid is incorrect !");

        if (moduleConfig.getName().isEmpty())
            throw new ModRegisterEx("Module name is empty");
    }

    protected void setModuleSource(ModularSource source) throws ModSourceEx, ModUuidEx {
        if (source != null)
            modSource = source;
        else
            throw new ModSourceEx("ModSource cannot be null !");

        if (modSource.getModuleManager().findModuleByUUID(this.moduleConfig.getUuid()) != null)
            throw new ModUuidEx("Module already instantiated !");

        if (this.moduleDependencies != null && this.moduleDependencies.length > 0)
            getModSource().getModuleManager().setDepends(this, this.moduleDependencies);
    }

    protected void exec() {
        modStatus = ModuleStatus.RUNNING;
        modThread = Thread.currentThread();
        threadName = modThread.getName();

        start();
    }

    /**
     * Stop the module
     */
    protected abstract void stop();

    protected abstract void start();

    protected abstract void load();

    protected abstract void unload();

    @SuppressWarnings("deprecation") // Because modThread.stop() is deprecated.
    protected void kill() throws ModRunEx {
        if (modStatus != ModuleStatus.STOPPING)
            throw new ModRunEx("Please try with stop() before call kill() !");
        modThread.stop();
    }

    public String getThreadName() {
        return threadName;
    }

    public ModuleStatus getModuleStatus() {
        return modStatus;
    }

    protected void setModuleStatus(ModuleStatus modStatus) {
        this.modStatus = modStatus;
    }

    private ModularSource getModSource() {
        return modSource;
    }

    public ModuleConfigModel getModuleConfig() {
        return this.moduleConfig;
    }
}
