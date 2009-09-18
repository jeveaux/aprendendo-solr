package org.apache.jsp.admin;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;
import org.apache.solr.core.SolrCore;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.lang.management.ThreadInfo;
import java.io.IOException;
import org.apache.solr.common.util.XML;
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

public final class threaddump_jsp extends org.apache.jasper.runtime.HttpJspBase
    implements org.apache.jasper.runtime.JspSourceDependent {


  static ThreadMXBean tmbean = ManagementFactory.getThreadMXBean();


  static void printThreadInfo(ThreadInfo ti, JspWriter out) throws IOException {
      long tid = ti.getThreadId();
      out.println("    <thread>");
      out.println("      <id>" + tid + "</id>");
      out.print("      <name>");
      XML.escapeCharData(ti.getThreadName(), out);
      out.println("</name>");
      out.println("      <state>" + ti.getThreadState() + "</state>");
      if (ti.getLockName() != null) {
          out.println("      <lock>" + ti.getLockName() + "</lock>");
      }
      if (ti.isSuspended()) {
          out.println("      <suspended/>");
      }
      if (ti.isInNative()) {
          out.println("      <inNative/>");
      }
      if (tmbean.isThreadCpuTimeSupported()) {
          out.println("      <cpuTime>" + formatNanos(tmbean.getThreadCpuTime(tid)) + "</cpuTime>");
          out.println("      <userTime>" + formatNanos(tmbean.getThreadUserTime(tid)) + "</userTime>");
      }

      if (ti.getLockOwnerName() != null) {
          out.println("      <owner>");
          out.println("        <name>" + ti.getLockOwnerName() + "</name>");
          out.println("        <id>" + ti.getLockOwnerId() + "</id>");
          out.println("      </owner>");
      }
      out.println("      <stackTrace>");
      for (StackTraceElement ste : ti.getStackTrace()) {
          out.print("        <line>");
          XML.escapeCharData("at " + ste.toString(), out);
          out.println("        </line>");
      }
      out.println("      </stackTrace>");
      out.println("    </thread>");
  }

  static String formatNanos(long ns) {
      return String.format("%.4fms", ns / (double) 1000000);
  }

  private static final JspFactory _jspxFactory = JspFactory.getDefaultFactory();

  private static java.util.Vector _jspx_dependants;

  static {
    _jspx_dependants = new java.util.Vector(1);
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
      response.setContentType("text/xml; charset=utf-8");
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
      out.write('\n');
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
      out.write("\n");
      out.write("\n");
      out.write("<?xml-stylesheet type=\"text/xsl\" href=\"threaddump.xsl\"?>\n");
      out.write("\n");
      out.write("<solr>\n");
      out.write("  <core>");
      out.print( collectionName );
      out.write("</core>\n");
      out.write("  <system>\n");
      out.write("  <jvm>\n");
      out.write("    <version>");
      out.print(System.getProperty("java.vm.version"));
      out.write("</version>\n");
      out.write("    <name>");
      out.print(System.getProperty("java.vm.name"));
      out.write("</name>\n");
      out.write("  </jvm>\n");
      out.write("  <threadCount>\n");
      out.write("    <current>");
      out.print(tmbean.getThreadCount());
      out.write("</current>\n");
      out.write("    <peak>");
      out.print(tmbean.getPeakThreadCount());
      out.write("</peak>\n");
      out.write("    <daemon>");
      out.print(tmbean.getDaemonThreadCount());
      out.write("</daemon>\n");
      out.write("  </threadCount>\n");

  long[] tids;
  ThreadInfo[] tinfos;
  tids = tmbean.findMonitorDeadlockedThreads();
  if (tids != null) {
      out.println("  <deadlocks>");
      tinfos = tmbean.getThreadInfo(tids, Integer.MAX_VALUE);
      for (ThreadInfo ti : tinfos) {
          printThreadInfo(ti, out);
      }
      out.println("  </deadlocks>");
  }

      out.write('\n');

  tids = tmbean.getAllThreadIds();
  tinfos = tmbean.getThreadInfo(tids, Integer.MAX_VALUE);
  out.println("  <threadDump>");
  for (ThreadInfo ti : tinfos) {
     printThreadInfo(ti, out);
  }
  out.println("  </threadDump>");

      out.write("\n");
      out.write("  </system>\n");
      out.write("</solr>\n");
      out.write("\n");
      out.write('\n');
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
