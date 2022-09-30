package hexlet.code.utils;

public class Env {
    public static final int ITEMS_PER_PAGE = 10;
    public static final int UNPROC_ENTITY = 422;
    public static int getPort() {
        String port = System.getenv().getOrDefault("PORT", "5000");
        return Integer.valueOf(port);
    }

    public static String getMode() {
        return System.getenv().getOrDefault("APP_ENV", "development");
    }

    public static boolean isProduction() {
        return getMode().equals("production");
    }
}
