package org.lappsgrid.eager.nlp

import edu.stanford.nlp.ling.CoreLabel
import edu.stanford.nlp.pipeline.CoreDocument
import edu.stanford.nlp.pipeline.CoreSentence
import edu.stanford.nlp.pipeline.StanfordCoreNLP
import edu.stanford.nlp.util.Pair
import groovy.util.logging.Log4j2
import org.lappsgrid.discriminator.Discriminators
import org.lappsgrid.serialization.LifException
import org.lappsgrid.serialization.lif.Annotation
import org.lappsgrid.serialization.lif.Container
import org.lappsgrid.serialization.lif.View

/**
 *
 */
//@Slf4j("logger")
//@Log4j2('logger')
class Stanford {

//    static final Logger logger = LoggerFactory.getLogger(Stanford)

    StanfordCoreNLP pipeline;

    Stanford() {
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize,ssplit,pos,lemma");
        pipeline = new StanfordCoreNLP(props);
    }

    public Container process(String text) throws LifException
    {
        Container container = new Container();
        container.setText(text);
        return process(container);
    }

    public Container process(Container container) throws LifException
    {
//        logger.info("Processing container. Size: {}", container.text.length())
        CoreDocument document = new CoreDocument(container.getText());
        pipeline.annotate(document);


        // Process the sentences.
//        logger.trace("processing sentences")
        int id = 0;
        View sentences = container.newView();
        for (CoreSentence s : document.sentences()) {
            Pair<Integer,Integer> offsets = s.charOffsets();
            sentences.newAnnotation("s-" + (id++), Discriminators.Uri.SENTENCE, offsets.first, offsets.second);
        }
        if (id > 0) {
            sentences.addContains(Discriminators.Uri.SENTENCE, this.getClass().getName(), "stanford");
        }

        // Process tokens. Include lemmas and part of speech.
//        logger.trace('Processing tokens')
        View tokens = container.newView();
        id = 0;
        for (CoreLabel token : document.tokens()) {
            int start = token.beginPosition();
            int end = token.endPosition();
            Annotation a = tokens.newAnnotation("tok-" + (id++), Discriminators.Uri.TOKEN, start, end);
            set(a, Features.Token.LEMMA, token.lemma());
            set(a, Features.Token.PART_OF_SPEECH, token.tag());
            set(a, Features.Token.WORD, token.word());
            set(a, "category", token.category());
        }
        if (id > 0) {
            tokens.addContains(Discriminators.Uri.TOKEN, this.getClass().getName(), "stanford");
        }

//        logger.debug('Processing complete')
        return container
    }

    protected void set(Annotation a, String key, String value) {
        if (value == null) {
            return;
        }
        a.addFeature(key, value);
    }

}
