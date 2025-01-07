package com.rotarola.portafolio_kotlin.presentation.view.atoms;

import com.rotarola.feature_ui.presentation.atoms.TextKt;

import org.junit.Test;

public class TextKtTest {

    @Test
    public void testTextM3() throws Exception {
        TextKt.TextM3(0, true, "text", "placeholder", "label", null, 0, true, 0, 0L, "textDownEditext");
    }

    @Test
    public void testEditextM3() throws Exception {
        TextKt.EditextM3(0, true, "value", "placeholder", "label", true, null, 0, true, 0, true, 0d, null, "textDownEditext", true, null, true, true, null, null);
    }

    @Test
    public void testSimpleText() throws Exception {
        TextKt.SimpleText("text", 0);
    }
}

//Generated with love by TestMe :) Please raise issues & feature requests at: https://weirddev.com/forum#!/testme