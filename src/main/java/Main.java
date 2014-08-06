import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.*;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.*;
import java.io.*;

import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.DocumentException;

public class Main extends HttpServlet {
  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    if (req.getRequestURI().endsWith("/pdf")) {
      resp.setContentType("application/pdf");
      OutputStream out = resp.getOutputStream();

      try {
        Document document = new Document();

        PdfWriter.getInstance(document, out);
        document.open();
        document.add(new Paragraph("Hello from Java!"));
        document.close();
      } catch (DocumentException exc) {
        throw new IOException(exc.getMessage());
      } finally {
        out.close();
      }
    } else {
      resp.getWriter().print("Hello from Java!");
    }
  }

  public static void main(String[] args) throws Exception{
    Server server = new Server(Integer.valueOf(System.getenv("PORT")));
    ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
    context.setContextPath("/");
    server.setHandler(context);
    context.addServlet(new ServletHolder(new Main()),"/*");
    server.start();
    server.join();
  }
}
