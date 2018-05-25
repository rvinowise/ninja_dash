package org.rvinowise.ninja_dash.background;


import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;

import org.rvinowise.game_engine.Viewport;
import org.rvinowise.game_engine.units.animation.Animated;
import org.rvinowise.game_engine.units.animation.Animation;
import org.rvinowise.game_engine.utils.primitives.Point;


public class Backgrownd {


    private Deque<Animated> instances = new ArrayDeque<Animated>();
    private Viewport viewport;

    private Animation animation;
    private Point size;
    private float animation_speed = 1;


    public Animation getAnimation() {
        return animation;
    }
    public void init(Viewport in_viewport, Animation in_animation, Point in_size) {
        viewport = in_viewport;
        animation = in_animation;
        size = in_size;
    }

    public void setAnimation_speed(float speed) {
        animation_speed = speed;
    }

    public  void create_first_instances(float in_y) {
        Point current_position = new Point(viewport.getRect().getLeft(), in_y);
        Point offset = new Point(getWidth(), 0);
        int qty_needed = (int)Math.ceil(viewport.getRect().getWidth() / getWidth());
        for (int i_instance = 0; i_instance < qty_needed; i_instance++) {
            Animated new_animated = create_instance();
            new_animated.setPosition(new Point(current_position));
            current_position.plus(offset);
        }

    }


    public void step_instances() {
        for (Animated instance: instances) {
            instance.step();
        }
    }

    public float getWidth() {
        return size.getX()*animation.getEssential_texture_scale().getX();
    }
    public float getHeight() {
        return size.getY();
    }

    public Collection<Animated> getInstances() {
        return instances;
    }

    public Animated getLast_instance() {
        return instances.peekLast();
    }


    public boolean no_more_instances_ahead() {
        Animated last_instance = getLast_instance();
        if (last_instance != null) {

            if (
                    (last_instance.getPosition().getX() + (getWidth() / 2)) <
                            viewport.getRect().getRight()
                    ) {
                return true;
            }
        }
        return false;
    }

    public void prolongate() {
        Point position = new Point(
                getLast_instance().getPosition().getX()+ getWidth(),
                getLast_instance().getPosition().getY()
        );
        Animated new_animated = create_instance();
        new_animated.setPosition(position);

    }

    public Animated create_instance() {
        Animated new_instance = new Animated();
        instances.addLast(new_instance);
        new_instance.startAnimation(animation);
        new_instance.setSize(size);
        new_instance.setAnimation_speed(animation_speed);
        return new_instance;
    }


}
