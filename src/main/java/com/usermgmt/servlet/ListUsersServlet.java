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
import java.util.List;

@WebServlet("/api/users")
public class ListUsersServlet extends HttpServlet {

    private final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // 1. Query all entities of Kind "User"
        Query<Entity> query = Query.newEntityQueryBuilder()
                .setKind("User")
                .build();
        
        QueryResults<Entity> results = datastore.run(query);
        List<User> users = new ArrayList<>();

        // 2. Convert Datastore Entities to Java Objects
        while (results.hasNext()) {
            Entity entity = results.next();
            users.add(new User(
                    entity.getKey().getName(), // Email is the Key Name
                    entity.getString("name"),
                    entity.getString("dob"),
                    "PROTECTED", // Don't send real password to UI
                    entity.getString("phone"),
                    entity.getString("gender"),
                    entity.getString("address")
            ));
        }

        // 3. Return as JSON
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().write(gson.toJson(users));
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String email = req.getParameter("email");
        
        if (email != null && !email.isEmpty()) {
            Key key = datastore.newKeyFactory().setKind("User").newKey(email);
            datastore.delete(key);
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write("User deleted successfully.");
        } else {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Email parameter is missing.");
        }
    }
}