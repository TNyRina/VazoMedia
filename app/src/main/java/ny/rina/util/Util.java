package ny.rina.util;

public class Util {
    public static String formatValueDuration(Double value){
        return (value < 10) ? "0" + value.intValue() : String.valueOf(value.intValue());
    }
}
