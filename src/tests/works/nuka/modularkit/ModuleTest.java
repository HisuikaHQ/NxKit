package works.nuka.modularkit;

import works.nuka.modularkit.ex.ModRegisterEx;
import works.nuka.modularkit.ex.ModUuidEx;

public class ModuleTest extends ModularModule {

    public ModuleTest() throws ModUuidEx, ModRegisterEx {
        ModuleConfigModel configModel = new ModuleConfigModel();
        configModel.setName("ModuleTest");
        configModel.setUuid("81f9ab59");
        configModel.setVersion("1.0.0");
        configModel.setDescription("ModuleTest Test module");
        configModel.setAuthor("Powerm1nt");
        configModel.setVersion("1.0.0");
        super(configModel, new AnotherModule());
    }

    @Override
    protected void stop() {
        System.out.println("Bye " + this.getModuleConfig().getName() + " !");
    }

    @Override
    protected void start() {
        System.out.println("Hello " + this.getModuleConfig().getName() + " !");
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void load() {

    }

    @Override
    protected void unload() {

    }
}
