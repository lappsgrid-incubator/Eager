package org.lappsgrid.eager.mining.web.util

import static org.lappsgrid.discriminator.Discriminators.*
import org.lappsgrid.serialization.lif.Annotation
import org.lappsgrid.serialization.lif.Container
import org.lappsgrid.serialization.lif.View

/**
 *
 */
class Utils {
    static List<Annotation> getTokens(Container container) {
        List<View> views = container.findViewsThatContain(Uri.TOKEN)
        if (views.size() == 0) {
            return []
        }
        View view = views[-1]
        return view.findByAtType(Uri.TOKEN)
    }
}
