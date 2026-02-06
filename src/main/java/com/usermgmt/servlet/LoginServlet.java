package com.usermgmt.servlet;

import com.google.cloud.datastore.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/api/login")
public class LoginServlet extends HttpServlet {

    private final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            // 1. Capture Inputs
            String email = req.getParameter("email");
            String password = req.getParameter("password");

            // 2. Debug: If email is null, throw error immediately
            if (email == null) {
                throw new IllegalArgumentException("Input 'email' is NULL. Check Frontend Payload.");
            }

            // 3. Datastore Lookup
            Key key = datastore.newKeyFactory().setKind("User").newKey(email);
            Entity userEntity = datastore.get(key);

            // 4. Validate User
            if (userEntity == null) {
                resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                resp.getWriter().write("User not found in database.");
                return;
            }

            // 5. Validate Password
            if (userEntity.getString("password").equals(password)) {
                HttpSession session = req.getSession();
                session.setAttribute("user", email);
                resp.getWriter().write("Login Success");
            } else {
                resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                resp.getWriter().write("Invalid Credentials");
            }

        } catch (Exception e) {
            // !!! THIS IS THE FIX !!!
            // Send the actual crash error to the browser so you can read it.
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); // 500
            e.printStackTrace(resp.getWriter()); 
        }
    }
}