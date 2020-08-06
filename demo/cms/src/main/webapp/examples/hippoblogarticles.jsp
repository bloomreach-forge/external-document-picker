<%--
  Copyright 2020 BloomReach, Inc. (https://www.bloomreach.com)

  Licensed under the Apache License, Version 2.0
  (the "License"); you may not use this file except in compliance with the
  License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
  Unless required by applicable law or agreed to in writing, software distributed
  under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES
  OR CONDITIONS OF ANY KIND, either express or implied. See the License for
  the specific language governing permissions and limitations under the License.
--%>
<%--
Simple JSON REST Service implementation only for demo purpose, supporting "?q=<search term>" parameter for filtering.
--%>
<%@ page contentType="application/json" %>
<%@ page import="java.util.*" %>
<%@ page import="org.apache.commons.lang3.*" %>
<%@ page import="org.apache.commons.io.*" %>
<%@ page import="net.sf.json.*" %>

<%!
private JSONObject transform(final JSONObject source) {
    final JSONObject target = new JSONObject();
    target.put("id", source.getString("data-custom1"));
    target.put("title", source.getString("title"));
    target.put("description", source.getString("description"));
    target.put("thumbnail", "/cms/images/onehippo-default.png");
    return target;
}
%>

<%
final String data = IOUtils.toString(application.getResource("/WEB-INF/hippoblogarticles.json"), "UTF-8");
final JSONArray jsonData = JSONArray.fromObject(data);
final String id = request.getParameter("id");
final String query = request.getParameter("q");

if (StringUtils.isNotBlank(id)) {
    for (Object item : jsonData) {
        final JSONObject sourceItem = (JSONObject) item;
        if (StringUtils.equals(sourceItem.getString("data-custom1"), id)) {
            final JSONObject targetItem = transform(sourceItem);
            out.print(targetItem);
            break;
        }
    }
} else {
    if (StringUtils.isNotBlank(query)) {
        for (Iterator it = jsonData.iterator(); it.hasNext(); ) {
            final JSONObject item = (JSONObject) it.next();
            if (!StringUtils.containsIgnoreCase(item.getString("title"), query)) {
                it.remove();
            }
        }
    }

    final int size = jsonData.size();
    out.println("[");

    for (int i = 0; i < size; i++) {
      final JSONObject targetItem = transform((JSONObject) jsonData.get(i));
      out.print(targetItem);
      out.println((i < size - 1) ? ",": "");
    }

    out.println("]");
}
%>
