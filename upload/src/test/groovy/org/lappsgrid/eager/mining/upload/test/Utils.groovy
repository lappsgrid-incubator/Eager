package org.lappsgrid.eager.mining.upload.test

import org.junit.Test
import org.lappsgrid.discriminator.Discriminators
import org.lappsgrid.eager.rabbitmq.topic.PostOffice
import org.lappsgrid.serialization.Data
import org.lappsgrid.serialization.lif.Container

import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

/**
 *
 */
class Utils {

    @Test
    void uploadZip() {
        PostOffice po = new PostOffice('galaxy.upload.service')
        po.send('zip', readZip('/tmp/eager/work/b1950bed-f7a1-46cb-b708-b7dbec931a6f.zip'))
        po.close()
        println "Sent zip to galaxy.upload.service"
    }

    byte[] readZip(String path) {
        return new File(path).bytes
    }

    byte[] generateZip() {
        ByteArrayOutputStream out = new ByteArrayOutputStream()
        ZipOutputStream zip = new ZipOutputStream(out)
        ZipEntry entry = new ZipEntry('suderman@cs.vassar.edu/upload/File1.lif')
        zip.putNextEntry(entry)
        zip.write(getData('Goodbye cruel world. I am leaving you today'))
        zip.closeEntry()

        entry = new ZipEntry('suderman@cs.vassar.edu/upload/File2.lif')
        zip.putNextEntry(entry)
        zip.write(getData('Karen flew to New York. Nancy flew to Blommington.'))
        zip.closeEntry()
        zip.close()
        return out.toByteArray()
    }

    byte[] getData(String message) {
        Container container = new Container()
        container.text = message
        container.language = 'en'
        return new Data(Discriminators.Uri.LIF, container).asPrettyJson().bytes
    }
}
