package org.lappsgrid.eager.mining.preprocess.pmc

import com.codahale.metrics.Meter
import groovy.util.logging.Slf4j
import org.lappsgrid.eager.core.json.Serializer
import org.lappsgrid.eager.rabbitmq.Message
import org.lappsgrid.serialization.Data
import org.lappsgrid.serialization.DataContainer
import org.lappsgrid.serialization.lif.Annotation
import org.lappsgrid.serialization.lif.Container
import org.lappsgrid.serialization.lif.View
import org.lappsgrid.vocabulary.Features

import static org.lappsgrid.discriminator.Discriminators.*

/**
 *
 */
@Slf4j("logger")
class Processor implements Runnable {

    ExtractText app
    Context context

    Processor(ExtractText app, Context context) {
        this.app = app
        this.context = context
    }

    void run() {
        logger.debug("Processing {}/{}", context.parent, context.filename)
        Container container = context.process()
        if (container == null) {
            app.preprocessorError()
            return
        }
        Data data = new Data(Uri.LIF, container)
        try {
            File lifFile = context.getOutputFile( 'lif', '.lif')
            lifFile.text = data.asJson()
            logger.trace("Wrote {}", lifFile.path)

            File textFile = context.getOutputFile('text', '.txt')
            textFile.text = container.text
            logger.trace("Wrote {}", textFile.path)

            File processedFile = context.getOutputFile("processed", '.txt')
            preprocess(container, processedFile, context.stopwords)
            app.preprocessorMark()
            logger.info("Processed {}/{}", context.parent, context.filename)
        }
        catch(Exception e) {
            logger.error("Unable to process NLP result", e)
            app.preprocessorError()
        }
    }

//    File getOutputFile(File root, String parent, String child, String filename, String suffix) {
//
//        File parentDir = new File(root, parent)
//        if (!parentDir.exists()) {
//            parentDir.mkdir()
//        }
//        File childDir = new File(parentDir, child)
//        if (!childDir.exists()) {
//            childDir.mkdir()
//        }
//        return new File(childDir, filename.replace(".nxml", suffix))
//    }

    /**
     * Preprocess text for input into Word2Vec or Doc2Vec.  Stop words, punctuations, and numbers
     * are removed and words are converted to lowercase.
     *
     * @param container  The LIF container with the input text.
     * @param outputFile Where the output should be written.
     * @param stopwords The list of stop words to be removed.
     */
    void preprocess(Container container, File outputFile, List<String> stopwords) {
        StringWriter writer = new StringWriter()

        List<View> views = container.findViewsThatContain(Uri.TOKEN)
        if (views == null || views.size() == 0) {
            return
        }

        int count = 0
        View view = views[0]
        view.annotations.each { Annotation a ->
            if (Uri.TOKEN == a.atType) {
                String word = a.getFeature(Features.Token.WORD)
                if (word) {
                    word = word.toLowerCase()
                    if (!stopwords.contains(word) && isWord(word)) {
                        ++count
                        writer.print(word)
                        writer.print(' ')
                    }
                }
            }
        }
        outputFile.text = writer.toString()
        logger.trace("Wrote {} words to {}", count, outputFile.path)
    }

    boolean isWord(String word) {
        for (int i = 0; i < word.length(); ++i) {
            if (!Character.isAlphabetic((int)word.charAt(i))) {
                return false
            }
        }
        return true
    }
}
