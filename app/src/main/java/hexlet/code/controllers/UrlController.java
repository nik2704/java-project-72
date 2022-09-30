package hexlet.code.controllers;

import hexlet.code.utils.LoggerFactory;
import hexlet.code.domain.Url;
import hexlet.code.domain.UrlCheck;
import hexlet.code.domain.query.QUrl;
import hexlet.code.domain.query.QUrlCheck;
import hexlet.code.utils.Env;
import hexlet.code.utils.Parser;
import io.ebean.PagedList;
import io.javalin.http.Handler;
import io.javalin.http.NotFoundResponse;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import lombok.Getter;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.validator.routines.UrlValidator;
import org.jsoup.nodes.Document;


import static hexlet.code.utils.Env.UNPROC_ENTITY;
import static hexlet.code.utils.Parser.getBody;


public final class UrlController {

    private static final Logger LOGGER = LoggerFactory.getLogger(Parser.class);

    @Getter
    private static Handler checkUrl = ctx -> {
        int id = ctx.pathParamAsClass("id", Integer.class).getOrDefault(null);

        Url url = new QUrl()
                .id.equalTo(id)
                .findOne();

        if (url == null) {
            throw new NotFoundResponse();
        }

        try {
            HttpResponse<String> response = Unirest.get(url.getName()).asString();

            int statusCode = response.getStatus();
            Document body = getBody(response.getBody());
            String title = body.title();
            String description = null;
            String h1 = null;

            if (body.selectFirst("meta[name=description]") != null) {
                description = body.selectFirst("meta[name=description]").attr("content");
            }


            if (body.selectFirst("h1") != null) {
                h1 = body.selectFirst("h1").text();
            }

            UrlCheck urlCheck = new UrlCheck(statusCode, title, h1, description, url);
            urlCheck.save();

            ctx.sessionAttribute("flash", "Страница успешно проверена");
            ctx.sessionAttribute("flash-type", "success");

        } catch (Exception e) {
            ctx.sessionAttribute("flash", "Ошибка проверки сайта (" + url.getName() + ")");
            ctx.sessionAttribute("flash-type", "danger");

            LOGGER.log(
                    Level.WARNING,
                    "Attempt to check the URL: " + url.getName() + " was failed: " + e.getMessage()
            );
        }

        ctx.redirect("/urls/" + id);
    };

    @Getter
    private static Handler newUrl = ctx -> {
        String newUrlRequested = Parser.getUrlFormatted(ctx.formParam("url"));

        UrlValidator urlValidator = new UrlValidator(UrlValidator.ALLOW_LOCAL_URLS + UrlValidator.ALLOW_ALL_SCHEMES);

        if (!urlValidator.isValid(newUrlRequested)) {
            ctx.status(UNPROC_ENTITY);
            ctx.sessionAttribute("flash", "Некорректный URL");
            ctx.sessionAttribute("flash-type", "danger");
            ctx.redirect("/");
            return;
        }

        Url url = new QUrl()
                .name.equalTo(newUrlRequested)
                .findOne();

        if (url != null) {
            ctx.sessionAttribute("flash", "Страница уже существует");
            ctx.sessionAttribute("flash-type", "info");
        } else {
            Url newUrlCreation = new Url(newUrlRequested);
            newUrlCreation.save();
            ctx.sessionAttribute("flash", "Страница успешно добавлена");
            ctx.sessionAttribute("flash-type", "success");
        }
        ctx.redirect("/urls");
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

        if (url == null) {
            throw new NotFoundResponse();
        }

        List<UrlCheck> urlChecks = new QUrlCheck()
                .url.id.equalTo(id)
                .orderBy()
                .id
                .desc()
                .findList();

        ctx.attribute("url", url);
        ctx.attribute("urlChecks", urlChecks);
        ctx.render("urls/show.html");
    };
}
