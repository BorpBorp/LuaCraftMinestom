package LuaCraft.LuaStom.Addons;

public abstract class LuaStomAddon {
    private String name;
    private String version;

    public final void setName(String name) {
        this.name = name;
    }
    public final void setVersion(String version) {
        this.version = version;
    }

    public final String getName() {
        return name;
    }
    public final String getVersion() {
        return version;
    }

    public abstract void onEnable();
    public void onDisable() {}
}