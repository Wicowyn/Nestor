import com.sun.istack.internal.NotNull;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yapiti on 27/07/15.
 */
public class Main {

    private static final String IMAGE_PATTERN = "([^\\s]+(\\.(?i)(jpg|png|gif|bmp))$)";

    public static void main(String[] args) {
        List<String> argsList=Arrays.asList(args);
        File folder=new File(args[0]);

        HashMap<String, ImageFileInfo> files=new HashMap<>();

        analyze(folder, Density.LDPI, files);
        analyze(folder, Density.MDPI, files);
        analyze(folder, Density.HDPI, files);
        analyze(folder, Density.XHDPI, files);
        analyze(folder, Density.XXHDPI, files);
        analyze(folder, Density.XXXHDPI, files);


//        ImageFileInfo info=new ImageFileInfo();
//        info.setSize(36, 36, Density.LDPI);
//
//        for(Density density : Density.values()) {
////            System.out.println(density.name()+" -> mult "+ ImageFileInfo.getNextMultiplier(density));
//            System.out.println(density.name()+" -> "+ info.getCalculateWidth(density)+"x"+info.getCalculateHeight(density));
//            break;
//        }
//
//        if(true) return;

        if(argsList.contains("--resume")) {
            printResume(files);
        }

        if(argsList.contains("--size")) {
            String size=argsList.get(argsList.indexOf("--size")+1);

            switch (size) {
                case "ldpi":
                    printSize(files, Density.LDPI);
                    break;
                case "mdpi":
                    printSize(files, Density.MDPI);
                    break;
                case "hdpi":
                    printSize(files, Density.HDPI);
                    break;
                case "xhdpi":
                    printSize(files, Density.XHDPI);
                    break;
                case "xxhdpi":
                    printSize(files, Density.XXHDPI);
                    break;
                case "xxxhdpi":
                    printSize(files, Density.XXXHDPI);
                    break;
            }
        }
    }

    public static void printSize(HashMap<String, ImageFileInfo> files, Density density) {
        int maxLength=0;

        for(String key : files.keySet()) {
            maxLength=Math.max(maxLength, key.length());
        }

        for(Map.Entry<String, ImageFileInfo> entry : files.entrySet()) {
            printSizeLine(entry.getKey(), entry.getValue(), density, maxLength);
        }
    }

    public static void printSizeLine(String name, ImageFileInfo infos, Density density, int maxLength) {
        if(!infos.haveInfo(density)) {
            StringBuilder stringBuilder=new StringBuilder(name);

            for(int i = name.length(); i < maxLength; i++) {
                stringBuilder.append(' ');
            }

            stringBuilder.append(" -> ")
                    .append(infos.getCalculateWidth(density))
                    .append("x")
                    .append(infos.getCalculateHeight(density));

            System.out.println(stringBuilder);
        }
    }

    public static void printResume(HashMap<String, ImageFileInfo> files) {
        int maxLength=0;

        for(String key : files.keySet()) {
            maxLength=Math.max(maxLength, key.length());
        }

        printHeader(maxLength);

        for(Map.Entry<String, ImageFileInfo> entry : files.entrySet()) {
            printLine(entry.getKey(), entry.getValue(), maxLength);
        }
    }

    public static void printHeader(int maxLength) {
        StringBuilder stringBuilder=new StringBuilder();
        stringBuilder.append("File name");

        for(int i = stringBuilder.length(); i < maxLength; i++) {
            stringBuilder.append(' ');
        }

        stringBuilder.append("    ldpi  mdpi  hdpi  xhdpi xxhdp xxxhd");

        System.out.println(stringBuilder);
    }

    public static void printLine(String name, ImageFileInfo infos, int maxLength) {
        StringBuilder stringBuilder=new StringBuilder(name);

        for(int i = name.length(); i < maxLength; i++) {
            stringBuilder.append(' ');
        }

        stringBuilder.append(" ->");

        for(Density density : Density.values()) {
            stringBuilder.append(" | ")
                    .append(infos.haveInfo(density) ? 'o' : ' ')
                    .append(" |");
        }

        System.out.println(stringBuilder);
    }

    public static void analyze(File rootFolder, Density density, HashMap<String, ImageFileInfo> map) {
        File folder=new File(rootFolder, getDirectoryName(density));

        if(!folder.exists() || !folder.isDirectory()) return;

        File[] files=folder.listFiles(new ImageFilter());

        for(File file : files) {
            ImageFileInfo infos=map.get(file.getName());

            if(infos==null) {
                infos=new ImageFileInfo();
                map.put(file.getName(), infos);
            }

            try {
                BufferedImage readImage = ImageIO.read(file);
                infos.setSize(readImage.getWidth(), readImage.getHeight(), density);
            } catch (IOException e) {
                System.err.println("Failled to read "+file.getAbsolutePath());
                e.printStackTrace();
            }
        }
    }

    public static @NotNull String getDirectoryName(Density density) {
        switch (density) {
            case LDPI:
                return "drawable-ldpi";
            case MDPI:
                return "drawable-mdpi";
            case HDPI:
                return "drawable-hdpi";
            case XHDPI:
                return "drawable-xhdpi";
            case XXHDPI:
                return "drawable-xxhdpi";
            case XXXHDPI:
                return "drawable-xxxhdpi";
            default:
                throw new RuntimeException("Yolo");
        }
    }



    public static class ImageFilter implements FilenameFilter {

        @Override
        public boolean accept(File file, String s) {
            return s.matches(IMAGE_PATTERN);
        }
    }

}
