package io.github.cdsap.fatbinary

import org.junit.Assert.assertEquals
import org.junit.Test

class FatBinaryNamingTest {

    @Test
    fun manifestMainClassAppendsKtSuffix() {
        assertEquals("com.example.MainKt", FatBinaryNaming.manifestMainClass("com.example.Main"))
    }
}
