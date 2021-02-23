package com.aof.mcinabox.network.model.assets;

import com.google.common.collect.Maps;

import java.util.LinkedHashMap;
import java.util.Map;

public class AssetIndex {
    public static final String DEFAULT_ASSET_NAME = "legacy";

    private final Map<String, AssetObject> objects = new LinkedHashMap<>();

    private boolean virtual;

    public Map<String, AssetObject> getFileMap() {
        return this.objects;
    }

    public Map<AssetObject, String> getUniqueObjects() {
        Map<AssetObject, String> result = Maps.newHashMap();
        for (Map.Entry<String, AssetObject> objectEntry : this.objects.entrySet())
            result.put(objectEntry.getValue(), objectEntry.getKey());
        return result;
    }

    public boolean isVirtual() {
        return this.virtual;
    }

    public class AssetObject {
        private String hash;

        private long size;

        private boolean reconstruct;

        private String compressedHash;

        private long compressedSize;

        public String getHash() {
            return this.hash;
        }

        public long getSize() {
            return this.size;
        }

        public boolean shouldReconstruct() {
            return this.reconstruct;
        }

        public boolean hasCompressedAlternative() {
            return (this.compressedHash != null);
        }

        public String getCompressedHash() {
            return this.compressedHash;
        }

        public long getCompressedSize() {
            return this.compressedSize;
        }

        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (o == null || getClass() != o.getClass())
                return false;
            AssetObject that = (AssetObject) o;
            if (this.compressedSize != that.compressedSize)
                return false;
            if (this.reconstruct != that.reconstruct)
                return false;
            if (this.size != that.size)
                return false;
            if ((this.compressedHash != null) ? !this.compressedHash.equals(that.compressedHash) : (that.compressedHash != null))
                return false;
          return (this.hash != null) ? this.hash.equals(that.hash) : (that.hash == null);
        }

        public int hashCode() {
            int result = (this.hash != null) ? this.hash.hashCode() : 0;
            result = 31 * result + (int) (this.size ^ this.size >>> 32L);
            result = 31 * result + (this.reconstruct ? 1 : 0);
            result = 31 * result + ((this.compressedHash != null) ? this.compressedHash.hashCode() : 0);
            result = 31 * result + (int) (this.compressedSize ^ this.compressedSize >>> 32L);
            return result;
        }
    }
}
