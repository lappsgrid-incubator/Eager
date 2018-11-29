package org.lappsgrid.eager.mining.preprocess.pmc

import groovy.util.logging.Slf4j
import org.apache.commons.pool2.ObjectPool
import org.apache.commons.pool2.impl.GenericObjectPool
import org.lappsgrid.eager.mining.preprocess.pool.ParserFactory
import org.lappsgrid.serialization.lif.Container

/**
 *
 */
@Slf4j("logger")
class Context {
    String text
    List<String> stopwords
    String directory
    String parent
    String filename
    Pipeline pipeline
    ObjectPool<Parser> parsers

//    Context(int poolSize) {
//        parsers = new GenericObjectPool<>(new ParserFactory())
//        parsers.setBlockWhenExhausted(true)
//        parsers.setMaxTotal(poolSize)
//        pipeline = new Pipeline()
//    }

//    Context(Pipeline pipeline, ObjectPool<Parser> parsers) {
//        this.pipeline = pipeline
//        this.parsers = parsers
//    }
    Context() { }

    Parser getParser() {
        return parsers.borrowObject()
    }

    void returnParser(Parser parser) {
        parsers.returnObject(parser)
    }

    String parse(String xml) {
        Parser parser = parsers.borrowObject()
        String result = parser.parse(xml)
        parsers.returnObject(parser)
        return result
    }

    Container nlp(String text) {
        return pipeline.process(text)
    }

    Container process() {
        Container container = null
        try {
            logger.trace("{} : Parsing text", this.filename)
            String raw = parse(text)
            logger.trace("{} : Stanford NLP", this.filename)
            container = pipeline.process(raw)
        }
        catch (Exception e) {
            logger.error("Error processing document", e)
        }
        return container
    }

    File getOutputFile(String intermediate, String suffix) {

        File parentDir = new File(directory, intermediate)
        if (!parentDir.exists()) {
            parentDir.mkdir()
        }
        File childDir = new File(parentDir, parent)
        if (!childDir.exists()) {
            childDir.mkdir()
        }
        return new File(childDir, filename.replace(".nxml", suffix))
    }

}
