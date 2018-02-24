package org.jdownloader.extensions.httpAPI.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DownloadLinkListResponse extends DefaultResponse {
    public final List<Map<String, String>> links = new ArrayList<Map<String, String>>();
}
