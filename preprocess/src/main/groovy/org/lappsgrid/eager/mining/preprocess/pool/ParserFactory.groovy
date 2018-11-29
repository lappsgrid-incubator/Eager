package org.lappsgrid.eager.mining.preprocess.pool

import org.apache.commons.pool2.BasePooledObjectFactory
import org.apache.commons.pool2.PooledObject
import org.apache.commons.pool2.impl.DefaultPooledObject
import org.lappsgrid.eager.mining.preprocess.pmc.Parser

/**
 *
 */
class ParserFactory extends BasePooledObjectFactory<Parser> {
    @Override
    Parser create() throws Exception {
        return new Parser()
    }

    @Override
    PooledObject<Parser> wrap(Parser parser) {
        return new DefaultPooledObject<Parser>(parser)
    }
}
