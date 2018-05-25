package org.rvinowise.ninja_dash;

import org.rvinowise.ninja_dash.walls.Strawberry;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import javax.microedition.khronos.opengles.GL10;

import org.rvinowise.game_engine.Engine;
import org.rvinowise.game_engine.initialisation.Sprite_loader;
import org.rvinowise.game_engine.pos_functions.pos_functions;
import org.rvinowise.game_engine.units.animation.Animation;
import org.rvinowise.game_engine.units.animation.Effect;
import org.rvinowise.game_engine.units.animation.Sprite_for_loading;
import org.rvinowise.game_engine.units.animation.Animated;
import org.rvinowise.game_engine.utils.primitives.Point;
import org.rvinowise.game_engine.utils.primitives.Rectangle;
import org.rvinowise.game_engine.utils.primitives.Moving_vector;


public class NinjaEngine extends Engine
{

    private Set<Strawberry> strawberries = new HashSet<Strawberry>();
    private Ninja_player player;

    private Units_generator generator = new Units_generator(this);

    public Ninja_player getPlayer() {
        return player;
    }

    public Collection<Strawberry> getStrawberries() {
        return strawberries;
    }

    public PlayerViewport getPlayerViewport() {
        return player_viewport;
    }

    PlayerViewport player_viewport;


    public NinjaEngine() {

    }


    /* инициализировать сцену.
       создать и настроить контролирующие объекты (типа View),
       на основе которых будут генерироваться новые объекты в процессе движения игры.
            При изменении важных характеристик (соотношение сторон монитора),
       эти контролирующие объекты изменятся/подстроятся      */
    public void init_scene() {
        player = new Ninja_player();
        init_viewport(player);

        player.startAnimation(Animation.valueOf(R.drawable.anim_bumblebee_fly));
        player.setPosition(new Point(0, (float) 2));

        generator.init_scene();

        super.register_first_step_as_done();
    }

    private void init_viewport(Animated watched_object) {

        player_viewport = new PlayerViewport(super.getViewport(), watched_object);
        super.getViewport().watch_object(watched_object);
    }


    private boolean is_resolution_known() {
        return (getViewport().getRect() != null);
    }

    public Strawberry add_strawberry() {
        Strawberry strawberry = new Strawberry();
        strawberries.add(strawberry);
        return strawberry;
    }
    public Strawberry add_strawberry(Point position) {
        Strawberry strawberry = add_strawberry();
        strawberry.setPosition(position);
        return strawberry;
    }
    protected void remove(int i_physical) {
        Animated physical = getAnimateds().get(i_physical);
        if (physical instanceof Strawberry) {
            strawberries.remove(physical);
        }

        super.remove(i_physical);
    }

    @Override
    public void step() {

        if (player.isLive()) {
            player_control();
        }
        process_player_physics();
        process_maybe_changing_viewport();
        process_elementary_physics();

        generator.step();
    }


    private void process_maybe_changing_viewport() {

        final float water_y = generator.getWater_y();
        float distance_to_water = player.getPosition().getY() - water_y;
        if (
                (getPlayerViewport().getWatch_upto_bottom() == 0) &&
                        (distance_to_water < super.getViewport().getRect().getHeight() / 2)
                ) {
            getPlayerViewport().setWatch_upto_bottom(super.getViewport().getRect().getBottom());
        } else if (
                (getPlayerViewport().getWatch_upto_bottom() < 0) &&
                        (distance_to_water >= super.getViewport().getRect().getHeight() / 2)
                ) {
            getPlayerViewport().setWatch_upto_bottom(0);
        }

    }



    private void process_player_physics() {
        if (!is_collide_water(player)) {
            process_player_falling();
        } else {
            process_player_water_sink();
        }
        process_player_collisions();

    }

    private void process_player_falling() {
        final Point gravity_vector = new Moving_vector(0f,-0.001f).getStep_value();
        player.getMoving_vector().plus(gravity_vector);

        //player.cruise_fly();
    }

    private void process_player_collisions() {
        Collection<Animated> collided = getCollided_circle(player);
        player_jump_from_balloons(collided);
    }

    private void player_jump_from_balloons(Collection<Animated> collided) {
        for (Animated animated: collided) {
            if (animated instanceof Strawberry) {
                Strawberry strawberry = (Strawberry)animated;
                if (!is_collide_water(player)) {
                    jump_from_strawberry(player, strawberry);
                }
                strawberry.explode(player);
                score.add(1);
            }
        }
    }

    private void jump_from_strawberry(Animated jumper, Strawberry from) {
        final Point prev_position = jumper.getPosition()/*.minus(jumper.getMoving_vector())*/;
        final float dir_to_balloon = pos_functions.poidir(prev_position, from.getPosition());
        float corner =pos_functions.corner(jumper.getVectorDirection(), dir_to_balloon);
        float bounce_dir = (dir_to_balloon) + corner-180;
        final float jump_slowing_factor = 1.8f;
        float bounce_speed = jumper.getVectorLength()/jump_slowing_factor;
        jumper.setMoving_vector(pos_functions.lendir(bounce_speed, bounce_dir));
    }


    private void player_control() {
        if (
                getControl().isTouched()
           )
        {

        }
        else if (
                !getControl().isTouched()
                )
        {

        }

    }

    private void process_player_water_sink() {
        if (player.isLive()) {
            Effect effect = Effect.create(Animation.valueOf(R.drawable.water_splash),
                    new Point(player.getPosition().getX(), getWaterHeight()), 0);
            effect.setAnimation_speed(0.5f);
            effect.setSize(new Point(player.getRadius() * 10, Math.abs(player.getMoving_vector().getY()) * 20));
            player.setMoving_vector(new Point(player.getMoving_vector().getX()/4, -0.02f));

            getViewport().watch_object(null);
            bumblebee_die_from_sinking();
        }
    }
    private boolean is_collide_water(Animated animated) {
        return (animated.getPosition().getY() - animated.getRadius() <= getWaterHeight());
    }
    private void bumblebee_die_from_sinking() {
        player.die();
        player.setAnimation_speed(0);
        start_epilog_before_returning_to_menu();
    }
    private void start_epilog_before_returning_to_menu() {
        super.start_epilog();
        Timer timer_to_menu = new Timer();
        TimerTask start_menu = new TimerTask() {
            @Override
            public void run() {
                handler_menu.post(new Runnable() {
                    @Override
                    public void run() {
                        system_listener.return_score_to_start_screen();
                    }
                });
            }
        };
        final int epilog_duration = 2000;
        timer_to_menu.schedule(start_menu, epilog_duration);
    }



    protected boolean is_outside(Animated animated) {

        final float possible_player_move_back = 0;//getViewport().getRect().getWidth()/6;
        final float x_when_allready_not_visible =
                getViewport().getRect().getLeft()-
                animated.getDrowing_size().getX()/2;
                //animated.getRight();
        final float x_when_not_reacheble = x_when_allready_not_visible-possible_player_move_back;
        /*if (animated.getPosition().getX() < x_when_not_reacheble) {
            return true;
        }*/
        if (animated.getRight() <= getViewport().getRect().getLeft()) {
            return true;
        }
        return false;
    }


    @Override
    public Sprite_loader getSprite_loader() {

        Sprite_loader sprite_loader = new Sprite_loader();

        sprite_loader.start_background_registration();
        //sprite_loader.add(new Sprite_for_loading(R.drawable.grass, new Rectangle (512,512), 1));
        sprite_loader.add(new Sprite_for_loading(R.drawable.grass_no_bottom, new Rectangle (512,512), 1));

        sprite_loader.start_foreground_registration();
        sprite_loader.add(new Sprite_for_loading(R.drawable.strawberry_stalk, new Rectangle(512,2048), 1, 4.26f, new Point(175,160)));
        sprite_loader.add(new Sprite_for_loading(R.drawable.strawberry_full, new Rectangle(512,2048), 1, 4.26f, new Point(175,160)));
        sprite_loader.add(new Sprite_for_loading(R.drawable.strawberry, new Rectangle(128,128), 1, 1, new Point(64,64)));
        sprite_loader.add(new Sprite_for_loading(R.drawable.strawberry_explode, new Rectangle(100,150), 8, 2, new Point(47,32)));

        sprite_loader.add(new Sprite_for_loading(R.drawable.anim_bumblebee_fly, new Rectangle (160,220), 6, 2.16f, new Point(78,108)));

        sprite_loader.add(new Sprite_for_loading(R.drawable.water, new Rectangle (256,35), 5, 1, new Point(0,4)));
        sprite_loader.add(new Sprite_for_loading(R.drawable.water_splash, new Rectangle(62,62), 10, 1, new Point(32, 50)));

        return sprite_loader;
    }

    boolean scene_created = false;
    @Override
    public void onSurfaceChanged(GL10 glUnused, int width, int height) {
        super.getViewport().set_scale_of_shortest_side(7);
        super.onSurfaceChanged(glUnused, width, height);
        if (!scene_created) {
            init_scene();
            getPlayerViewport().setWatch_upto_bottom(0);
            //scene_created = true; todo
        }
    }


    public void onAdLoaded() {

    }

    public void onAdClosed() {

    }


    public float getWaterHeight() {
        return generator.getWater_y();
    }

    public void change_resolution(int screenWidth, int screenHeight) {
        getPlayerViewport().setWatch_upto_bottom(0);

    }
}
