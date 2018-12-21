package org.lappsgrid.eager.mining.web.nlp.stanford

import org.junit.*
import static org.lappsgrid.discriminator.Discriminators.*
import org.lappsgrid.eager.core.json.Serializer
import org.lappsgrid.serialization.lif.Container
import org.lappsgrid.serialization.lif.View

/**
 *
 */
class PipelineTest {

    public static final String TEXT = "Karen flew to New York. Nancy flew to Bloomington."
    Pipeline pipeline

    @Before
    void setup() {
        pipeline = new Pipeline()
    }

    @After
    void teardown() {
        pipeline = null
    }

    @Test
    void text() {
        Container container = pipeline.process(TEXT)
        assert 2 == container.views.size()
        List<View> views = container.findViewsThatContain(Uri.SENTENCE)
        assert 1 == views.size()
        assert 2 == views[0].annotations.size()

        views = container.findViewsThatContain(Uri.TOKEN)
        assert 1 == views.size()
        assert 11 == views[0].annotations.size()

        println Serializer.toPrettyJson(container)
    }

    @Test
    void container() {
        Container container = new Container()
        container.text = TEXT

        container = pipeline.process(container)
        assert 2 == container.views.size()
        List<View> views = container.findViewsThatContain(Uri.SENTENCE)
        assert 1 == views.size()
        assert 2 == views[0].annotations.size()

        views = container.findViewsThatContain(Uri.TOKEN)
        assert 1 == views.size()
        assert 11 == views[0].annotations.size()
    }
}
