package org.lappsgrid.eager.mining.index

import org.junit.After
import org.junit.Before
import org.junit.Ignore
import org.junit.Test

/**
 *
 */
@Ignore
class IndexTest {

    PMCIndex index

    @Before
    void setup() {
        index = new PMCIndex()
    }

    @After
    void teardown() {
        index = null
    }

    @Test
    void size() {
        assert 2019810 == index.size()
    }

    @Test
    void get() {
        String expected = "/var/data/pmc/xml/com/Inflamm_Regen/PMC5828134.nxml"

        String path = index.get(0)
        assert expected == path

        path = index.get("PMC5828134")
        assert expected == path
    }

    @Test
    void arrayStyleAccess() {
        String expected = "/var/data/pmc/xml/com/Inflamm_Regen/PMC5828134.nxml"

        String path = index[0]
        assert expected == path

        path = index["PMC5828134"]
        assert expected == path
    }
}
