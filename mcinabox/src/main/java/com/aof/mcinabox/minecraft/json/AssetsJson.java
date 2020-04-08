package com.aof.mcinabox.minecraft.json;

import java.util.HashMap;

public class AssetsJson {

    public HashMap<String,MinecraftAssetInfo> objects;
    public class MinecraftAssetInfo{
        public String hash;
        public int size;
        public String getHash() { return hash; }
        public void setHash(String hash) { this.hash = hash; }
        public int getSize() { return size; }
        public void setSize(int size) { this.size = size; }
    }
    public HashMap<String, MinecraftAssetInfo> getObjects() { return objects; }
    public void setObjects(HashMap<String, MinecraftAssetInfo> objects) { this.objects = objects; }

}

