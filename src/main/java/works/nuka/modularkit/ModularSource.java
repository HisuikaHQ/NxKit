package works.nuka.modularkit;

import works.nuka.modularkit.events.ModuleStatus;
import works.nuka.modularkit.ex.ModRegisterEx;
import works.nuka.modularkit.ex.ModSourceEx;
import works.nuka.modularkit.ex.ModUuidEx;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

@SuppressWarnings("unused")
public class ModularSource {

    private static final HashMap<String, ModularSource> sourceMap = new HashMap<>();
    private final HashMap<String, ModularModule> moduleMap = new HashMap<>();
    private final String uuid;
    private ModuleManager moduleManager;
    private FileWatcher fileWatcher;

    /**
     * ModularSource - Create a collections of Modules.
     *
     * @param _uuid - UuID of the ModularSource.
     * @throws ModUuidEx Returns a ModUuidEx if the UuiD is incorrect or null.
     */

    public ModularSource(String _uuid) throws ModUuidEx {
        try {
            this.moduleManager = new ModuleManager(this);
        } catch (ModSourceEx modSourceEx) {
            modSourceEx.printStackTrace();
        }

        if (_uuid == null)
            throw new ModUuidEx("uuid cannot be null.");

        if (_uuid.length() != 8)
            throw new ModUuidEx("uuid is incorrect !");

        uuid = _uuid;
        registerSource();
    }

    /**
     * ModularSource - Create a collections of Modules with External ModularModule
     * Repository.
     *
     * @param _uuid - UuID of the ModularSource.
     * @param path  - File Path of the Modules Repository.
     * @throws ModUuidEx - Can return a ModUuidEx if UuID is incorrect or null.
     */
    public ModularSource(String _uuid, File path, String fileExtension) throws ModUuidEx, ModSourceEx, IOException {
        if (_uuid == null)
            throw new ModUuidEx("uuid cannot be null.");

        if (_uuid.length() != 8)
            try {
                throw new ModUuidEx("uuid is incorrect !"); // Grab only the group 1 of the uuid eg :
                // "**f3eafee8**-2419-4a58-b8fe-fe72d6f4019b"
            } catch (Exception e) {
                e.printStackTrace();
            }

        if (fileExtension.isEmpty())
            throw new ModSourceEx("You need to set a custom file extension !");

        uuid = _uuid;
        this.moduleManager = new ModuleManager(this);

        indexModules(path);
        // Finally, register the source !
        registerSource();
    }

    public ModularSource(String _uuid, File path, String fileExtension, boolean isFileWatcherEnabled) throws ModUuidEx, ModSourceEx, IOException {
        this.uuid = _uuid;
        ModularSource source = new ModularSource(_uuid, path, fileExtension);

        if (isFileWatcherEnabled) {
            fileWatcher = new FileWatcher(source, path.toPath(), fileExtension);
        }
    }

    protected ModuleConfigFile getFileModuleConfig(Path file) throws IOException {
        Properties extModRegistrationFile;

        if (file.toFile().isFile()) {
            URLClassLoader classLoader;

            classLoader = new URLClassLoader(
                    new URL[]{new URL("file", null, file.toFile().getAbsolutePath())});

            extModRegistrationFile = new Properties();
            InputStream inModlr = classLoader.getResourceAsStream(".modlr");
            if (inModlr != null) {
                extModRegistrationFile.load(inModlr);
            } else {
                throw new IOException("File .modlr not found !");
            }

            return new ModuleConfigFile(classLoader, extModRegistrationFile);
        }
        return null;
    }

    private ModularModule loadModuleFile(Path file)
            throws IOException,
            ClassNotFoundException,
            ModSourceEx,
            ModUuidEx,
            ModRegisterEx,
            NoSuchMethodException,
            InvocationTargetException,
            InstantiationException,
            IllegalAccessException {
        if (file.toFile().isFile()) {
            ModuleConfigFile configFile = getFileModuleConfig(file);

            for (Map.Entry<Object, Object> entry : configFile.getModuleProperties().entrySet()) {
                Class<?> modClass = Class.forName(entry.getValue().toString(), false, configFile.getClassLoader());

                if (modClass.getSuperclass().getName().equals(ModularModule.class.getName())) {
                    ModularModule newModule = (ModularModule) modClass.getDeclaredConstructor().newInstance();
                    registerModule(newModule);
                    return newModule;
                } else {
                    throw new ModSourceEx("The module doesn't extends to ModularModule.");
                }
            }
        }

        return null;
    }

    private void indexModules(File path) throws IOException {
        if (path.exists() && path.canRead()) {
            Files.walk(path.toPath()).forEach(e -> {
                try {
                    loadModuleFile(e.toFile().toPath());
                } catch (IOException | ClassNotFoundException | ModSourceEx | ModUuidEx | ModRegisterEx |
                         NoSuchMethodException | InvocationTargetException | InstantiationException |
                         IllegalAccessException ex) {
                    throw new RuntimeException(ex);
                }
            });
        }
    }

    /**
     * Processes a single file, loading and registering its modules.
     *
     * @param filePath The path to the file to process.
     */
    public ModularModule processFile(Path filePath, String fileExtension)
            throws ModUuidEx,
            ModRegisterEx,
            IOException,
            ClassNotFoundException,
            ModSourceEx,
            InvocationTargetException,
            NoSuchMethodException,
            InstantiationException,
            IllegalAccessException
    {
        File file = filePath.toFile();
        if (!file.isFile() || !file.getName().endsWith(fileExtension)) {
            return null; // Skip if not a file or doesn't match extension
        }

        return loadModuleFile(file.toPath());
    }

    /**
     * Retrieves the list of modules associated with a file.
     *
     * @param filePath The path to the file.
     * @return The list of modules, or null if none exist.
     */
    protected ModularModule getModuleForFile(Path filePath) throws IOException, IllegalArgumentException, ModUuidEx {
        ModuleConfigFile moduleConfig = getFileModuleConfig(filePath);
        String uuid = moduleConfig.getModuleProperties().toString().replace("{", "").split("=")[0];
        if (uuid == null) throw new IllegalArgumentException("Uuid not found");

        return this.getModuleManager().findModuleByUuiD(uuid);
    }

    /**
     * Find a ModularSource with the source-uuid.
     *
     * @param uuid - Give the Source-UuID.
     * @return - Returns a found ModularSource Object.
     * @throws ModUuidEx - Can returns a ModUuidEx if the uuid is incorrect or null.
     */
    public static ModularSource findSourceByUuiD(String uuid) throws ModUuidEx {
        if (uuid.length() == 8) {
            if (sourceMap.containsKey(uuid))
                return sourceMap.get(uuid);
        } else
            throw new ModUuidEx("uuid is incorrect");
        return null;
    }

    /**
     * (Protected, Internal API) - get the ModularSource sourceMap.
     *
     * @return - Returns a ModularSource HashMap Object.
     */
    protected static HashMap<String, ModularSource> getSourceMap() {
        return sourceMap;
    }

    /**
     * Get a ModularSource unmodifiable-hashmap
     *
     * @return - Return a ModularSource Unmodifioble HashMap.
     */
    public static HashMap<String, ModularSource> getUnmodifiableSourceMap() {
        return (HashMap<String, ModularSource>) Collections.unmodifiableMap(sourceMap);
    }

    private synchronized boolean registerSource() {
        if (!sourceMap.containsKey(uuid)) {
            sourceMap.put(uuid, this);
            return true;
        }
        return false;
    }

    private boolean unregisterSource() {
        if (sourceMap.containsKey(uuid)) {
            if (fileWatcher != null) {
                this.fileWatcher.stop();
            }
            sourceMap.remove(uuid, this);
            return true;
        }
        return false;
    }

    /**
     * Destroys the entire source
     *
     * @param forceDestroy - Force the Destruction.
     * @return - Return true if destroy success.
     * @since 1.0
     */
    public boolean destroy(@Deprecated boolean forceDestroy) {
        for (Map.Entry<String, ModularModule> moduleEntry : moduleMap.entrySet()) {
            moduleEntry.getValue().setModuleStatus(ModuleStatus.STOPPING);
            moduleEntry.getValue().stop();

            if (forceDestroy) {
                try {
                    moduleEntry.getValue().kill(); // Force stop the module
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return unregisterSource();
    }

    /**
     * Registering a module with the ModularModule Object.
     *
     * @param module - Module Object
     * @return - Return true if the module registration will success.
     * @throws ModRegisterEx - Can returns a ModRegisterEx if the Module
     *                       registration will fail.
     * @throws ModUuidEx     - If the uuid is incorrect or uuid is null.
     * @throws ModSourceEx   - Can fail if the ModSource is null.
     * @since 1.0
     */
    public boolean registerModule(ModularModule module) throws ModRegisterEx, ModUuidEx, ModSourceEx {
        if (!moduleMap.containsKey(module.getUuid())) {
            module.setModuleSource(this);
            module.load(); // Call load() hook needed on later versions
            moduleMap.put(module.getUuid(), module);

        } else
            throw new ModRegisterEx("Module already instantiated !");

        return true;
    }

    /**
     * Registering a module with the ModuleClass.
     *
     * @param module Class Object contains a ModularModule Object.
     * @return - Return true if the module registration will success.
     * @throws ModUuidEx              - Can fail if null of incorrect.
     * @throws ModRegisterEx          - Can fail if null of incorrect.
     * @throws ModSourceEx            - Can fail if null of incorrect.
     * @throws InstantiationException - Can return an error if the module is already
     *                                instanced.
     * @throws IllegalAccessException - Can return an Ex if Illegal Access violation
     *                                subsist.
     * @since 1.0
     */
    public boolean registerModule(Class<?> module)
            throws ModUuidEx, ModRegisterEx, ModSourceEx, InstantiationException,
            IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        ModularModule mod = (ModularModule) module.getDeclaredConstructor().newInstance();
        return registerModule(mod);
    }

    /**
     * Unregister a Module quickly
     *
     * @param module - Give the module to unregister.
     * @return Returns true if the operation success.
     * @throws ModRegisterEx - Can fail if the module is null.
     */
    public boolean unregisterModule(ModularModule module) throws ModRegisterEx {
        if (moduleMap.containsKey(module.getUuid())) {
            if (module.getModuleStatus() == ModuleStatus.RUNNING)
                throw new ModRegisterEx("Failed to unregister the module : the module is running.");
            else {
                module.unload(); // Call unload() hook needed on later versions
                moduleMap.remove(module.getUuid(), module);
                return true;
            }
        }

        return false;
    }

    public Map<String, ModularModule> getUnmodifiableModuleMap() {
        return Collections.unmodifiableMap(moduleMap);
    }

    protected Map<String, ModularModule> getModuleMap() {
        return moduleMap;
    }

    public ModuleManager getModuleManager() {
        return moduleManager;
    }

    public String getUuid() {
        return uuid;
    }
}
