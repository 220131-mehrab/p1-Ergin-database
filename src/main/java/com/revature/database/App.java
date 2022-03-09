package com.revature.database;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;
import org.apache.tomcat.util.http.fileupload.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.fasterxml.jackson.databind.ObjectMapper;

class Artist {
    private int artistId;
    private String name;
    public Artist(int artistId, String name) {
        this.artistId = artistId;
        this.name = name;
    }
    public Artist() {
    }
    public int getArtistId() {
        return artistId;
    }
    public void setArtistId(int artistId) {
        this.artistId = artistId;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    @Override
    public String toString() {
        return "Artist [artistId=" + artistId + ", name=" + name + "]";
    }
}

public class App {
    public static void main(String[] args) throws SQLException {
        Connection connection = DriverManager
        .getConnection("jdbc:h2:mem:test;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;INIT=runscript from 'classpath:schema.sql'", "sa","");

        HttpServlet artistServlet = new HttpServlet() {
            @Override
            protected void doGet(HttpServletRequest req, HttpServletResponse resp)
                    throws ServletException, IOException {
            List<Artist> artists = new ArrayList<>();
            try {
                ResultSet rs = connection.prepareStatement("select * from Artist").executeQuery();
            while (rs.next()){
                artists.add(new Artist(rs.getInt("ArtistId"), rs.getString("Name")));
                }
            } catch (SQLException e) {
                System.err.println("Failed to retrieve from DB: " + e.getSQLState());
            }

            ObjectMapper mapper = new ObjectMapper();
            String results = mapper.writeValueAsString(artists);
            resp.setContentType("application/json");
            resp.getWriter().println(results);
        }

            @Override
            protected void doPost(HttpServletRequest req, HttpServletResponse resp)
                    throws ServletException, IOException {

                ObjectMapper mapper = new ObjectMapper();
                Artist newArtist = mapper.readValue(req.getInputStream(), Artist.class);
                System.out.println(newArtist);
                try {
                    PreparedStatement stmt = connection.prepareStatement("insert into Artist values (?,?)");
                    stmt.setInt(1, newArtist.getArtistId());
                    stmt.setString(2, newArtist.getName());
                    stmt.executeUpdate();
                } catch (SQLException e) {
                    System.err.println("Failed to insert" + e.getMessage());
                }
            }
        };
        Tomcat server = new Tomcat();
        server.getConnector();
        server.addContext("",null);
        server.addServlet("", "defaultServlet", new HttpServlet() {
            @Override
            protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
                System.out.println("This is the path information requested from doGet" + req.getPathInfo());
                String filename = req.getPathInfo();
                String resourcesDir = "static";
                InputStream file = getClass().getClassLoader().getResourceAsStream(resourcesDir + filename);
                String mimeType = getServletContext().getMimeType(filename);
                resp.setContentType(mimeType);
                IOUtils.copy(file,resp.getOutputStream());
            }
        }).addMapping("/*");
        server.addServlet("", "artistServlet", artistServlet).addMapping("/artists");;//Question:why both of them are supposed to be the same
        try {
            server.start();
        } catch (LifecycleException e) {
            System.err.println("Failed to start server in the main App :"+ e.getMessage());
            e.printStackTrace();
        }
    }

    static void Wait(){
        Scanner scan = new Scanner(System.in);
        System.out.println("Press any key to continue");
        if (scan.nextLine() == " "){
            System.out.println("OK lets move on");
            scan.close();
        } else System.out.println("press space");
        
    }
}
