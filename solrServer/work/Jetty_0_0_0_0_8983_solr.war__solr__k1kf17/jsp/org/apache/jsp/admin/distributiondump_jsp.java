package org.apache.jsp.admin;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;
import org.apache.solr.core.SolrCore;
import org.apache.solr.schema.IndexSchema;
import java.io.BufferedReader;
import java.io.File;
import java.io.FilenameFilter;
import java.io.FileReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import org.apache.solr.core.SolrConfig;
import org.apache.solr.core.SolrCore;
import org.apache.solr.schema.IndexSchema;
import java.io.File;
import java.net.InetAddress;
import java.io.StringWriter;
import org.apache.solr.core.Config;
import org.apache.solr.common.util.XML;
import org.apache.solr.common.SolrException;
import org.apache.lucene.LucenePackage;
import java.net.UnknownHostException;

public final class distributiondump_jsp extends org.apache.jasper.runtime.HttpJspBase
    implements org.apache.jasper.runtime.JspSourceDependent {

  private static final JspFactory _jspxFactory = JspFactory.getDefaultFactory();

  private static java.util.Vector _jspx_dependants;

  static {
    _jspx_dependants = new java.util.Vector(2);
    _jspx_dependants.add("/admin/header.jsp");
    _jspx_dependants.add("/admin/_info.jsp");
  }

  private org.apache.jasper.runtime.ResourceInjector _jspx_resourceInjector;

  public Object getDependants() {
    return _jspx_dependants;
  }

  public void _jspService(HttpServletRequest request, HttpServletResponse response)
        throws java.io.IOException, ServletException {

    PageContext pageContext = null;
    HttpSession session = null;
    ServletContext application = null;
    ServletConfig config = null;
    JspWriter out = null;
    Object page = this;
    JspWriter _jspx_out = null;
    PageContext _jspx_page_context = null;


    try {
      response.setContentType("text/html; charset=utf-8");
      pageContext = _jspxFactory.getPageContext(this, request, response,
      			null, true, 8192, true);
      _jspx_page_context = pageContext;
      application = pageContext.getServletContext();
      config = pageContext.getServletConfig();
      session = pageContext.getSession();
      out = pageContext.getOut();
      _jspx_out = out;
      _jspx_resourceInjector = (org.apache.jasper.runtime.ResourceInjector) application.getAttribute("com.sun.appserv.jsp.resource.injector");

      out.write('\n');
      out.write("\n");
      out.write("\n");
      out.write("\n");
      out.write("\n");
      out.write('\n');
      out.write("\n");
      out.write("<html>\n");
      out.write("<head>\n");

request.setCharacterEncoding("UTF-8");

      out.write('\n');
      out.write("\n");
      out.write("\n");
      out.write("\n");
      out.write("\n");
      out.write("\n");
      out.write("\n");
      out.write("\n");
      out.write("\n");
      out.write("\n");

  // 
  SolrCore  core = (SolrCore) request.getAttribute("org.apache.solr.SolrCore");
  if (core == null) {
    response.sendError( 404, "missing core name in path" );
    return;
  }
    
  SolrConfig solrConfig = core.getSolrConfig();
  int port = request.getServerPort();
  IndexSchema schema = core.getSchema();

  // enabled/disabled is purely from the point of a load-balancer
  // and has no effect on local server function.  If there is no healthcheck
  // configured, don't put any status on the admin pages.
  String enabledStatus = null;
  String enabledFile = solrConfig.get("admin/healthcheck/text()",null);
  boolean isEnabled = false;
  if (enabledFile!=null) {
    isEnabled = new File(enabledFile).exists();
  }

  String collectionName = schema!=null ? schema.getName():"unknown";
  InetAddress addr = null;
  String hostname = "unknown";
  try {
    addr = InetAddress.getLocalHost();
    hostname = addr.getCanonicalHostName();
  } catch (UnknownHostException e) {
    //default to unknown
  }

  String defaultSearch = "";
  { 
    StringWriter tmp = new StringWriter();
    XML.escapeCharData
      (solrConfig.get("admin/defaultQuery/text()", null), tmp);
    defaultSearch = tmp.toString();
  }

  String solrImplVersion = "";
  String solrSpecVersion = "";
  String luceneImplVersion = "";
  String luceneSpecVersion = "";

  { 
    Package p;
    StringWriter tmp;

    p = SolrCore.class.getPackage();

    tmp = new StringWriter();
    solrImplVersion = p.getImplementationVersion();
    if (null != solrImplVersion) {
      XML.escapeCharData(solrImplVersion, tmp);
      solrImplVersion = tmp.toString();
    }
    tmp = new StringWriter();
    solrSpecVersion = p.getSpecificationVersion() ;
    if (null != solrSpecVersion) {
      XML.escapeCharData(solrSpecVersion, tmp);
      solrSpecVersion = tmp.toString();
    }
  
    p = LucenePackage.class.getPackage();

    tmp = new StringWriter();
    luceneImplVersion = p.getImplementationVersion();
    if (null != luceneImplVersion) {
      XML.escapeCharData(luceneImplVersion, tmp);
      luceneImplVersion = tmp.toString();
    }
    tmp = new StringWriter();
    luceneSpecVersion = p.getSpecificationVersion() ;
    if (null != luceneSpecVersion) {
      XML.escapeCharData(luceneSpecVersion, tmp);
      luceneSpecVersion = tmp.toString();
    }
  }
  
  String cwd=System.getProperty("user.dir");
  String solrHome= solrConfig.getInstanceDir();

      out.write("\n");
      out.write("<script>\n");
      out.write("var host_name=\"");
      out.print( hostname );
      out.write("\"\n");
      out.write("</script>\n");
      out.write("\n");
      out.write("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\n");
      out.write("<link rel=\"stylesheet\" type=\"text/css\" href=\"solr-admin.css\">\n");
      out.write("<link rel=\"icon\" href=\"favicon.ico\" type=\"image/ico\"></link>\n");
      out.write("<link rel=\"shortcut icon\" href=\"favicon.ico\" type=\"image/ico\"></link>\n");
      out.write("<title>Solr admin page</title>\n");
      out.write("</head>\n");
      out.write("\n");
      out.write("<body>\n");
      out.write("<a href=\".\"><img border=\"0\" align=\"right\" height=\"61\" width=\"142\" src=\"solr-head.gif\" alt=\"Solr\"></a>\n");
      out.write("<h1>Solr Admin (");
      out.print( collectionName );
      out.write(')');
      out.write('\n');
      out.print( enabledStatus==null ? "" : (isEnabled ? " - Enabled" : " - Disabled") );
      out.write(" </h1>\n");
      out.write("\n");
      out.print( hostname );
      out.write(':');
      out.print( port );
      out.write("<br/>\n");
      out.write("cwd=");
      out.print( cwd );
      out.write("  SolrHome=");
      out.print( solrHome );
      out.write('\n');
      out.write('\n');
      out.write('\n');

  File slaveinfo = new File(solrHome + "logs/snappuller.status");

  StringBuffer buffer = new StringBuffer();
  StringBuffer buffer2 = new StringBuffer();
  String mode = "";

  if (slaveinfo.canRead()) {
    // Slave instance
    mode = "Slave";
    File slavevers = new File(solrHome + "logs/snapshot.current");
    BufferedReader inforeader = new BufferedReader(new FileReader(slaveinfo));
    BufferedReader versreader = new BufferedReader(new FileReader(slavevers));
    buffer.append("<tr>\n" +
                    "<td>\n" +
                      "Version:" +
                    "</td>\n" +
                    "<td>\n")
          .append(    versreader.readLine())
          .append(  "<td>\n" +
                    "</td>\n" +
                  "</tr>\n" +
                  "<tr>\n" +
                    "<td>\n" +
                      "Status:" +
                    "</td>\n" +
                    "<td>\n")
          .append(    inforeader.readLine())
          .append(  "</td>\n" +
                  "</tr>\n");
  } else {
    // Master instance
    mode = "Master";
    File masterdir = new File(solrHome + "logs/clients");
    FilenameFilter sfilter = new FilenameFilter() {
        public boolean accept(File dir, String name) {
            return name.startsWith("snapshot.status");
        }
    };
    FilenameFilter cfilter = new FilenameFilter() {
        public boolean accept(File dir, String name) {
            return name.startsWith("snapshot.current");
        }
    };
    File[] clients = masterdir.listFiles(cfilter);
    if (clients == null) {
      buffer.append("<tr>\n" +
                      "<td>\n" +
                      "</td>\n" +
                      "<td>\n" +
                        "No distribution info present" +
                      "</td>\n" +
                    "</tr>\n");
    } else {
      buffer.append("<h4>Client Snapshot In Use:</h4>\n" +
                    "<tr>\n" +
                      "<th>\n" +
                      "Client" +
                      "</th>\n" +
                      "<th>\n" +
                      "Version" +
                      "</th>\n" +
                    "</tr>\n");
      int i = 0;
      while (i < clients.length) {
        String fileName=clients[i].toString();
        int p=fileName.indexOf("snapshot.current");
        String clientName=fileName.substring(p+17);
        BufferedReader reader = new BufferedReader(new FileReader(clients[i]));
        buffer.append("<tr>\n" +
                        "<td>\n" +
                        clientName +
                        "</td>\n" +
                        "<td>\n")
              .append(    reader.readLine())
              .append(  "</td>\n" +
                      "</tr>\n" +
                      "<tr>\n" +
                      "</tr>\n");
        i++;
      }
      clients = masterdir.listFiles(sfilter);
      if (clients!=null) {
        buffer.append("</table>\n" +
                      "<h4>Client Snapshot Distribution Status:</h4>\n" +
                      "<table>\n" +
                      "<tr>\n" +
                        "<th>\n" +
                        "Client" +
                        "</th>\n" +
                        "<th>\n" +
                        "Status" +
                        "</th>\n" +
                      "</tr>\n");
        i = 0;
        while (i < clients.length) {
          String fileName=clients[i].toString();
          int p=fileName.indexOf("snapshot.status");
          String clientName=fileName.substring(p+16);
          BufferedReader reader = new BufferedReader(new FileReader(clients[i]));
          buffer.append("<tr>\n" +
                          "<td>\n" +
                          clientName +
                          "</td>\n" +
                          "<td>\n")
                .append(    reader.readLine())
                .append(  "</td>\n" +
                        "</tr>\n" +
                        "<tr>\n" +
                        "</tr>\n");
          i++;
        }
      }
    }
  }

      out.write("\n");
      out.write("\n");
      out.write("\n");
      out.write("<br clear=\"all\">\n");
      out.write("<h2>Distribution Info</h2>\n");
      out.write("<h3>");
      out.print( mode );
      out.write(" Server</h3>\n");
      out.write("<table>\n");
      out.print( buffer );
      out.write("\n");
      out.write("</table>\n");
      out.write("<br><br>\n");
      out.write("    <a href=\".\">Return to Admin Page</a>\n");
      out.write("</body>\n");
      out.write("</html>\n");
    } catch (Throwable t) {
      if (!(t instanceof SkipPageException)){
        out = _jspx_out;
        if (out != null && out.getBufferSize() != 0)
          out.clearBuffer();
        if (_jspx_page_context != null) _jspx_page_context.handlePageException(t);
      }
    } finally {
      _jspxFactory.releasePageContext(_jspx_page_context);
    }
  }
}
