package LuaCraft.LuaStom.Addons;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AddonLoader {
    private static final Logger logger = LoggerFactory.getLogger("LuaCraft AddonLoader");

    public static List<LuaStomAddon> loadaddons(File jarDir) throws Exception {
        File[] jars = jarDir.listFiles((dir, name) -> name.endsWith(".jar"));
        if (jars == null) return List.of();

        List<LuaStomAddon> addons = new ArrayList<>();
        for (File jar : jars) {
            URLClassLoader loader = new URLClassLoader(
                new URL[]{ jar.toURI().toURL()},
                AddonLoader.class.getClassLoader()
            );

            try (JarFile jarFile = new JarFile(jar)) {
                Manifest manifest = jarFile.getManifest();
                String mainClass = manifest.getMainAttributes().getValue("Addon-Main");
                String addonName = manifest.getMainAttributes().getValue("Addon-Name");
                String addonVersion = manifest.getMainAttributes().getValue("Addon-Version");

                if (mainClass == null) continue;

                Class<?> clazz = Class.forName(mainClass, true, loader);
                LuaStomAddon addon = (LuaStomAddon) clazz.getDeclaredConstructor().newInstance();

                addon.setName(addonName != null ? addonName : mainClass);
                addon.setVersion(addonVersion);
                logger.info("Successfully loaded: " + addonName + " " + addonVersion);
                addon.onEnable();
                addons.add(addon);
            }
        }

        return addons;
    }
}
