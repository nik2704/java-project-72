package hexlet.code.controllers;

import hexlet.code.domain.Url;
import hexlet.code.domain.query.QUrl;
import hexlet.code.utils.Env;
import hexlet.code.utils.Parser;
import io.ebean.PagedList;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import lombok.Getter;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.validator.routines.UrlValidator;

import javax.servlet.http.HttpServletResponse;

import static hexlet.code.utils.Env.UNPROC_ENTITY;


public final class UrlController {
    @Getter
    private static Handler newUrl = ctx -> {
        String newUrlRequested = Parser.getUrlFormatted(ctx.formParam("url"));

        UrlValidator urlValidator = new UrlValidator();
        if (urlValidator.isValid(newUrlRequested)) {
            Optional<Url> url = new QUrl()
                        .name.equalTo(newUrlRequested)
                        .findOneOrEmpty();
            if (url.isPresent()) {
                redirect(ctx, "/urls", "Страница уже существует", "info");
            } else {
                Url newUrlCreation = new Url(newUrlRequested);
                newUrlCreation.save();

                redirect(ctx, "/urls", "Страница успешно добавлена", "success");
            }
        } else {
            ctx.status(UNPROC_ENTITY);
            render(ctx, "index.html", "Некорректный URL", "danger");
        }
    };

    @Getter
    private static Handler listUrls = ctx -> {
        int page = ctx.queryParamAsClass("page", Integer.class).getOrDefault(1) - 1;
        int rowsPerPage = Env.ITEMS_PER_PAGE;

        PagedList<Url> pagedUrls = new QUrl()
                .setFirstRow(page * rowsPerPage)
                .setMaxRows(rowsPerPage)
                .orderBy()
                    .id.asc()
                .findPagedList();

        List<Url> urls = pagedUrls.getList();

        int lastPage = pagedUrls.getTotalPageCount() + 1;
        int currentPage = pagedUrls.getPageIndex() + 1;
        List<Integer> pages = IntStream
                .range(1, lastPage)
                .boxed()
                .collect(Collectors.toList());

        ctx.attribute("urls", urls);
        ctx.attribute("pages", pages);
        ctx.attribute("currentPage", currentPage);

        ctx.render("urls/list.html");
    };

    @Getter
    private static Handler showUrl = ctx -> {
        long id = ctx.pathParamAsClass("id", Long.class).getOrDefault(null);

        Url url = new QUrl()
                .id.equalTo(id)
                .findOne();

        if (url != null) {
            ctx.attribute("url", url);
            render(ctx, "urls/show.html", null, null);
        } else {
            ctx.status(HttpServletResponse.SC_NOT_FOUND);
            render(ctx, "index.html", "Некорректный ID (" + id + ")", "danger");
        }
    };

    private static void setAlert(Context ctx, String msg, String flash) {
        if (msg != null) {
            ctx.sessionAttribute("flash", msg);
        }

        if (flash != null) {
            ctx.sessionAttribute("flash-type", flash);
        }
    }

    private static void render(Context ctx, String target, String msg, String flash) {
        setAlert(ctx, msg, flash);
        ctx.render(target);
    }

    private static void redirect(Context ctx, String target, String msg, String flash) {
        setAlert(ctx, msg, flash);
        ctx.redirect(target);
    }
}
