package works.nuka.modularkit;

import com.google.gson.annotations.SerializedName;

public class ModuleConfigModel extends BaseModel {
    @SerializedName("name")
    private String name;
    @SerializedName("uuid")
    private String uuid;
    @SerializedName("mainClass")
    private String mainClass;
    @SerializedName("author")
    private String author;
    @SerializedName("description")
    private String description;
    @SerializedName("version")
    private String version;

    protected String getName() {
        return name;
    }

    protected String getUuid() {
        return uuid;
    }

    protected String getMainClass() {
        return mainClass;
    }

    protected String getAuthor() {
        return author;
    }

    protected String getDescription() {
        return description;
    }

    protected String getVersion() {
        return version;
    }

    public ModuleConfigModel setName(String name) {
        this.name = name;
        return this;
    }

    public ModuleConfigModel setUuid(String uuid) {
        this.uuid = uuid;
        return this;
    }

    public ModuleConfigModel setMainClass(String mainClass) {
        this.mainClass = mainClass;
        return this;
    }

    public ModuleConfigModel setAuthor(String author) {
        this.author = author;
        return this;
    }

    public ModuleConfigModel setDescription(String description) {
        this.description = description;
        return this;
    }

    public ModuleConfigModel setVersion(String version) {
        this.version = version;
        return this;
    }
}

