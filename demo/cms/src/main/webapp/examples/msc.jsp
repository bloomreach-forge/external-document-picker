<%--
  Copyright 2024 Bloomreach, Inc. (https://www.bloomreach.com)

  Licensed under the Apache License, Version 2.0
  (the "License"); you may not use this file except in compliance with the
  License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
  Unless required by applicable law or agreed to in writing, software distributed
  under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES
  OR CONDITIONS OF ANY KIND, either express or implied. See the License for
  the specific language governing permissions and limitations under the License.
--%>
<%--
Simple JSON REST Service implementation only for demo purpose, serializing example tree data (/WEB-INF/msc.json).
--%>
<%@ page contentType="application/json" %>
<%@ page import="org.apache.commons.lang3.*" %>
<%@ page import="org.apache.commons.io.*" %>
<%@ page import="net.sf.json.*" %><%@ page import="java.nio.charset.StandardCharsets"%>

<%
final String data = IOUtils.toString(application.getResource("/WEB-INF/msc.json"), StandardCharsets.UTF_8);
final JSONArray jsonData = JSONArray.fromObject(data);
final String id = request.getParameter("id");

if (StringUtils.isNotBlank(id)) {
    for (Object item : jsonData) {
        final JSONObject sourceItem = (JSONObject) item;
        if (StringUtils.equals(sourceItem.getString("id"), id)) {
            out.print(sourceItem);
            break;
        }
    }
} else {
    out.println(jsonData);
}
%>
