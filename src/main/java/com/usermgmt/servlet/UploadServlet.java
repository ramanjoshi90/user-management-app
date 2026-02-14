package com.usermgmt.servlet;

import com.google.cloud.datastore.*;

import com.usermgmt.util.PasswordUtil;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/api/upload")
@MultipartConfig // Essential for file uploads!
public class UploadServlet extends HttpServlet {

    private final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // 1. SECURITY CHECK: Verify Admin Key
        String secret = System.getenv("ADMIN_SECRET");
        String requestKey = req.getHeader("X-Admin-Key");

        // If secret is set in config, strict check is required
        if (secret == null || requestKey == null || !secret.trim().equals(requestKey.trim())) {
            // Set the 403 status code manually
            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
            // Send a clean, plain-text response (no HTML tags)
            resp.getWriter().write("Access Denied: Invalid Admin Key");
            return; // Stop execution
        }

        Part filePart = req.getPart("file"); // Matches HTML input name="file"
        InputStream fileContent = filePart.getInputStream();

        List<Entity> userEntities = new ArrayList<>();
        String kind = System.getenv("DATASTORE_KIND");
        KeyFactory keyFactory = datastore.newKeyFactory().setKind(kind);

        try (Workbook workbook = new XSSFWorkbook(fileContent)) {
            Sheet sheet = workbook.getSheetAt(0);

            // Iterate rows (Skip header row 0)
            for (Row row : sheet) {
                if (row.getRowNum() == 0)
                    continue;

                // Basic parsing (Assuming strict column order: Name, DOB, Email, Password,
                // Phone, Gender, Address)

                String name = getCellValue(row.getCell(0));
                String dob = getCellValue(row.getCell(1));
                String email = getCellValue(row.getCell(2));
                String password = getCellValue(row.getCell(3));
                String phone = getCellValue(row.getCell(4));
                String gender = getCellValue(row.getCell(5));
                String address = getCellValue(row.getCell(6));

                if (email == null || email.isEmpty())
                    continue;

                // Create Datastore Entity
                // We use Email as the Key (Primary Key) so duplicates overwrite
                Key key = keyFactory.newKey(email);
                Entity entity = Entity.newBuilder(key)
                        .set("name", name)
                        .set("dob", dob)
                        .set("email", email)
                        .set("password", PasswordUtil.hashPassword(password))
                        .set("phone", phone)
                        .set("gender", gender)
                        .set("address", address)
                        .build();

                userEntities.add(entity);
            }
        } catch (Exception e) {
            resp.sendError(500, "Error parsing Excel: " + e.getMessage());
            return;
        }

        // Batch save to Datastore (Better performance than saving one by one)
        if (!userEntities.isEmpty()) {
            datastore.put(userEntities.toArray(new Entity[0]));
        }

        resp.getWriter().write("Upload Successful! Processed " + userEntities.size() + " users.");
    }

    // Helper to handle different cell types (String vs Numeric)
    private String getCellValue(Cell cell) {
        if (cell == null)
            return "";
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                return String.valueOf((long) cell.getNumericCellValue()); // Cast to long to avoid 1.0
            default:
                return "";
        }
    }
}