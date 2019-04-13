package org.elixir_lang.sdk.elixir;

import org.junit.Test;

import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

public class TypeTest {

    @Test
    public void testSuggestHomePathWithoutSuggestHomePaths() {
        Type spy = spy(new Type());

        doReturn(Collections.<String>emptyList()).when(spy).suggestHomePaths();

        assertNull(spy.suggestHomePath());
    }

    @Test
    public void testSuggestHomePathWithSuggestHomePaths() {
        Type spy = spy(new Type());
        String expectedSuggestedHomePath = "/suggested/home/path";

        doReturn(Collections.singletonList(expectedSuggestedHomePath)).when(spy).suggestHomePaths();

        assertEquals(expectedSuggestedHomePath, spy.suggestHomePath());
    }
}
