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
import java.util.Scanner;

public class App {
    public static void main(String[] args) {
        Tomcat server = new Tomcat();
        /**
         * This will start the localhost 8080 Connector
         */
        server.getConnector();
        server.addContext("",null);
        server.addServlet("", "defaultServlet", new HttpServlet() {
            @Override
            protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
                System.out.println("This is the path information requested from doGet" + req.getPathInfo());
                String filename = req.getPathInfo();//.replaceFirst("/","");
                String resourcesDir = "static";
                /**
                 * This part is to load the file. It also requires
                 */
                InputStream file = getClass().getClassLoader().getResourceAsStream(resourcesDir + filename);
                /**
                 * Question: is Mime type convert the file type or modifies it or leaves it the same but just inform the Http
                 */
                String mimeType = getServletContext().getMimeType(filename);
                resp.setContentType(mimeType);
                /**
                 * Question: By copying the file with the response, what are we doing exactly?
                 */
                IOUtils.copy(file,resp.getOutputStream());

                //super.doGet(req, resp);
            }
        }).addMapping("/*");
        try {
            server.start();
        } catch (LifecycleException e) {
            /**
             * There extra println error messages for me to see what kind of error messages will be printed
             */
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
        scan.nextLine();
    }
}
