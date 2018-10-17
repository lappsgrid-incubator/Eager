package org.lappsgrid.eager.mining.index

import org.lappsgrid.eager.core.Configuration
import org.lappsgrid.eager.rabbitmq.Message
import org.lappsgrid.eager.rabbitmq.topic.PostOffice

/**
 *
 */
class FileLoader implements Runnable {

    Configuration conf
    List<String> index

    FileLoader() {
        conf = new Configuration()

        index = new ArrayList<>()
        InputStream stream = this.class.getResourceAsStream("/pmc-index.txt")
        stream.eachLine { String line ->
            index.add(line)
        }
    }

    void run() {
        println "FileLoder starting"
        Random random = new Random()
        PostOffice po = new PostOffice(conf.POSTOFFICE)
        5.times { i ->
//            int i = random.nextInt(index.size())
            String path = index[i]
            println "Requesting $path"
            Message message = new Message()
                .command("load")
                .body(path)
                .route(conf.BOX_LOAD, SectionParser.MBOX)
            po.send(message)
        }
        po.close()
    }
}
