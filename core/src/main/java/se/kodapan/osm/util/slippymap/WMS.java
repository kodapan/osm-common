package se.kodapan.osm.util.slippymap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author kalle
 * @since 8/25/13 1:29 PM
 */
public class WMS extends SlippyMap {

  private static Logger log = LoggerFactory.getLogger(WMS.class);


  public WMS() {
  }

  public WMS(String urlPattern) {
    super(urlPattern);
  }

  @Override
  public Tile tileFactory(double longitude, double latitude, int z) {
    int x = (int) Math.floor((longitude + 180) / 360 * (1 << z));
    int y = (int) Math.floor((1 - Math.log(Math.tan(Math.toRadians(latitude)) + 1 / Math.cos(Math.toRadians(latitude))) / Math.PI) / 2 * (1 << z));
    return new WMSTile(x, y, z);
  }


  @Override
  public void listTiles(double southLatitude, double westLongitude, double northLatitude, double eastLongitude, int z, TileVisitor tileVisitor) {
    final Tile northEast = tileFactory(eastLongitude, northLatitude, z);
    final Tile southWest = tileFactory(westLongitude, southLatitude, z);
    Tile tile = new WMSTile(0, 0, z);
    for (int x = southWest.getX(); x <= northEast.getX(); x++) {
      for (int y = northEast.getY(); y <= southWest.getY(); y++) {
        tile.setX(x);
        tile.setY(y);
        tileVisitor.visit(tile);
      }
    }
  }

  @Override
  public WMSTile tileFactory(int x, int y, int z) {
    return new WMSTile(x, y, z);
  }

  public static class WMSTile extends Tile {
    public WMSTile(int x, int y, int z) {
      super(x, y, z);
    }

    public double evaluateLongitude(int x, int z) {
      return x / Math.pow(2.0, z) * 360.0 - 180;
    }

    public double evaluateLatitude(int y, int z) {
      double n = Math.PI - (2.0 * Math.PI * y) / Math.pow(2.0, z);
      return Math.toDegrees(Math.atan(Math.sinh(n)));
    }

    @Override
    public double getNorthLatitude() {
      return evaluateLatitude(getY(), getZ());
    }

    @Override
    public double getSouthLatitude() {
      return evaluateLatitude(getY() + 1, getZ());
    }


    @Override
    public double getEastLongitude() {
      return evaluateLongitude(getX() + 1, getZ());
    }

    @Override
    public double getWestLongitude() {
      return evaluateLongitude(getX(), getZ());
    }


  }

}
