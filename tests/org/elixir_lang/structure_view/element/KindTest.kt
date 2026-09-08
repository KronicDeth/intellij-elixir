package org.elixir_lang.structure_view.element

import junit.framework.TestCase
import org.elixir_lang.call.Visibility
import org.elixir_lang.structure_view.element.Timed.Time

/**
 * The module-attribute mappings the sorters read, and the boundary that answers an unrecognised name.
 */
class KindTest : TestCase() {
    fun testTypeKindVisibility() {
        assertEquals(Visibility.PUBLIC, Type.Kind.of("@opaque")?.visibility)
        assertEquals(Visibility.PUBLIC, Type.Kind.of("@type")?.visibility)
        assertEquals(Visibility.PRIVATE, Type.Kind.of("@typep")?.visibility)
    }

    fun testOnlyOpaqueIsOpaque() {
        assertEquals(true, Type.Kind.of("@opaque")?.isOpaque)
        assertEquals(false, Type.Kind.of("@type")?.isOpaque)
        assertEquals(false, Type.Kind.of("@typep")?.isOpaque)
    }

    fun testCallbackKindTime() {
        assertEquals(Time.RUN, Callback.Kind.of("@callback")?.time)
        assertEquals(Time.COMPILE, Callback.Kind.of("@macrocallback")?.time)
    }

    fun testAnUnrecognisedAttributeIsNull() {
        assertNull(Type.Kind.of("@spec"))
        assertNull(Type.Kind.of("@callback"))
        assertNull(Callback.Kind.of("@type"))
        assertNull(Callback.Kind.of("@spec"))
    }
}
