package com.hippo.androidchinesestring;

/**
 * Created by Hippo on 2015/2/17.
 */
public final class ChineseLanguage {

    public static final int CHINESE_COUNT = 3;

    public static final int INT_CHINSES_UNKNOWN = -1;
    public static final int INT_CHINSES_S = 0;
    public static final int INT_CHINSES_T_HK = 1;
    public static final int INT_CHINSES_T_TW  = 2;
    public static final int[] INT_CHINESE_ARRAY = {
            INT_CHINSES_S,
            INT_CHINSES_T_HK,
            INT_CHINSES_T_TW
    };

    public static final String STRING_CHINSES_S = "values-zh-rCN";
    public static final String STRING_CHINSES_T_HK = "values-zh-rHK";
    public static final String STRING_CHINSES_T_TW = "values-zh-rTW";
    public static final String[] STRING_CHINSES_ARRAY = {
            STRING_CHINSES_S,
            STRING_CHINSES_T_HK,
            STRING_CHINSES_T_TW
    };

    public static final Chinese CHINESE_S = new Chinese(INT_CHINSES_S, STRING_CHINSES_S);
    public static final Chinese CHINESE_T_HK = new Chinese(INT_CHINSES_T_HK, STRING_CHINSES_T_HK);
    public static final Chinese CHINESE_T_TW = new Chinese(INT_CHINSES_S, STRING_CHINSES_T_TW);
    public static final Chinese[] CHINESE_ARRAY = {
            CHINESE_S,
            CHINESE_T_HK,
            CHINESE_T_TW
    };

    public static class Chinese {
        public final int integer;
        public final String string;

        private Chinese(int i, String s) {
            integer = i;
            string = s;
        }
    }

    /**
     * Get source and destination Chinese.
     *
     * @param valueName the value folder name
     * @return first one is source, the other is destination, null for not Chinese
     */
    public static Chinese[] parserValueName(String valueName) {
        int index;
        int length = STRING_CHINSES_ARRAY.length;
        for (index = 0; index < length; index++) {
            if (STRING_CHINSES_ARRAY[index].equals(valueName)) {
                break;
            }
        }
        if (index != length) {
            // OK, I found it
            Chinese[] result = new Chinese[CHINESE_COUNT];
            System.arraycopy(CHINESE_ARRAY, 0, result, 0, CHINESE_COUNT);
            // Change index and first
            if (index != 0) {
                result[0] = CHINESE_ARRAY[index];
                result[index] = CHINESE_ARRAY[0];
            }
            return result;
        } else {
            return null;
        }
    }


    /**
     * Check if the value folder is Chinese value folder.
     *
     * @param valueName the value folder name
     * @return True if it is Chinese value folder
     */
    public static boolean isChineseValueName(String valueName) {
        for (String chinese : STRING_CHINSES_ARRAY) {
            if (chinese.equals(valueName)) {
                return true;
            }
        }
        return false;
    }


}
