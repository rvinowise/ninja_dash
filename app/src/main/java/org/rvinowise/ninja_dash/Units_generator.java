package org.rvinowise.ninja_dash;


import android.util.Log;

import org.rvinowise.ninja_dash.background.Backgrownd;
import org.rvinowise.ninja_dash.walls.Strawberry;

import java.util.Collection;
import java.util.Random;

import org.rvinowise.game_engine.units.animation.Animated;
import org.rvinowise.game_engine.units.animation.Animation;
import org.rvinowise.game_engine.utils.primitives.Point;

class Units_generator {

    private final NinjaEngine engine;
    private final Random random;
    private Backgrownd water_flow = new Backgrownd();
    private Backgrownd grass_flow = new Backgrownd();
    private int min_strawberries_qty = 6;
    private Animated last_animated;
    //private int min_strawberries_qty = 20;

    Units_generator(NinjaEngine in_engine) {
        engine = in_engine;

        long random_seed = System.currentTimeMillis();
        random = new Random(random_seed);
        Log.d("Units_generator", "random_seed = "+random_seed);
    }

    void init_scene() {
        water_flow.init(engine.getViewport(), Animation.valueOf(R.drawable.water), new Point(1,1));
        water_flow.setAnimation_speed(0.6f);

        grass_flow.init(engine.getViewport(), Animation.valueOf(R.drawable.grass_no_bottom), new Point(12,12));

        water_flow.create_first_instances(-4);
        grass_flow.create_first_instances(water_flow.getLast_instance().getPosition().getY()+
                grass_flow.getHeight()/2);

        fill_scene_with_strawberries();

    }


    void step() {
        if (too_few_strawberries()) {
            create_strawberry_ahead();
        }
        if (water_flow.no_more_instances_ahead()) {
            water_flow.prolongate();
        }
        if (grass_flow.no_more_instances_ahead()) {
            grass_flow.prolongate();
        }
        water_flow.step_instances();
        grass_flow.step_instances();
    }



    private boolean too_few_strawberries() {
        if (engine.getStrawberries().size() < min_strawberries_qty) {
            return true;
        }
        return false;
    }

    private void fill_scene_with_strawberries() {
        Strawberry first_strawberry = engine.add_strawberry();
        first_strawberry.setPosition(
                engine.getBumblebee().getPosition().new_plus(new Point(
                        3, -3
                ))
        );
        first_strawberry.setSize(1.5f);
        Strawberry last_strawberry = first_strawberry;
        for (int i_strawberry = 0; i_strawberry < min_strawberries_qty-1; i_strawberry++) {
            final float x_spread = 4;
            //final float y_spread = 5;
            Strawberry strawberry = engine.add_strawberry();
            strawberry.setPosition(
                    last_strawberry.getPosition().new_plus(new Point(
                            strawberry.getRadius()+last_strawberry.getRadius()
                                    +random.nextFloat()*x_spread,
                            get_random_possible_height_for_strawberry()
                    ))
                    //last_strawberry.getPosition().new_plus(new Point(1,0))
            );

            last_strawberry = strawberry;
        }
    }

    private void create_strawberry_ahead() {

        float radius = Strawberry.get_random_radius(random);

        float line_ahead;
        if (
                is_outside_scene_forward(last_animated)
                ) {
            line_ahead = last_animated.getPosition().getX();
        } else {
            line_ahead = engine.getViewport().getRect().getRight() + radius;
        }
        float random_height = get_random_possible_height_for_strawberry();


        Strawberry strawberry = engine.add_strawberry(new Point(line_ahead, random_height));
        strawberry.setRadius(radius);

        strawberry.setPosition(new Point(line_ahead, random_height));
        place_to_free_position(strawberry);


    }

    private boolean is_outside_scene_forward(Animated animated) {
        return (
                (animated!=null)&&
                (animated.getPosition().getX()+animated.getRadius() > engine.getViewport().getRect().getRight())
        );
    }

    private float get_random_possible_height_for_strawberry() {
        return engine.getViewport().getRect().getBottom()+
                random.nextFloat()*engine.getViewport().getRect().getHeight() + Strawberry.getStandardRadius();
    }

    private void place_to_free_position(Animated animated) {
        Collection<Animated> collided = engine.getCollided_rect(animated, Strawberry.class);
        if (!collided.isEmpty()) {
                animated.getPosition().plus(
                        animated.getRadius()+Strawberry.get_max_radius(),
                        0);
        }
        //test
        //animated.setSize(new Point(0.5f,2f));
        /*for(Animated a_collided: collided) {
            a_collided.setSize(new Point(2f,0.5f));
        }*/
        //testend

        last_animated = animated;
    }


    float getWater_y() {
        return water_flow.getLast_instance().getPosition().getY();
    }
}
