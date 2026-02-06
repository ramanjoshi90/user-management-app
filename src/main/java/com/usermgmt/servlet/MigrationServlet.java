package com.usermgmt.servlet;

import com.google.cloud.bigquery.*;
import com.google.cloud.datastore.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/api/migrate")
public class MigrationServlet extends HttpServlet {

    private final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
    private final BigQuery bigquery = BigQueryOptions.getDefaultInstance().getService();

    // Configuration
    private static final String DATASET_NAME = "user_backup";
    private static final String TABLE_NAME = "users";

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            // 1. EXTRACT: Fetch all users from Datastore
            Query<Entity> query = Query.newEntityQueryBuilder().setKind("User").build();
            QueryResults<Entity> results = datastore.run(query);

            // 2. TRANSFORM: Prepare BigQuery Insert Request
            InsertAllRequest.Builder insertBuilder = InsertAllRequest.newBuilder(TableId.of(DATASET_NAME, TABLE_NAME));
            int count = 0;

            while (results.hasNext()) {
                Entity entity = results.next();
                
                // Map Datastore Entity -> BigQuery Row
                Map<String, Object> rowContent = new HashMap<>();
                rowContent.put("email", entity.getKey().getName());
                rowContent.put("name", entity.getString("name"));
                rowContent.put("dob", entity.getString("dob"));
                rowContent.put("password", entity.getString("password"));
                rowContent.put("phone", entity.getString("phone"));
                rowContent.put("gender", entity.getString("gender"));
                rowContent.put("address", entity.getString("address"));

                insertBuilder.addRow(rowContent);
                count++;
            }

            if (count == 0) {
                resp.getWriter().write("No data found in Datastore to migrate.");
                return;
            }

            // 3. LOAD: Send batch to BigQuery
            InsertAllResponse response = bigquery.insertAll(insertBuilder.build());

            if (response.hasErrors()) {
                // Handle Partial Errors
                Map<Long, java.util.List<BigQueryError>> insertErrors = response.getInsertErrors();
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                resp.getWriter().write("Migration Failed with errors: " + insertErrors.toString());
            } else {
                resp.getWriter().write("Migration Successful! Moved " + count + " records to BigQuery.");
            }

        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            e.printStackTrace(resp.getWriter());
        }
    }
}