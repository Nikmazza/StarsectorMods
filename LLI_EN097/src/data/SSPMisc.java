package data;

import java.awt.*;

public class SSPMisc {
    public static String getDigitValue(float value) {
        return getDigitValue(value, 1);
    }

    public static String getDigitValue(float value, int digit) {
        if (Math.abs((float) Math.round(value) - value) < 0.01f) {
            return String.format("%d", (int) Math.round(value));
        } else {
            return String.format("%." + digit + "f", value);
        }
    }

    //反色小工具
    public static Color Anti_Color(Color color){
        int newR = 255 - color.getRed();
        int newG = 255 - color.getGreen();
        int newB = 255 - color.getBlue();
        return new Color(newR,newG,newB) ;
    }
}
