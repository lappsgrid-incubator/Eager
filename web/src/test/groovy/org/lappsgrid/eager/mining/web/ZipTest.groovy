package org.lappsgrid.eager.mining.web

import org.junit.Ignore
import org.junit.Test

import java.nio.file.FileSystem
import java.nio.file.FileSystems
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

import static org.junit.Assert.*

/**
 *
 */
@Ignore
class ZipTest {

    @Test
    void zip() {
        File directory = new File("/tmp/eager/work")
        if (!directory.exists()) {
            if (!directory.mkdirs()) {
                fail "Unable to create work directory."
            }
        }



        File zipFile = new File(directory, "example.zip")
        ZipOutputStream zip = new ZipOutputStream(new FileOutputStream(zipFile))
        ZipEntry entry = new ZipEntry('suderman@cs.vassar.edu/TestOne/file1.txt')
        zip.putNextEntry(entry)
        zip.write('Hello world'.bytes)
        zip.closeEntry()

        entry = new ZipEntry('suderman@cs.vassar.edu/TestOne/file2.txt')
        zip.putNextEntry(entry)
        zip.write("Goodbye cruel world. I am leaving you today.".bytes)
        zip.closeEntry()
        zip.close()

//        URI uri = URI.create("jar:file:/tmp/eager/work/example.zip")
//        Path path = Paths.get("/tmp/eager/work/example.zip")

        /*
        File file = new File("/tmp/eager/work/example.zip")
        URI uri = file.toURI()
        URI zipUri = new URI("jar:" + uri.getScheme(), uri.getPath(), null);
        println zipUri.toString()
        Map env = [ create: 'true' ]
        FileSystem fs = FileSystems.newFileSystem(zipUri, env)
        Path zipPath = fs.getPath('suderman@cs.vassar.edu', 'TestOne', 'file1.txt')
        Files.write("Hello world.", zipPath.toFile())
        zipPath = fs.getPath('suderman@cs.vassar.edu', 'TestOne', 'file2.txt')
        Files.write("Goodbye cruel world. I am leaving you today.", zipPath.toFile())
        fs.close()
        */
    }

    @Test
    void unzip() {
        String name = "bb44eb1b-e391-4fb4-88a3-5d96e20112b2.zip"
        File root = new File("/tmp/eager/work")
        File dest = new File("/tmp/eager/output")
        if (!dest.exists()) {
            if (!dest.mkdirs()) {
                fail "Unable to create output directory."
            }
        }

        File zipFile = new File(root, name)
        ZipInputStream zip = new ZipInputStream(new FileInputStream(zipFile))
        ZipEntry entry = zip.getNextEntry()
        while (entry != null) {
            File outfile = new File(dest, entry.name)
            File parentDir = outfile.parentFile
            if (!parentDir.exists()) {
                parentDir.mkdirs()
            }
            FileOutputStream out = new FileOutputStream(outfile)
            byte[] buffer = new byte[1024]
            int len = zip.read(buffer)
            while (len > 0) {
                out.write(buffer, 0, len)
                len = zip.read(buffer)
            }
            out.close()
            entry = zip.getNextEntry()
        }
        zip.close()
    }
}
