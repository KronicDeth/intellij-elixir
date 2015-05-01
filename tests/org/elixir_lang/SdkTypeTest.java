package org.elixir_lang;

import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;
import org.junit.Test;

import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.*;

public class SdkTypeTest {

    @Test
    public void testSuggestHomePathWithoutSuggestHomePaths() throws Exception {
        SdkType spy = spy(new SdkType());

        doReturn(Collections.<String>emptyList()).when(spy).suggestHomePaths();

        assertNull(spy.suggestHomePath());
    }

    @Test
    public void testSuggestHomePathWithSuggestHomePaths() throws Exception {
        SdkType spy = spy(new SdkType());
        String expectedSuggestedHomePath = "/suggested/home/path";

        doReturn(Collections.singletonList(expectedSuggestedHomePath)).when(spy).suggestHomePaths();

        assertEquals(expectedSuggestedHomePath, spy.suggestHomePath());
    }
}