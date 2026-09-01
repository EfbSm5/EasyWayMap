package com.efbsm5.easyway.build

import org.junit.Assert.assertEquals
import org.junit.Test

class BuildConfigEscapingTest {
    @Test
    fun buildConfigString_escapesJavaStringControlCharacters() {
        assertEquals(
            "\"a\\\\b\\\"c\\n\\r\"",
            buildConfigString("a\\b\"c\n\r"),
        )
    }
}
