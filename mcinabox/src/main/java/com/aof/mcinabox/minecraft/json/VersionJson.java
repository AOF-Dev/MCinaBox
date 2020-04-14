package com.aof.mcinabox.minecraft.json;

public class VersionJson {

        //通用参数
    private String id;
    private String mainClass;
    private int minimumLauncherVersion;
    private String releaseTime;
    private String time;
    private String type;
    private String assets;
    private DependentLibrary[] libraries;
    private Download downloads;
    private AssetIndex assetIndex;

        //minimumLauncherVersion < 21
    private String minecraftArguments;

        //minimumLauncherVersion >= 21
    private Arguments arguments;

        //forge
    private String inheritsFrom;


    public class DependentLibrary{
        private String name;
        private Download downloads;
        private String url;
        public class Download{
            private Artifact artifact;
            public class Artifact{
                private String path;
                private String url;
                private String sha1;
                private int size;
                public String getPath() { return path; }
                public void setPath(String path) { this.path = path; }
                public String getUrl() { return url; }
                public void setUrl(String url) { this.url = url; }
                public String getSha1() { return sha1; }
                public void setSha1(String sha1) { this.sha1 = sha1; }
                public int getSize() { return size; }
                public void setSize(int size) { this.size = size; }
            }
            public Artifact getArtifact() { return artifact; }
            public void setArtifact(Artifact artifact) { this.artifact = artifact; }
        }
        public String getUrl() {return url;}
        public void setUrl(String url){this.url = url;}
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public Download getDownloads() { return downloads; }
        public void setDownloads(Download downloads) { this.downloads = downloads; }
    }

    public class Download{
        private Client client;
        private Server server;
        public class Client{
            private int size;
            private String sha1;
            private String url;
            public int getSize() { return size; }
            public void setSize(int size) { this.size = size; }
            public String getSha1() { return sha1; }
            public void setSha1(String sha1) { this.sha1 = sha1; }
            public String getUrl() { return url; }
            public void setUrl(String url) { this.url = url; }
        }
        public class Server extends Client{ }
        public Client getClient() { return client; }
        public void setClient(Client client) { this.client = client; }
        public Server getServer() { return server; }
        public void setServer(Server server) { this.server = server; }
    }

    public class AssetIndex{
        private String id;
        private String sha1;
        private String url;
        private int size;
        private int totalSize;
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getSha1() { return sha1; }
        public void setSha1(String sha1) { this.sha1 = sha1; }
        public String getUrl() { return url; }
        public void setUrl(String url) { this.url = url; }
        public int getSize() { return size; }
        public void setSize(int size) { this.size = size; }
        public int getTotalSize() { return totalSize; }
        public void setTotalSize(int totalSize) { this.totalSize = totalSize; }
    }

    public class Arguments{
        private Object[] game;
        public Object[] getGame() { return game; }
        public void setGame(Object[] game) { this.game = game; }
    }

    public Arguments getArguments() { return arguments; }
    public void setArguments(Arguments arguments) { this.arguments = arguments; }
    public String getInheritsFrom() { return inheritsFrom; }
    public void setInheritsFrom(String inheritsFrom) { this.inheritsFrom = inheritsFrom; }
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getMainClass() { return mainClass; }
    public void setMainClass(String mainClass) { this.mainClass = mainClass; }
    public String getMinecraftArguments() { return minecraftArguments; }
    public void setMinecraftArguments(String minecraftArguments) { this.minecraftArguments = minecraftArguments; }
    public int getMinimumLauncherVersion() { return minimumLauncherVersion; }
    public void setMinimumLauncherVersion(int minimumLauncherVersion) { this.minimumLauncherVersion = minimumLauncherVersion; }
    public String getReleaseTime() { return releaseTime; }
    public void setReleaseTime(String releaseTime) { this.releaseTime = releaseTime; }
    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getAssets() { return assets; }
    public void setAssets(String assets) { this.assets = assets; }
    public DependentLibrary[] getLibraries() { return libraries; }
    public void setLibraries(DependentLibrary[] libraries) { this.libraries = libraries; }
    public Download getDownloads() { return downloads; }
    public void setDownloads(Download downloads) { this.downloads = downloads; }
    public AssetIndex getAssetIndex() { return assetIndex; }
    public void setAssetIndex(AssetIndex assetIndex) { this.assetIndex = assetIndex; }
}
