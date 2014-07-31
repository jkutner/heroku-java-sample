import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.*;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.*;

public class HelloWorld extends HttpServlet {
      @Override
      protected void doGet(HttpServletRequest req, HttpServletResponse resp)
          throws ServletException, IOException {
        try {
          Connection connection = getConnection();
          
          Statement stmt = connection.createStatement();
          stmt.executeUpdate("DROP TABLE IF EXISTS ticks");
          stmt.executeUpdate("CREATE TABLE ticks (tick timestamp)");
          stmt.executeUpdate("INSERT INTO ticks VALUES (now())");
          ResultSet rs = stmt.executeQuery("SELECT tick FROM ticks");
          
          String out = "Hello!\n";
          while (rs.next()) {
              out += "Read from DB: " + rs.getTimestamp("tick") + "\n";
          }
          
          resp.getWriter().print(out);
        } catch (URISyntaxException e) {
          resp.getWriter().print("There was an error");
        } catch (SQLException e) {
          resp.getWriter().print("There was an error");
        }
      }

      public static void main(String[] args) throws Exception{
        Server server = new Server(Integer.valueOf(System.getenv("PORT")));
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        server.setHandler(context);
        context.addServlet(new ServletHolder(new HelloWorld()),"/*");
        server.start();
        server.join();
      }

      private Connection getConnection() throws URISyntaxException, SQLException {
        URI dbUri = new URI(System.getenv("DATABASE_URL"));

        String username = dbUri.getUserInfo().split(":")[0];
        String password = dbUri.getUserInfo().split(":")[1];
        String dbUrl = "jdbc:postgresql://" + dbUri.getHost() + dbUri.getPath();

        return DriverManager.getConnection(dbUrl, username, password);
      }
}
