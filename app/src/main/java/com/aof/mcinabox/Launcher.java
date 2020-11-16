package com.aof.mcinabox;

import android.os.FileUtils;
import android.util.Log;

import com.aof.mcinabox.model.Account;
import com.aof.mcinabox.model.ArgumentsSubstitutor;
import com.aof.mcinabox.model.CurrentLaunchFeatureMatcher;
import com.aof.mcinabox.model.Profile;
import com.aof.mcinabox.network.gson.DateDeserializer;
import com.aof.mcinabox.network.model.AuthenticationResponse;
import com.aof.mcinabox.network.model.CompatibilityRule;
import com.aof.mcinabox.network.model.ExtractRules;
import com.aof.mcinabox.network.model.Library;
import com.aof.mcinabox.network.model.OperatingSystem;
import com.aof.mcinabox.network.model.Version;
import com.aof.mcinabox.network.model.assets.AssetIndex;
import com.aof.mcinabox.utils.UUIDTypeAdapter;
import com.google.gson.Gson;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class Launcher {
    private static final String TAG = "Launcher";

    private final File gameDirectory;
    private final File assetsDirectory;
    private final int height;
    private final int width;
    private final Gson gson;

    public Launcher(MCinaBox mCinaBox, Version version, int height, int width) {
        this.gameDirectory = mCinaBox.getFileHelper().getGameDirectory();
        this.assetsDirectory = new File(this.gameDirectory, "assets");
        this.height = height;
        this.width = width;
        this.gson = new Gson();
    }

    public void launchGame(Profile profile, Account account, AuthenticationResponse auth) {
        Log.i(TAG, "launchGame: Launching in " + gameDirectory);

        File nativeDirectory = new File(gameDirectory, "versions/" + profile.getVersion().getId() + "/" + profile.getVersion().getId() + "-natives-" + System.nanoTime());
        if (!nativeDirectory.exists()) {
            if (!nativeDirectory.mkdirs()) {
                Log.e(TAG, "launchGame: Aborting launch; couldn't create native directory");
                return;
            }
        } else if (!nativeDirectory.isDirectory()) {
            Log.e(TAG, "launchGame: Aborting launch; native directory is not actually a directory");
            return;
        }
        Log.i(TAG, "launchGame: Unpacking natives to " + nativeDirectory);

        try {
            unpackNatives(profile.getVersion(), nativeDirectory);
        } catch (IOException e) {
            Log.e(TAG, "launchGame: Couldn't unpack natives!", e);
            return;
        }

        File virtualAssetsDirectory;
        try {
            virtualAssetsDirectory = reconstructAssets(profile.getVersion());
        } catch (IOException e) {
            Log.e(TAG, "launchGame: Couldn't unpack natives!", e);
            return;
        }

        File serverResourcePacksDir = new File(gameDirectory, "server-resource-packs");
        if (!serverResourcePacksDir.exists())
            serverResourcePacksDir.mkdirs();

        ArgumentsSubstitutor argumentsSubstitutor = createArgumentsSubstitutor(profile, account, gameDirectory, assetsDirectory, nativeDirectory, virtualAssetsDirectory);
        String[] gameArguments = argumentsSubstitutor.substitute(profile.getVersion().getGameArguments());
        String[] jvmArguments = argumentsSubstitutor.substitute(profile.getVersion().getJvmArguments());
        List<String> tmpJvmArguments = new ArrayList<>(Arrays.asList(jvmArguments));
        String profileArgs = profile.getJavaArgs();
        if (profileArgs != null) {
            tmpJvmArguments.addAll(Arrays.asList(profileArgs.split(" ")));
        } else {
            String defaultArgument = "-Xmx1G -XX:+UnlockExperimentalVMOptions -XX:+UseG1GC -XX:G1NewSizePercent=20 -XX:G1ReservePercent=20 -XX:MaxGCPauseMillis=50 -XX:G1HeapRegionSize=32M -Xmn128M";
            tmpJvmArguments.addAll(Arrays.asList(defaultArgument.split(" ")));
        }
        tmpJvmArguments.add(profile.getVersion().getMainClass());
        jvmArguments = tmpJvmArguments.toArray(new String[0]);

        // Launch game here

        //performCleanups();
    }

    private void unpackNatives(Version version, File targetDir) throws IOException {
        OperatingSystem os = OperatingSystem.LINUX;
        Collection<Library> libraries = version.getRelevantLibraries(new CurrentLaunchFeatureMatcher());
        for (Library library : libraries) {
            Map<OperatingSystem, String> nativesPerOs = library.getNatives();
            if (nativesPerOs != null && nativesPerOs.get(os) != null) {
                File file = new File(gameDirectory, "libraries/" + library.getArtifactPath(nativesPerOs.get(os)));
                try (ZipFile zip = new ZipFile(file)) {
                    ExtractRules extractRules = library.getExtractRules();
                    Enumeration<? extends ZipEntry> entries = zip.entries();
                    while (entries.hasMoreElements()) {
                        ZipEntry entry = entries.nextElement();
                        if (extractRules != null && !extractRules.shouldExtract(entry.getName()))
                            continue;
                        File targetFile = new File(targetDir, entry.getName());
                        if (targetFile.getParentFile() != null)
                            targetFile.getParentFile().mkdirs();
                        if (!entry.isDirectory()) {
                            try (BufferedInputStream inputStream = new BufferedInputStream(zip.getInputStream(entry));
                                 BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(targetFile))) {
                                FileUtils.copy(inputStream, bufferedOutputStream);
                            }
                        }
                    }
                }
            }
        }
    }

    private File reconstructAssets(Version version) throws IOException {
        File indexDir = new File(assetsDirectory, "indexes");
        File objectDir = new File(assetsDirectory, "objects");
        String assetVersion = version.getAssetIndexInfo().getId();
        File indexFile = new File(indexDir, assetVersion + ".json");
        File virtualRoot = new File(new File(assetsDirectory, "virtual"), assetVersion);
        if (!indexFile.isFile()) {
            Log.w(TAG, "reconstructAssets: No assets index file " + virtualRoot + "; can't reconstruct assets");
            return virtualRoot;
        }
        AssetIndex index;
        try (Reader r = new FileReader(indexFile)) {
            index = gson.fromJson(r, AssetIndex.class);
        }
        if (index.isVirtual()) {
            Log.i(TAG, "reconstructAssets: Reconstructing virtual assets folder at " + virtualRoot);
            for (Map.Entry<String, AssetIndex.AssetObject> entry : index.getFileMap().entrySet()) {
                File target = new File(virtualRoot, entry.getKey());
                File original = new File(new File(objectDir, entry.getValue().getHash().substring(0, 2)), entry.getValue().getHash());
                if (!target.isFile())
                    try (InputStream is = new FileInputStream(original);
                         OutputStream os = new FileOutputStream(target)) {
                        FileUtils.copy(is, os);
                    }
            }
            try (OutputStream os = new FileOutputStream(new File(virtualRoot, ".lastused"))) {
                os.write(new DateDeserializer().serializeToString(new Date()).getBytes());
            }
        }
        return virtualRoot;
    }

    private ArgumentsSubstitutor createArgumentsSubstitutor(Profile profile, Account account, File gameDirectory, File assetsDirectory, File nativeDirectory, File virtualAssetsDirectory) {
        Map<String, String> map = new HashMap<>();
        map.put("auth_access_token", account.getAccessToken());
        //map.put("user_property_map", (new GsonBuilder()).registerTypeAdapter(PropertyMap.class, new PropertyMap.Serializer()).create().toJson(authentication.getUserProperties()));
        if (account.isLoggedIn() && account.canPlayOnline()) {
            map.put("auth_session", String.format("token:%s:%s", account.getAccessToken(), UUIDTypeAdapter.fromUUID(account.getSelectedProfile().getId())));
        } else {
            map.put("auth_session", "-");
        }
        map.put("auth_player_name", account.getSelectedProfile().getName());
        map.put("auth_uuid", UUIDTypeAdapter.fromUUID(account.getSelectedProfile().getId()));
        map.put("user_type", "mojang");
        map.put("profile_name", profile.getName());
        map.put("version_name", profile.getVersion().getId());
        map.put("game_directory", gameDirectory.getAbsolutePath());
        map.put("game_assets", virtualAssetsDirectory.getAbsolutePath());
        map.put("assets_root", assetsDirectory.getAbsolutePath());
        map.put("assets_index_name", profile.getVersion().getAssetIndexInfo().getId());
        map.put("version_type", profile.getVersion().getType().getName());
        map.put("resolution_width", String.valueOf(width));
        map.put("resolution_height", String.valueOf(height));
        map.put("language", "en-us");
        try {
            AssetIndex assetIndex = getAssetIndex(profile.getVersion());
            for (Map.Entry<String, AssetIndex.AssetObject> entry : assetIndex.getFileMap().entrySet()) {
                String hash = entry.getValue().getHash();
                String path = new File(new File(assetsDirectory, "objects"), hash.substring(0, 2) + "/" + hash).getAbsolutePath();
                map.put("asset=" + entry.getKey(), path);
            }
        } catch (IOException ignored) {}
        map.put("launcher_name", "MCinaBox");
        map.put("launcher_version", BuildConfig.VERSION_NAME);
        map.put("natives_directory", nativeDirectory.getAbsolutePath());
        map.put("classpath", constructClassPath(profile.getVersion()));
        map.put("classpath_separator", System.getProperty("path.separator"));
        map.put("primary_jar", new File(gameDirectory, "versions/" + profile.getVersion().getId() + "/" + profile.getVersion().getId() + ".jar").getAbsolutePath());
        return new ArgumentsSubstitutor(map);
    }

    private AssetIndex getAssetIndex(Version version) throws IOException {
        String assetVersion = version.getAssetIndexInfo().getId();
        File indexFile = new File(new File(assetsDirectory, "indexes"), assetVersion + ".json");
        try (Reader reader = new FileReader(indexFile)) {
            return gson.fromJson(reader, AssetIndex.class);
        }
    }

    private String constructClassPath(Version version) {
        StringBuilder result = new StringBuilder();
        Collection<File> classPath = version.getClassPath(OperatingSystem.LINUX, gameDirectory, createFeatureMatcher());
        String separator = System.getProperty("path.separator");
        for (File file : classPath) {
            if (!file.isFile())
                throw new RuntimeException("Classpath file not found: " + file);
            if (result.length() > 0)
                result.append(separator);
            result.append(file.getAbsolutePath());
        }
        return result.toString();
    }

    private CompatibilityRule.FeatureMatcher createFeatureMatcher() {
        return new CurrentLaunchFeatureMatcher();
    }

    /*public void cleanupOrphanedVersions() {
        Log.i(TAG, "cleanupOrphanedVersions: Looking for orphaned versions to clean up...");
        Set<String> referencedVersions = new HashSet<>();
        for (Profile profile : getProfileManager().getProfiles().values()) {
            String lastVersionId = profile.getLastVersionId();
            VersionSyncInfo syncInfo = null;
            if (lastVersionId != null)
                syncInfo = getLauncher().getVersionManager().getVersionSyncInfo(lastVersionId);
            if (syncInfo == null || syncInfo.getLatestVersion() == null)
                syncInfo = getLauncher().getVersionManager().getVersions(profile.getVersionFilter()).get(0);
            if (syncInfo != null) {
                Version version = syncInfo.getLatestVersion();
                referencedVersions.add(version.getId());
                if (version instanceof CompleteMinecraftVersion) {
                    Version completeMinecraftVersion = version;
                    referencedVersions.add(completeMinecraftVersion.getInheritsFrom());
                    referencedVersions.add(completeMinecraftVersion.getJar());
                }
            }
        }
        Calendar calendar = Calendar.getInstance();
        calendar.add(5, -7);
        Date cutoff = calendar.getTime();
        for (VersionSyncInfo versionSyncInfo : getLauncher().getVersionManager().getInstalledVersions()) {
            if (versionSyncInfo.getLocalVersion() instanceof CompleteMinecraftVersion) {
                CompleteVersion version = (CompleteVersion)versionSyncInfo.getLocalVersion();
                if (!referencedVersions.contains(version.getId()) && version.getType() == MinecraftReleaseType.SNAPSHOT) {
                    if (versionSyncInfo.isOnRemote()) {
                        LOGGER.info("Deleting orphaned version {} because it's a snapshot available on remote", new Object[] { version.getId() });
                        try {
                            getLauncher().getVersionManager().uninstallVersion(version);
                        } catch (IOException e) {
                            LOGGER.warn("Couldn't uninstall version " + version.getId(), e);
                        }
                        continue;
                    }
                    if (version.getUpdatedTime().before(cutoff)) {
                        LOGGER.info("Deleting orphaned version {} because it's an unsupported old snapshot", new Object[] { version.getId() });
                        try {
                            getLauncher().getVersionManager().uninstallVersion(version);
                        } catch (IOException e) {
                            LOGGER.warn("Couldn't uninstall version " + version.getId(), e);
                        }
                    }
                }
            }
        }
    }

    public void cleanupOrphanedAssets() {
        File assetsDir = new File(gameDirectory, "assets");
        File indexDir = new File(assetsDir, "indexes");
        File objectsDir = new File(assetsDir, "objects");
        Set<String> referencedObjects = Sets.newHashSet();
        if (!objectsDir.isDirectory())
            return;
        for (VersionSyncInfo syncInfo : getLauncher().getVersionManager().getInstalledVersions()) {
            if (syncInfo.getLocalVersion() instanceof CompleteMinecraftVersion) {
                CompleteMinecraftVersion version = (CompleteMinecraftVersion)syncInfo.getLocalVersion();
                String assetVersion = version.getAssetIndex().getId();
                File indexFile = new File(indexDir, assetVersion + ".json");
                AssetIndex index = (AssetIndex)this.gson.fromJson(FileUtils.readFileToString(indexFile, Charsets.UTF_8), AssetIndex.class);
                for (AssetIndex.AssetObject object : index.getUniqueObjects().keySet())
                    referencedObjects.add(object.getHash().toLowerCase());
            }
        }
        File[] directories = objectsDir.listFiles((FileFilter)DirectoryFileFilter.DIRECTORY);
        if (directories != null)
            for (File directory : directories) {
                File[] files = directory.listFiles((FileFilter)FileFileFilter.FILE);
                if (files != null)
                    for (File file : files) {
                        if (!referencedObjects.contains(file.getName().toLowerCase())) {
                            LOGGER.info("Cleaning up orphaned object {}", new Object[] { file.getName() });
                            FileUtils.deleteQuietly(file);
                        }
                    }
            }
        deleteEmptyDirectories(objectsDir);
    }

    public void cleanupOldSkins() {
        File assetsDir = new File(getLauncher().getWorkingDirectory(), "assets");
        File skinsDir = new File(assetsDir, "skins");
        if (!skinsDir.isDirectory())
            return;
        Collection<File> files = FileUtils.listFiles(skinsDir, (IOFileFilter)new AgeFileFilter(System.currentTimeMillis() - 604800000L), TrueFileFilter.TRUE);
        if (files != null)
            for (File file : files) {
                LOGGER.info("Cleaning up old skin {}", new Object[] { file.getName() });
                FileUtils.deleteQuietly(file);
            }
        deleteEmptyDirectories(skinsDir);
    }

    public void cleanupOldNatives() {
        File root = new File(this.launcher.getWorkingDirectory(), "versions/");
        LOGGER.info("Looking for old natives & assets to clean up...");
        AgeFileFilter ageFileFilter = new AgeFileFilter(System.currentTimeMillis() - 3600000L);
        if (!root.isDirectory())
            return;
        File[] versions = root.listFiles((FileFilter)DirectoryFileFilter.DIRECTORY);
        if (versions != null)
            for (File version : versions) {
                File[] files = version.listFiles((FileFilter)FileFilterUtils.and(new IOFileFilter[] { (IOFileFilter)new PrefixFileFilter(version.getName() + "-natives-"), (IOFileFilter)ageFileFilter }));
                if (files != null)
                    for (File folder : files) {
                        LOGGER.debug("Deleting " + folder);
                        FileUtils.deleteQuietly(folder);
                    }
            }
    }

    public void cleanupOldVirtuals() {
        File assetsDir = new File(getLauncher().getWorkingDirectory(), "assets");
        File virtualsDir = new File(assetsDir, "virtual");
        DateTypeAdapter dateAdapter = new DateTypeAdapter();
        Calendar calendar = Calendar.getInstance();
        calendar.add(5, -5);
        Date cutoff = calendar.getTime();
        if (!virtualsDir.isDirectory())
            return;
        File[] directories = virtualsDir.listFiles((FileFilter)DirectoryFileFilter.DIRECTORY);
        if (directories != null)
            for (File directory : directories) {
                File lastUsedFile = new File(directory, ".lastused");
                if (lastUsedFile.isFile()) {
                    Date lastUsed = dateAdapter.deserializeToDate(FileUtils.readFileToString(lastUsedFile));
                    if (cutoff.after(lastUsed)) {
                        LOGGER.info("Cleaning up old virtual directory {}", new Object[] { directory });
                        FileUtils.deleteQuietly(directory);
                    }
                } else {
                    LOGGER.info("Cleaning up strange virtual directory {}", new Object[] { directory });
                    FileUtils.deleteQuietly(directory);
                }
            }
        deleteEmptyDirectories(virtualsDir);
    }

    public void performCleanups() {
        cleanupOrphanedVersions();
        cleanupOrphanedAssets();
        cleanupOldSkins();
        cleanupOldNatives();
        cleanupOldVirtuals();
    }*/
}
