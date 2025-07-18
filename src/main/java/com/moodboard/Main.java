package com.moodboard;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.io.*;
import java.net.InetSocketAddress;
import java.awt.Desktop;
import java.net.URI;
import java.util.concurrent.Executors;

public class Main {
    private static final int PORT = 8080;
    private static final String BASE_URL = "http://localhost:" + PORT;
    
    public static void main(String[] args) {
        try {
            // Create HTTP server
            HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);
            
            // Set up handlers for static files
            server.createContext("/", new StaticFileHandler());
            server.createContext("/index.html", new StaticFileHandler());
            server.createContext("/script.js", new StaticFileHandler());
            server.createContext("/style.css", new StaticFileHandler());
            
            // Use a thread pool for handling requests
            server.setExecutor(Executors.newFixedThreadPool(4));
            
            // Start the server
            server.start();
            System.out.println("MoodBoard server started on " + BASE_URL);
            
            // Open browser automatically
            openBrowser(BASE_URL);
            
            // Keep the application running
            System.out.println("Press Ctrl+C to stop the server...");
            
            // Add shutdown hook to gracefully stop the server
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                System.out.println("\nShutting down server...");
                server.stop(0);
            }));
            
        } catch (Exception e) {
            System.err.println("Error starting server: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void openBrowser(String url) {
        try {
            if (Desktop.isDesktopSupported()) {
                Desktop desktop = Desktop.getDesktop();
                if (desktop.isSupported(Desktop.Action.BROWSE)) {
                    desktop.browse(new URI(url));
                    System.out.println("Browser opened automatically");
                    return;
                }
            }
            
            // Fallback for different operating systems
            String os = System.getProperty("os.name").toLowerCase();
            if (os.contains("win")) {
                Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + url);
            } else if (os.contains("mac")) {
                Runtime.getRuntime().exec("open " + url);
            } else if (os.contains("nix") || os.contains("nux")) {
                Runtime.getRuntime().exec("xdg-open " + url);
            }
            System.out.println("Browser opened via system command");
        } catch (Exception e) {
            System.err.println("Could not open browser automatically. Please visit: " + url);
        }
    }
    
    static class StaticFileHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String path = exchange.getRequestURI().getPath();
            
            // Default to index.html for root path
            if (path.equals("/")) {
                path = "/index.html";
            }
            
            // Remove leading slash for resource loading
            String resourcePath = path.startsWith("/") ? path.substring(1) : path;
            
            try (InputStream is = getClass().getClassLoader().getResourceAsStream("static/" + resourcePath)) {
                if (is == null) {
                    // File not found
                    String response = "404 - File Not Found";
                    exchange.sendResponseHeaders(404, response.length());
                    try (OutputStream os = exchange.getResponseBody()) {
                        os.write(response.getBytes());
                    }
                    return;
                }
                
                // Set appropriate content type
                String contentType = getContentType(resourcePath);
                exchange.getResponseHeaders().set("Content-Type", contentType);
                
                // Read file content
                byte[] content = is.readAllBytes();
                
                // Send response
                exchange.sendResponseHeaders(200, content.length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(content);
                }
                
            } catch (Exception e) {
                String response = "500 - Internal Server Error";
                exchange.sendResponseHeaders(500, response.length());
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(response.getBytes());
                }
                e.printStackTrace();
            }
        }
        
        private String getContentType(String filename) {
            if (filename.endsWith(".html")) return "text/html";
            if (filename.endsWith(".css")) return "text/css";
            if (filename.endsWith(".js")) return "application/javascript";
            if (filename.endsWith(".json")) return "application/json";
            if (filename.endsWith(".png")) return "image/png";
            if (filename.endsWith(".jpg") || filename.endsWith(".jpeg")) return "image/jpeg";
            if (filename.endsWith(".gif")) return "image/gif";
            return "text/plain";
        }
    }
}