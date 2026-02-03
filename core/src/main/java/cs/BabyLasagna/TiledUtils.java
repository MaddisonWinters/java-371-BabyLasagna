package cs.BabyLasagna;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

public class TiledUtils {
    // Returns rectangles of all tiles of the specified type (e.g. "walls")
    public static void getTiles (TiledMap map, int startX, int startY, int endX, int endY, Array<Rectangle> tiles, String type) {
        TiledMapTileLayer layer = (TiledMapTileLayer)map.getLayers().get(type);
        tiles.clear();
        for (int y = startY; y <= endY; y++) {
            for (int x = startX; x <= endX; x++) {
                TiledMapTileLayer.Cell cell = layer.getCell(x, y);
                if (cell != null) {
                    Rectangle rect = new Rectangle(x, y, 1, 1);
                    tiles.add(rect);
                }
            }
        }
    }
}
