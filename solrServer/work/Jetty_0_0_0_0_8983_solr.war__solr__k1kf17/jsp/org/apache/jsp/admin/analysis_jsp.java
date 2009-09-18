package org.apache.jsp.admin;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.index.Payload;
import org.apache.solr.analysis.TokenFilterFactory;
import org.apache.solr.analysis.TokenizerChain;
import org.apache.solr.analysis.TokenizerFactory;
import org.apache.solr.schema.FieldType;
import org.apache.solr.schema.SchemaField;
import org.apache.solr.common.util.XML;
import javax.servlet.jsp.JspWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.*;
import java.math.BigInteger;
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

public final class analysis_jsp extends org.apache.jasper.runtime.HttpJspBase
    implements org.apache.jasper.runtime.JspSourceDependent {


  private static void doAnalyzer(JspWriter out, SchemaField field, String val, boolean queryAnalyser, boolean verbose, Set<Tok> match) throws Exception {
    Reader reader = new StringReader(val);

    FieldType ft = field.getType();
     Analyzer analyzer = queryAnalyser ?
             ft.getQueryAnalyzer() : ft.getAnalyzer();
     if (analyzer instanceof TokenizerChain) {
       TokenizerChain tchain = (TokenizerChain)analyzer;
       TokenizerFactory tfac = tchain.getTokenizerFactory();
       TokenFilterFactory[] filtfacs = tchain.getTokenFilterFactories();

       TokenStream tstream = tfac.create(reader);
       List<Token> tokens = getTokens(tstream);
       tstream = tfac.create(reader);
       if (verbose) {
         writeHeader(out, tfac.getClass(), tfac.getArgs());
       }

       writeTokens(out, tokens, ft, verbose, match);

       for (TokenFilterFactory filtfac : filtfacs) {
         if (verbose) {
           writeHeader(out, filtfac.getClass(), filtfac.getArgs());
         }

         final Iterator<Token> iter = tokens.iterator();
         tstream = filtfac.create( new TokenStream() {
           public Token next() throws IOException {
             return iter.hasNext() ? iter.next() : null;
           }
          }
         );
         tokens = getTokens(tstream);

         writeTokens(out, tokens, ft, verbose, match);
       }

     } else {
       TokenStream tstream = analyzer.tokenStream(field.getName(),reader);
       List<Token> tokens = getTokens(tstream);
       if (verbose) {
         writeHeader(out, analyzer.getClass(), new HashMap<String,String>());
       }
       writeTokens(out, tokens, ft, verbose, match);
     }
  }


  static List<Token> getTokens(TokenStream tstream) throws IOException {
    List<Token> tokens = new ArrayList<Token>();
    while (true) {
      Token t = tstream.next();
      if (t==null) break;
      tokens.add(t);
    }
    return tokens;
  }


  private static class Tok {
    Token token;
    int pos;
    Tok(Token token, int pos) {
      this.token=token;
      this.pos=pos;
    }

    public boolean equals(Object o) {
      return ((Tok)o).token.termText().equals(token.termText());
    }
    public int hashCode() {
      return token.termText().hashCode();
    }
    public String toString() {
      return token.termText();
    }
  }

  private static interface ToStr {
    public String toStr(Object o);
  }

  private static void printRow(JspWriter out, String header, List[] arrLst, ToStr converter, boolean multival, boolean verbose, Set<Tok> match) throws IOException {
    // find the maximum number of terms for any position
    int maxSz=1;
    if (multival) {
      for (List lst : arrLst) {
        maxSz = Math.max(lst.size(), maxSz);
      }
    }


    for (int idx=0; idx<maxSz; idx++) {
      out.println("<tr>");
      if (idx==0 && verbose) {
        if (header != null) {
          out.print("<th NOWRAP rowspan=\""+maxSz+"\">");
          XML.escapeCharData(header,out);
          out.println("</th>");
        }
      }

      for (int posIndex=0; posIndex<arrLst.length; posIndex++) {
        List<Tok> lst = arrLst[posIndex];
        if (lst.size() <= idx) continue;
        if (match!=null && match.contains(lst.get(idx))) {
          out.print("<td class=\"highlight\"");
        } else {
          out.print("<td class=\"debugdata\"");
        }

        // if the last value in the column, use up
        // the rest of the space via rowspan.
        if (lst.size() == idx+1 && lst.size() < maxSz) {
          out.print("rowspan=\""+(maxSz-lst.size()+1)+'"');
        }

        out.print('>');

        XML.escapeCharData(converter.toStr(lst.get(idx)), out);
        out.print("</td>");
      }

      out.println("</tr>");
    }

  }

  static String isPayloadString( Payload p ) {
  	String sp = new String( p.getData() );
	for( int i=0; i < sp.length(); i++ ) {
	if( !Character.isDefined( sp.charAt(i) ) || Character.isISOControl( sp.charAt(i) ) )
	  return "";
	}
	return "(" + sp + ")";
  }

  static void writeHeader(JspWriter out, Class clazz, Map<String,String> args) throws IOException {
    out.print("<h4>");
    out.print(clazz.getName());
    XML.escapeCharData("   "+args,out);
    out.println("</h4>");
  }



  // readable, raw, pos, type, start/end
  static void writeTokens(JspWriter out, List<Token> tokens, final FieldType ft, boolean verbose, Set<Tok> match) throws IOException {

    // Use a map to tell what tokens are in what positions
    // because some tokenizers/filters may do funky stuff with
    // very large increments, or negative increments.
    HashMap<Integer,List<Tok>> map = new HashMap<Integer,List<Tok>>();
    boolean needRaw=false;
    int pos=0;
    for (Token t : tokens) {
      if (!t.termText().equals(ft.indexedToReadable(t.termText()))) {
        needRaw=true;
      }

      pos += t.getPositionIncrement();
      List lst = map.get(pos);
      if (lst==null) {
        lst = new ArrayList(1);
        map.put(pos,lst);
      }
      Tok tok = new Tok(t,pos);
      lst.add(tok);
    }

    List<Tok>[] arr = (List<Tok>[])map.values().toArray(new ArrayList[map.size()]);

    /* Jetty 6.1.3 miscompiles this generics version...
    Arrays.sort(arr, new Comparator<List<Tok>>() {
      public int compare(List<Tok> toks, List<Tok> toks1) {
        return toks.get(0).pos - toks1.get(0).pos;
      }
    }
    */

    Arrays.sort(arr, new Comparator() {
      public int compare(Object toks, Object toks1) {
        return ((List<Tok>)toks).get(0).pos - ((List<Tok>)toks1).get(0).pos;
      }
    }


    );

    out.println("<table width=\"auto\" class=\"analysis\" border=\"1\">");

    if (verbose) {
      printRow(out,"term position", arr, new ToStr() {
        public String toStr(Object o) {
          return Integer.toString(((Tok)o).pos);
        }
      }
              ,false
              ,verbose
              ,null);
    }


    printRow(out,"term text", arr, new ToStr() {
      public String toStr(Object o) {
        return ft.indexedToReadable( ((Tok)o).token.termText() );
      }
    }
            ,true
            ,verbose
            ,match
   );

    if (needRaw) {
      printRow(out,"raw text", arr, new ToStr() {
        public String toStr(Object o) {
          // page is UTF-8, so anything goes.
          return ((Tok)o).token.termText();
        }
      }
              ,true
              ,verbose
              ,match
      );
    }

    if (verbose) {
      printRow(out,"term type", arr, new ToStr() {
        public String toStr(Object o) {
          String tt =  ((Tok)o).token.type();
          if (tt == null) {
             return "null";
          } else {
             return tt;
          }
        }
      }
              ,true
              ,verbose,
              null
      );
    }

    if (verbose) {
      printRow(out,"source start,end", arr, new ToStr() {
        public String toStr(Object o) {
          Token t = ((Tok)o).token;
          return Integer.toString(t.startOffset()) + ',' + t.endOffset() ;
        }
      }
              ,true
              ,verbose
              ,null
      );
    }

    if (verbose) {
      printRow(out,"payload", arr, new ToStr() {
        public String toStr(Object o) {
          Token t = ((Tok)o).token;
          Payload p = t.getPayload();
          if( null != p ) {
            BigInteger bi = new BigInteger( p.getData() );
            String ret = bi.toString( 16 );
            if (ret.length() % 2 != 0) {
              // Pad with 0
              ret = "0"+ret;
            }
            ret += isPayloadString( p );
            return ret;
          }
          return "";			
        }
      }
              ,true
              ,verbose
              ,null
      );
    }
    
    out.println("</table>");
  }


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
      out.write("\n");
      out.write("\n");
      out.write("\n");
      out.write('\n');
      out.write('\n');
      out.write('\n');
      out.write('\n');
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

  // is name a field name or a type name?
  String nt = request.getParameter("nt");
  if (nt==null || nt.length()==0) nt="name"; // assume field name
  nt = nt.toLowerCase().trim();
  String name = request.getParameter("name");
  if (name==null || name.length()==0) name="";
  String val = request.getParameter("val");
  if (val==null || val.length()==0) val="";
  String qval = request.getParameter("qval");
  if (qval==null || qval.length()==0) qval="";
  String verboseS = request.getParameter("verbose");
  boolean verbose = verboseS!=null && verboseS.equalsIgnoreCase("on");
  String qverboseS = request.getParameter("qverbose");
  boolean qverbose = qverboseS!=null && qverboseS.equalsIgnoreCase("on");
  String highlightS = request.getParameter("highlight");
  boolean highlight = highlightS!=null && highlightS.equalsIgnoreCase("on");

      out.write("\n");
      out.write("\n");
      out.write("<br clear=\"all\">\n");
      out.write("\n");
      out.write("<h2>Field Analysis</h2>\n");
      out.write("\n");
      out.write("<form method=\"POST\" action=\"analysis.jsp\" accept-charset=\"UTF-8\">\n");
      out.write("<table>\n");
      out.write("<tr>\n");
      out.write("  <td>\n");
      out.write("\t<strong>Field\n");
      out.write("          <select name=\"nt\">\n");
      out.write("\t  <option ");
      out.print( nt.equals("name") ? "selected=\"selected\"" : "" );
      out.write(" >name</option>\n");
      out.write("\t  <option ");
      out.print( nt.equals("type") ? "selected=\"selected\"" : "" );
      out.write(">type</option>\n");
      out.write("          </select></strong>\n");
      out.write("  </td>\n");
      out.write("  <td>\n");
      out.write("\t<input class=\"std\" name=\"name\" type=\"text\" value=\"");
 XML.escapeCharData(name, out); 
      out.write("\">\n");
      out.write("  </td>\n");
      out.write("</tr>\n");
      out.write("<tr>\n");
      out.write("  <td>\n");
      out.write("\t<strong>Field value (Index)</strong>\n");
      out.write("  <br/>\n");
      out.write("  verbose output\n");
      out.write("  <input name=\"verbose\" type=\"checkbox\"\n");
      out.write("     ");
      out.print( verbose ? "checked=\"true\"" : "" );
      out.write(" >\n");
      out.write("    <br/>\n");
      out.write("  highlight matches\n");
      out.write("  <input name=\"highlight\" type=\"checkbox\"\n");
      out.write("     ");
      out.print( highlight ? "checked=\"true\"" : "" );
      out.write(" >\n");
      out.write("  </td>\n");
      out.write("  <td>\n");
      out.write("\t<textarea class=\"std\" rows=\"8\" cols=\"70\" name=\"val\">");
 XML.escapeCharData(val,out); 
      out.write("</textarea>\n");
      out.write("  </td>\n");
      out.write("</tr>\n");
      out.write("<tr>\n");
      out.write("  <td>\n");
      out.write("\t<strong>Field value (Query)</strong>\n");
      out.write("  <br/>\n");
      out.write("  verbose output\n");
      out.write("  <input name=\"qverbose\" type=\"checkbox\"\n");
      out.write("     ");
      out.print( qverbose ? "checked=\"true\"" : "" );
      out.write(" >\n");
      out.write("  </td>\n");
      out.write("  <td>\n");
      out.write("\t<textarea class=\"std\" rows=\"1\" cols=\"70\" name=\"qval\">");
 XML.escapeCharData(qval,out); 
      out.write("</textarea>\n");
      out.write("  </td>\n");
      out.write("</tr>\n");
      out.write("<tr>\n");
      out.write("\n");
      out.write("  <td>\n");
      out.write("  </td>\n");
      out.write("\n");
      out.write("  <td>\n");
      out.write("\t<input class=\"stdbutton\" type=\"submit\" value=\"analyze\">\n");
      out.write("  </td>\n");
      out.write("\n");
      out.write("</tr>\n");
      out.write("</table>\n");
      out.write("</form>\n");
      out.write("\n");
      out.write("\n");

  SchemaField field=null;

  if (name!="") {
    if (nt.equals("name")) {
      try {
        field = schema.getField(name);
      } catch (Exception e) {
        out.print("<strong>Unknown Field: ");
        XML.escapeCharData(name, out);
        out.println("</strong>");
      }
    } else {
       FieldType t = schema.getFieldTypes().get(name);
       if (null == t) {
        out.print("<strong>Unknown Field Type: ");
        XML.escapeCharData(name, out);
        out.println("</strong>");
       } else {
         field = new SchemaField("fakefieldoftype:"+name, t);
       }
    }
  }

  if (field!=null) {
    HashSet<Tok> matches = null;
    if (qval!="" && highlight) {
      Reader reader = new StringReader(qval);
      Analyzer analyzer =  field.getType().getQueryAnalyzer();
      TokenStream tstream = analyzer.tokenStream(field.getName(),reader);
      List<Token> tokens = getTokens(tstream);
      matches = new HashSet<Tok>();
      for (Token t : tokens) { matches.add( new Tok(t,0)); }
    }

    if (val!="") {
      out.println("<h3>Index Analyzer</h3>");
      doAnalyzer(out, field, val, false, verbose,matches);
    }
    if (qval!="") {
      out.println("<h3>Query Analyzer</h3>");
      doAnalyzer(out, field, qval, true, qverbose,null);
    }
  }


      out.write("\n");
      out.write("\n");
      out.write("\n");
      out.write("</body>\n");
      out.write("</html>\n");
      out.write("\n");
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
