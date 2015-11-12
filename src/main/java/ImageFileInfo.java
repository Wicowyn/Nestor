import java.util.HashMap;

/**
 * Created by yapiti on 27/07/15.
 */
public class ImageFileInfo {
    private HashMap<Density, Integer> width=new HashMap<>();
    private HashMap<Density, Integer> height=new HashMap<>();


    public void setSize(int width, int height, Density density) {
        this.width.put(density, width);
        this.height.put(density, height);
    }

    public Integer getHeight(Density density) {
        return height.get(density);
    }

    public Integer getWidth(Density density) {
        return width.get(density);
    }

    public boolean haveInfo(Density density) {
        return getWidth(density) != null && getHeight(density) != null;
    }

    public Integer getCalculateHeight(Density density) {
        Density from=getNearest(density);

        return (int) (height.get(from)*getMultiplier(from, density));
    }

    public Integer getCalculateWidth(Density density) {
        Density from=getNearest(density);

        return (int) (width.get(from)*getMultiplier(from, density));
    }

    private Density getNearest(Density density) {
        Density from=null;

        for(int i=density.ordinal(); i<Density.values().length && from==null; i++) {
            Density next=Density.values()[i];

            if(haveInfo(next)) {
                from=next;
            }
        }

        for(int i=density.ordinal(); i<Density.values().length && from==null; i--) {
            Density next=Density.values()[i];

            if(haveInfo(next)) {
                from=next;
            }
        }

        return from;
    }


    public static float getMultiplier(Density from, Density to) {
        float multiplier=1f;
        boolean canStart=false;
        boolean shouldInvert=from.ordinal() > to.ordinal();

        if(shouldInvert) {
            Density temp=from;
            from=to;
            to=temp;
        }

        for(Density density : Density.values()) {
            if(density == from) {
                canStart=true;
            }
            if(density == to) {
                canStart = false;
            }

            if(canStart) {
                multiplier*=getNextMultiplier(density);
            }
        }

        return shouldInvert? 1f/multiplier : multiplier;
    }

    public static float getNextMultiplier(Density density) {
        switch (density) {
            case LDPI:
                return 4f/3f;
            case MDPI:
                return 6f/4f;
            case HDPI:
                return 8f/6f;
            case XHDPI:
                return 12f/8f;
            case XXHDPI:
                return 16f/12f;
            default:
                return 1f;
        }
    }
}
