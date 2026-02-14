package com.usermgmt.servlet;

import com.google.cloud.datastore.*;

import com.usermgmt.util.PasswordUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/api/login")
public class LoginServlet extends HttpServlet {

    private static final Logger log = Logger.getLogger(LoginServlet.class.getName());
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
            String kind = System.getenv("DATASTORE_KIND");
            Key key = datastore.newKeyFactory().setKind(kind).newKey(email);
            Entity userEntity = datastore.get(key);

            // 4. Validate User
            if (userEntity == null) {
                resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                resp.getWriter().write("User not found in database.");
                return;
            }

            // 5. Validate Password
            String storedHash = userEntity.getString("password");

            if (PasswordUtil.checkPassword(password, storedHash)) {
                HttpSession session = req.getSession();
                session.setAttribute("user", email);
                resp.getWriter().write("Login Success");
            } else {
                resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                resp.getWriter().write("Invalid Credentials");
            }

        } catch (Exception e) {
            // Log the REAL error internally
            log.log(Level.SEVERE, "Login failed unexpectedly", e);

            // Show a GENERIC message to the user
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("An internal server error occurred. Please try again later.");
        }
    }
}