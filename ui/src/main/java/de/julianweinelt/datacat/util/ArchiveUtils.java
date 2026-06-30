package de.julianweinelt.datacat.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Slf4j
public class ArchiveUtils {
    public static void unzip(File zipFile, File destDir) throws IOException {
        if (!destDir.exists()) {
            if (destDir.mkdirs()) log.debug("Created destination directory: {}", destDir.getPath());
        }
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                File outFile = new File(destDir, entry.getName());
                if (entry.isDirectory()) {
                    if (outFile.mkdirs()) log.debug("Created directory: {}", outFile.getPath());
                } else {
                    if (outFile.getParentFile().mkdirs()) log.debug("Created parent directory: {}", outFile.getParentFile().getPath());
                    try (FileOutputStream fos = new FileOutputStream(outFile)) {
                        byte[] buffer = new byte[4096];
                        int len;
                        while ((len = zis.read(buffer)) != -1) {
                            fos.write(buffer, 0, len);
                        }
                    }
                }
            }
        }
    }

    public static void untarGz(File tarGzFile, File destDir) throws IOException {
        if (!destDir.exists()) {
            if (destDir.mkdirs()) log.debug("Created destination directory: {}", destDir.getPath());
        }

        try (FileInputStream fis = new FileInputStream(tarGzFile);
             BufferedInputStream bis = new BufferedInputStream(fis);
             GzipCompressorInputStream gzis = new GzipCompressorInputStream(bis);
             TarArchiveInputStream tis = new TarArchiveInputStream(gzis)) {

            TarArchiveEntry entry;
            while ((entry = tis.getNextEntry()) != null) {
                File outFile = new File(destDir, entry.getName());
                if (entry.isDirectory()) {
                    if (outFile.mkdirs()) log.debug("Created directory: {}", outFile.getPath());
                } else {
                    if (outFile.getParentFile().mkdirs()) log.debug("Created parent directory: {}", outFile.getParentFile().getPath());
                    try (FileOutputStream fos = new FileOutputStream(outFile)) {
                        byte[] buffer = new byte[4096];
                        int len;
                        while ((len = tis.read(buffer)) != -1) {
                            fos.write(buffer, 0, len);
                        }
                    }
                }
            }
        }
    }
}
