package works.nuka.modularkit;

import works.nuka.modularkit.ex.ModRegisterEx;
import works.nuka.modularkit.ex.ModUuidEx;

public class AnotherModule extends ModularModule {
    public AnotherModule() throws ModUuidEx, ModRegisterEx {
        ModuleConfigModel configModel = new ModuleConfigModel();
        configModel.setName("AnotherModule");
        configModel.setUuid("452457c5");
        configModel.setVersion("1.0.0");
        configModel.setDescription("AnotherModule Test module");
        configModel.setAuthor("Powerm1nt");
        configModel.setVersion("1.0.0");

        super(configModel);
    }

    @Override
    public void stop() {
        System.out.println("Bye " + this.getModuleConfig().getName() + " !");
    }


    @Override
    public void start() {
        System.out.println("Hello " + this.getModuleConfig().getName() + " !");
    }

    @Override
    protected void load() {

    }

    @Override
    protected void unload() {

    }
}
