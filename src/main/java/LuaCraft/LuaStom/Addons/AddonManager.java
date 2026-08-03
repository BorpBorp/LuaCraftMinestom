package LuaCraft.LuaStom.Addons;

import java.io.File;
import java.util.List;

public class AddonManager {
    private static List<LuaStomAddon> addons = List.of();

    public static void loadAll(File jarDir) throws Exception {
        if (!jarDir.exists()) jarDir.mkdirs();
        addons = AddonLoader.loadaddons(jarDir);
    }

    public static List<LuaStomAddon> getAddons() {
        return addons;
    }
}
