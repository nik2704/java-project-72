package hexlet.code.controllers;

import io.javalin.http.Handler;
import lombok.Getter;

public final class RootController {

    @Getter
    private static Handler welcome = ctx -> {
        ctx.render("index.html");
    };
}
