package dev.rollczi.liteskullapi.standard;

import com.google.common.primitives.Ints;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.bukkit.Bukkit;
import org.intellij.lang.annotations.MagicConstant;

final class Versions {

    private static final Pattern VERSION_PATTERN = Pattern.compile("(?<version>\\d+\\.\\d+)(?<patch>\\.\\d+)?");

    private static final int CURRENT_VERSION = getCurrentVersion();
    public static final int V1_20_1 = 1201;

    public static boolean isSupported(@MagicConstant(valuesFromClass = Versions.class) int version) {
        return CURRENT_VERSION >= version;
    }

    private static int getCurrentVersion() {
        Matcher matcher = VERSION_PATTERN.matcher(Bukkit.getBukkitVersion());
        StringBuilder versionNumber = new StringBuilder();

        if (matcher.find()) {
            String version = matcher.group("version").replace(".", "");
            versionNumber.append(version);

            String maybePatch = matcher.group("patch");
            String path = maybePatch == null ? "0" : maybePatch.replace(".", "");
            versionNumber.append(path);
        }

        Integer version = Ints.tryParse(versionNumber.toString());
        if (version == null) {
            throw new IllegalStateException("Could not parse version: " + Bukkit.getBukkitVersion());
        }

        return version;
    }

}
