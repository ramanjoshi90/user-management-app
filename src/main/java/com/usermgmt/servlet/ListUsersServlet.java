package com.usermgmt.servlet;

import com.google.cloud.datastore.*;
import com.google.gson.Gson;
import com.usermgmt.model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/api/users")
public class ListUsersServlet extends HttpServlet {

    private final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // 1. Setup Defaults
        int limit = 20; // Only load 20 at a time
        String cursorString = req.getParameter("cursor");

        // 2. Build the Query
        String kind = System.getenv("DATASTORE_KIND");
        EntityQuery.Builder queryBuilder = Query.newEntityQueryBuilder()
                .setKind(kind)
                .setLimit(limit);

        // 3. Apply Cursor if present (Pagination logic)
        if (cursorString != null && !cursorString.isEmpty()) {
            queryBuilder.setStartCursor(Cursor.fromUrlSafe(cursorString));
        }

        Query<Entity> query = queryBuilder.build();
        QueryResults<Entity> results = datastore.run(query);

        List<User> users = new ArrayList<>();

        // 4. Convert Entities to POJOs
        while (results.hasNext()) {
            Entity entity = results.next();
            users.add(new User(
                    entity.getKey().getName(),
                    entity.getString("name"),
                    entity.getString("dob"),
                    "PROTECTED", // Password Masking
                    entity.getString("phone"),
                    entity.getString("gender"),
                    entity.getString("address")));
        }

        // 5. Get the Cursor for the NEXT page
        String nextCursor = results.getCursorAfter().toUrlSafe();

        // 6. Construct the Response Object manually (or use a Map)
        // We create a simple Map to hold both the list and the cursor
        Map<String, Object> jsonResponse = new HashMap<>();
        jsonResponse.put("users", users);
        jsonResponse.put("nextCursor", nextCursor);

        // 7. Send JSON
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().write(gson.toJson(jsonResponse));
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String email = req.getParameter("email");

        if (email != null && !email.isEmpty()) {
            String kind = System.getenv("DATASTORE_KIND");
            Key key = datastore.newKeyFactory().setKind(kind).newKey(email);
            datastore.delete(key);
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write("User deleted successfully.");
        } else {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Email parameter is missing.");
        }
    }
}