package org.rvinowise.bumblebee_jumper;

import org.junit.Test;

import static org.rvinowise.game_engine.pos_functions.pos_functions.corner;
import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }


    @Test
    public void test_corner() {
        float a = 30;
        float b = 70;
        float c;
        c = corner(200, 210);
        c = corner(210, 200);
        c = corner(-210, -200);
        c = corner(-200, -210);

        c = corner(-210, 200);
        c = corner(200, -210);

        c = corner(210, -200);
        c = corner(-200, 210);
    }

}