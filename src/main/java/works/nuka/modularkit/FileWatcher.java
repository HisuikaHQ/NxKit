package works.nuka.modularkit;

import org.jetbrains.annotations.NotNull;
import works.nuka.modularkit.ex.ModRegisterEx;
import works.nuka.modularkit.ex.ModSourceEx;
import works.nuka.modularkit.ex.ModUuidEx;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class FileWatcher {
    private final WatchService watchService;
    private final Thread watcherThread;
    private final ModularSource modularSource;
    private final Path path;
    private final String fileExtension;
    private final Map<Path, String> filePathsMap = new HashMap<>();

    /**
     * Constructor to initialize the FileWatcher.
     *
     * @param modularSource The ModularSource instance to interact with.
     * @param path          The directory path to watch.
     * @param fileExtension The file extension needed to be watched
     * @throws IOException If the WatchService cannot be created or directories cannot be registered.
     */
    protected FileWatcher(ModularSource modularSource, Path path, String fileExtension) throws IOException {
        this.modularSource = modularSource;
        this.path = path;
        this.fileExtension = fileExtension;
        this.watchService = FileSystems.getDefault().newWatchService();
        registerDirectories();
        this.watcherThread = new Thread(() -> {
            try {
                this.watch();
            } catch (ModUuidEx | ModRegisterEx | IOException | ClassNotFoundException | ModSourceEx |
                     InvocationTargetException | NoSuchMethodException | InstantiationException |
                     IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        });
        this.watcherThread.start();
    }

    /**
     * Registers all directories under the given path with the WatchService.
     */
    private void registerDirectories() throws IOException {
        Files.walk(path)
                .filter(Files::isDirectory)
                .forEach(dir -> {
                    try {
                        dir.register(watchService,
                                StandardWatchEventKinds.ENTRY_CREATE,
                                StandardWatchEventKinds.ENTRY_MODIFY,
                                StandardWatchEventKinds.ENTRY_DELETE);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
    }

    /**
     * Watches for file system events and processes them.
     */
    private void watch()
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
        try (Stream<@NotNull Path> paths = Files.list(this.path)) {
            paths.forEach(filePath -> {
                ModularModule module;
                try {
                    module = modularSource.getModuleForFile(filePath);
                } catch (IOException | ModUuidEx e) {
                    throw new RuntimeException(e);
                }

                filePathsMap.put(filePath, module.getUuid());
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        while (true) {
            WatchKey key;

            // Get the next watch key (blocks until an event occurs)
            try {
                key = watchService.take();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }

            // Process all events for the key
            for (WatchEvent<?> event : key.pollEvents()) {
                WatchEvent.Kind<?> kind = event.kind();

                // Skip overflow events
                if (kind == StandardWatchEventKinds.OVERFLOW) {
                    continue;
                }

                // Get the path of the changed file and the full path
                Path changed = (Path) event.context();
                Path dir = (Path) key.watchable();
                Path fullPath = dir.resolve(changed);

                // Handle different event types with if-else
                if (kind == StandardWatchEventKinds.ENTRY_CREATE) {
                    // File or directory created
                    if (Files.isDirectory(fullPath)) {
                        try {
                            // Register the new directory for watching
                            fullPath.register(watchService,
                                    StandardWatchEventKinds.ENTRY_CREATE,
                                    StandardWatchEventKinds.ENTRY_MODIFY,
                                    StandardWatchEventKinds.ENTRY_DELETE);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else if (Files.isRegularFile(fullPath)) {
                        // Process the new file
                        ModularModule module = modularSource.processFile(fullPath, fileExtension);
                        if (!filePathsMap.containsKey(fullPath)) {
                            filePathsMap.put(fullPath, module.getUuid());
                        }
                    }
                } else if (kind == StandardWatchEventKinds.ENTRY_MODIFY) {
                    // File modified
                    if (Files.isRegularFile(fullPath)) {
                        ModularModule module = modularSource.getModuleForFile(fullPath);
                        if (module != null) {
                            try {
                                // TODO: Restart the module also
                                if (modularSource.unregisterModule(module)) {
                                    modularSource.registerModule(module);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    modularSource.processFile(fullPath, fileExtension);
                } else if (kind == StandardWatchEventKinds.ENTRY_DELETE) {
                    // File deleted
                    ModularModule module = modularSource.getModuleManager().findModuleByUuiD(filePathsMap.get(fullPath));
                    if (module != null) {
                        try {
                            modularSource.unregisterModule(module);
                            filePathsMap.remove(fullPath);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            // Reset the key and check if it’s still valid
            boolean valid = key.reset();
            if (!valid) {
                break; // Exit if the key is no longer valid (e.g., directory deleted)
            }
        }
    }

    /**
     * Stops the file watcher and cleans up resources.
     */
    public void stop() {
        watcherThread.interrupt();
        try {
            watchService.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
