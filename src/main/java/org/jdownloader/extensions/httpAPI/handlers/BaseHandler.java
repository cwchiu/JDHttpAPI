package org.jdownloader.extensions.httpAPI.handlers;

import com.google.gson.Gson;
import jd.controlling.linkcollector.LinkCollector;
import jd.controlling.linkcrawler.CrawledLink;
import jd.plugins.DownloadLink;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.jdownloader.extensions.httpAPI.LinkController;
import org.jdownloader.extensions.httpAPI.ParseException;
import org.jdownloader.extensions.httpAPI.models.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class BaseHandler extends AbstractHandler {

    protected final Gson jparser = new Gson();

    protected final LinkController controller;

    protected BaseHandler(LinkController ctr) {
        super();
        controller = ctr;
    }

    protected abstract AddLinkRequest parseAddLinkParams(HttpServletRequest req) throws ParseException;

    protected abstract String getAllowedMethod();

    private void writeResponse(Request baseRequest, HttpServletResponse response, Object data) throws IOException {
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json; charset=utf-8");

        String origin = baseRequest.getHeader("Origin");
        if (origin != null) {
            response.setHeader("Access-Control-Allow-Origin", origin);
        }

        PrintWriter out = response.getWriter();
        out.println(jparser.toJson(data));
        baseRequest.setHandled(true);
    }

    private void writeError(Request baseRequest, HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        response.setContentType("application/json; charset=utf-8");
        ErrorResponse resp = new ErrorResponse();
        resp.errorMessage = message;
        PrintWriter out = response.getWriter();
        out.println(jparser.toJson(resp));
        baseRequest.setHandled(true);
    }

    public void handle(String target,
                       Request baseRequest,
                       HttpServletRequest request,
                       HttpServletResponse response) throws IOException, ServletException {
        if (!baseRequest.getMethod().equals(getAllowedMethod())) {
            return;
        }

        String path = request.getPathInfo();

        if (path.equals("/addLink")) {
            AddLinkRequest data;
            try {
                data = parseAddLinkParams(request);
            } catch (ParseException e) {
                writeError(baseRequest, response, e.getMessage());
                return;
            }

            if (data.packageName != null && !data.packageName.equals("")) {
                controller.AddLink(data.url.toString(), data.packageName, data.forcePackageName);
            } else {
                controller.AddLink(data.url.toString());
            }

            AddLinkResponse resp = new AddLinkResponse();
            resp.success = true;
            writeResponse(baseRequest, response, resp);
        } else if (path.equals("/queryLinks")) {
            DownloadLinkListResponse resp = new DownloadLinkListResponse();
            resp.success = true;
            List<CrawledLink> c_links = LinkCollector.getInstance().getAllChildren();

            for (CrawledLink link : c_links) {
                DownloadLink dl = link.getDownloadLink();
                Map<String, String> item = new HashMap<String, String>();
                String src = dl.getOriginUrl();
                if (src == null || src.equals("")) {
                    src = link.getURL();
                }
                item.put("OriginUrl", src);
                item.put("RealUrl", dl.getContentUrlOrPatternMatcher());
                item.put("Name", dl.getName());
                item.put("Status", link.getLinkState().name());
                resp.links.add(item);
            }
            writeResponse(baseRequest, response, resp);
        } else {
            //List<DownloadLink> links = DownloadController.getInstance().getAllChildren();

            //for (DownloadLink link : links) {

            //}


            DefaultResponse resp = new DefaultResponse();
            resp.success = false;
            resp.errorMessage = "no handler";
            writeResponse(baseRequest, response, resp);
        }
    }


}
