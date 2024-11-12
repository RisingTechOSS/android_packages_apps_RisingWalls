# Gson
-keep public class org.risingos.wallpaperpicker.jsonparser.objecs.** { *; }
-keepattributes Signature

# Keep method only accessed through reflect
-keep public class org.risingos.wallpaperpicker.utils.NetworkUtils {
        public static android.graphics.Bitmap getBitmapFromUrl(java.lang.String);
}