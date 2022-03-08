package com.revature.database;

import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;
import org.apache.tomcat.util.http.fileupload.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.PortUnreachableException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.fasterxml.jackson.databind.ObjectMapper;

public class App {
    public static void main(String[] args) throws SQLException {
        //This line is for to make a DB connection to SQL and run the script in schema.sql
        Connection connection = DriverManager.getConnection("jdbc:h2:mem:test;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;INIT=runscript from 'classpath:schema.sql'", "sa","");
        //after the above line we run the query and get the entire set of data printed and stored in rs as a resultset
        ResultSet rs = connection.prepareStatement("select * from Artist").executeQuery();
        //Next line creation is before the loop since we will create the list that way
        List<String> artists = new ArrayList<>();
        //we will loop through the data now and reading each letter
        while (rs.next()){
            artists.add(rs.getString("Name"));
        }
        // Creating an HttpServlet
        HttpServlet artistServlet = new HttpServlet() {
            @Override
            protected void doGet(HttpServletRequest req, HttpServletResponse resp)
                    throws ServletException, IOException {
            //Getting a json mapper
            ObjectMapper mapper = new ObjectMapper();
            //we use the mapper to get the artist names stored in it, and store that as another variable results pass it on
            String results = mapper.writeValueAsString(artists);
            //we are setting the content type to json. This maybe important since the api call we will make requires that
            resp.setContentType("application/json");
            //when we want to print from a mapper always used getWriter
            resp.getWriter().println(results);
            }

            @Override
            protected void doPost(HttpServletRequest req, HttpServletResponse resp)
                    throws ServletException, IOException {
                ObjectMapper mapper = new ObjectMapper();
                String newArtist = mapper.readValue(req.getInputStream(), String.class);
                try {
                    PreparedStatement stmt = connection.prepareStatement("insert into 'artist' values (?,?)")
                    stmt.setInt(1, 3);
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        };

        Tomcat server = new Tomcat();
        
        //This will start the localhost 8080 Connector
        server.getConnector();
        server.addContext("",null);
        
        //Ananymous Class
        server.addServlet("", "defaultServlet", new HttpServlet() {
            @Override
            protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
                System.out.println("This is the path information requested from doGet" + req.getPathInfo());
                String filename = req.getPathInfo();//.replaceFirst("/","");
                String resourcesDir = "static";
                //This part is to load the file. It also requires
                InputStream file = getClass().getClassLoader().getResourceAsStream(resourcesDir + filename);
                //Question: is Mime type convert the file type or modifies it or leaves it the same but just inform the Http
                String mimeType = getServletContext().getMimeType(filename);
                resp.setContentType(mimeType);
                //Question: By copying the file with the response, what are we doing exactly?
                IOUtils.copy(file,resp.getOutputStream());
                //super.doGet(req, resp);
            }
        }).addMapping("/*");
        //next line we are adding the information printed in the above artistServlet and passing it on to the server
        server.addServlet("", "artistServlet", artistServlet).addMapping("/artists");;//Question:why both of them are supposed to be the same
        try {
            server.start();
        } catch (LifecycleException e) {
            //There extra println error messages for me to see what kind of error messages will be printed
            System.err.println("Failed to start server in the main App :"+ e.getMessage());
            System.err.println("Failed to start server in the main App :"+ e.getLocalizedMessage());
            System.err.println("Failed to start server in the main App :"+ e.getCause());
            System.err.println("Failed to start server in the main App :"+ e.getStackTrace());
            System.err.println("Failed to start server in the main App :"+ e.getSuppressed());
            e.printStackTrace();
        }
    }
    static void Wait(){
        Scanner scan = new Scanner(System.in);
        System.out.println("Press any key to continue");
        if (scan.nextLine() == " "){
            System.out.println("OK lets move on");
        } else System.out.println("press space");
    }
}
