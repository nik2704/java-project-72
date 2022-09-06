package hexlet.code.utils;

public class Env {
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