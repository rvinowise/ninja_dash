package org.rvinowise.ninja_dash;


import org.rvinowise.game_engine.Viewport;
import org.rvinowise.game_engine.units.animation.Animated;
import org.rvinowise.game_engine.utils.primitives.Rectangle;


public class PlayerViewport {
    Viewport viewport;
    Animated watched_object;

    float watch_upto_bottom;

    public PlayerViewport(Viewport in_viewport, Animated watched_animated) {
        viewport = in_viewport;
        watched_object = watched_animated;
    }

    public float getWatch_upto_bottom() {
        return watch_upto_bottom;
    }
    public void setWatch_upto_bottom(float watch_up_to_bottom) {
        watch_upto_bottom = watch_up_to_bottom;

        assert(viewport != null);
        assert(viewport.getRect() != null);

        viewport.setWatched_rect(
                new Rectangle(
                        viewport.getRect().getLeft() + (watched_object.getRadius() * 2),
                        viewport.getRect().getLeft() +
                                viewport.getRect().getWidth()/4,
                        watch_up_to_bottom,
                        viewport.getRect().getTop() - (watched_object.getRadius()+2)
                ));
    }

}
