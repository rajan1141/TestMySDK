package com.test.my.app.common.pdfviewer.util;

public class Constants {

    public static boolean DEBUG_MODE = false;

    /** Between 0 and 1, the thumbnails quality (default 0.3). Increasing this value may cause performance decrease */
    public static float THUMBNAIL_RATIO = 0.3f;

    /**
     * The size of the rendered parts (default 256)
     * Tinier : a little bit slower to have the whole page rendered but more reactive.
     * Bigger : user will have to wait longer to have the first visual results
     */
    public static float PART_SIZE = 256;

    /** Part of document above and below screen that should be preloaded, in dp */
    public static int PRELOAD_OFFSET = 20;

    public static class Cache {

        /** The size of the cache (number of bitmaps kept) */
        public static int CACHE_SIZE = 120;

        public static int THUMBNAILS_CACHE_SIZE = 8;
    }

    public static class Pinch {

        public static float MAXIMUM_ZOOM = 10;

        public static float MINIMUM_ZOOM = 1;

    }

}
