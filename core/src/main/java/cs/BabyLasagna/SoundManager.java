package cs.BabyLasagna;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.*;

public class SoundManager {
    public static class GameSnd {
        public static class PlayerSnd {
            private static final Sound jump, grow, shrink;

            static {
                jump   = Gdx.audio.newSound(Gdx.files.internal("sound/player/jump.wav"));
                grow   = Gdx.audio.newSound(Gdx.files.internal("sound/player/toss.wav"));
                shrink = Gdx.audio.newSound(Gdx.files.internal("sound/player/hurt.wav"));
            }

            public static void jump() { jump.play(); }
            public static void grow() { grow.play(); }
            public static void shrink() { shrink.play(); }
        }
    }


    public static class BGMusic {
        private static Music playing = null;

        public static class GameMsc {
            private static final Music main;

            static {
                main = Gdx.audio.newMusic(Gdx.files.internal("sound/music/main_short.mp3"));

                main.setLooping(true);
            }

            public static void playMain() { switchTo(main); }
        }

        public static void pause() { if (playing != null) playing.pause(); }
        public static void stop() { if (playing != null) playing.stop(); }
        public static void switchTo(Music other) { if (playing != null) playing.stop(); other.play(); playing = other; }
    }

    public static class InMenu {
        
    }
}
