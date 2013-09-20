/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package magiccopier;

/**
 *
 * @author abhishekmunie
 */
public class StorageSize implements Comparable<StorageSize> {

    long size;
    StorageSizeUnit unit;

    static enum StorageSizeUnit {

        Bytes,
        KiloBytes,
        MegaBytes,
        GegaBytes,
        TeraBytes;

        public long toBytes() {
            if (this == StorageSizeUnit.TeraBytes) {
                return 1024 * 1024 * 1024 * 1024;
            } else if (this == StorageSizeUnit.GegaBytes) {
                return 1024 * 1024 * 1024;
            } else if (this == StorageSizeUnit.MegaBytes) {
                return 1024 * 1024;
            } else if (this == StorageSizeUnit.KiloBytes) {
                return 1024;
            } else {
                return 1;
            }
        }

        public long toBytes(long size) {
            return size * this.toBytes();
        }

        @Override
        public String toString() {
            if (this == StorageSizeUnit.TeraBytes) {
                return "TB";
            } else if (this == StorageSizeUnit.GegaBytes) {
                return "GB";
            } else if (this == StorageSizeUnit.MegaBytes) {
                return "MB";
            } else if (this == StorageSizeUnit.KiloBytes) {
                return "KB";
            } else if (this == StorageSizeUnit.Bytes) {
                return "B";
            }
            return "";
        }
    }

    public StorageSize() {
        size = 0;
        unit = StorageSizeUnit.Bytes;
    }

    public StorageSize(long size, StorageSizeUnit unit) {
        this.size = size;
        this.unit = unit;
    }

    public StorageSize(long bytes) {
        if (bytes < 1024l) {
            size = bytes;
            unit = StorageSizeUnit.Bytes;
        } else if (bytes < 1024l*1024) {
            size = bytes / 1024l;
            unit = StorageSizeUnit.KiloBytes;
        } else if (bytes < 1024l*1024*1024) {
            size = bytes / (1024l*1024);
            unit = StorageSizeUnit.MegaBytes;
        } else if (bytes < 1024l*1024*1024*1024) {
            size = bytes / (1024l*1024*1024);
            unit = StorageSizeUnit.GegaBytes;
        } else {
            size = bytes / (1024l*1024*1024*1024);
            unit = StorageSizeUnit.TeraBytes;
        }
    }

    public long toBytes() {
        return unit.toBytes(size);
    }

    @Override
    public int compareTo(StorageSize ss) {
        return (int) (this.toBytes() - ss.toBytes());
    }

    @Override
    public String toString() {
        return Long.toString(size)+" "+unit.toString();

    }
}
