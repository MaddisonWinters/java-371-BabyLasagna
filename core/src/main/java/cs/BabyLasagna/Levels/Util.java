package cs.BabyLasagna.Levels;

import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

public class Util {
    private Util() {}
    // Returns a list of rectangles of all tiles in the specific layer
    public static void getRect(TiledMap map, String layer_name, Array<Rectangle> tile_rects, int startX, int startY, int endX, int endY) {
        TiledMapTileLayer layer = (TiledMapTileLayer)map.getLayers().get(layer_name);
        if (layer == null) {
            System.err.println("ERROR in Levels.Util.getTiles: Null layer found in TiledMap");
            return;
        }
        for (int y = startY; y <= endY; ++y) {
            for (int x = startX; x <= endX; ++x) {
                TiledMapTileLayer.Cell cell = layer.getCell(x, y);
                if (cell != null) {
                    tile_rects.add(new Rectangle(x, y, 1, 1));
                }
            }
        }
    }

    //Returns any tags of tiles found on the specific layer
    public static void getTags(TiledMap map, String layer_name, Array<MapProperties> foundTags, Array<Rectangle> tileRects, int startX, int startY, int endX, int endY) {
        TiledMapTileLayer layer = (TiledMapTileLayer)map.getLayers().get(layer_name);
        if (layer == null) return;

        for (int y = startY; y <= endY; ++y) {
            for (int x = startX; x <= endX; ++x) {
                TiledMapTileLayer.Cell cell = layer.getCell(x, y);
                if (cell != null && cell.getTile() != null) {
                    MapProperties props = cell.getTile().getProperties();
                    if (props != null) {
                        foundTags.add(props);
                        tileRects.add(new Rectangle(x, y, 1, 1));
                    }
                }
            }
        }
    }
}
