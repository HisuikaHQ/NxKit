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

    @Override
    public ModuleConfigModel serialize(String json) {
        ModuleConfigModel model = (ModuleConfigModel) super.serialize(json);
        this.setName(model.name);
        this.setUuid(model.uuid);
        this.setMainClass(model.mainClass);
        this.setAuthor(model.author);
        this.setDescription(model.description);
        this.setVersion(model.version);
        return this;
    }

    public String getName() {
        return name;
    }

    public String getUuid() {
        return uuid;
    }

    public String getMainClass() {
        return mainClass;
    }

    public String getAuthor() {
        return author;
    }

    public String getDescription() {
        return description;
    }

    public String getVersion() {
        return version;
    }

    protected ModuleConfigModel setName(String name) {
        this.name = name;
        return this;
    }

    protected ModuleConfigModel setUuid(String uuid) {
        this.uuid = uuid;
        return this;
    }

    protected ModuleConfigModel setMainClass(String mainClass) {
        this.mainClass = mainClass;
        return this;
    }

    protected ModuleConfigModel setAuthor(String author) {
        this.author = author;
        return this;
    }

    protected ModuleConfigModel setDescription(String description) {
        this.description = description;
        return this;
    }

    protected ModuleConfigModel setVersion(String version) {
        this.version = version;
        return this;
    }
}

