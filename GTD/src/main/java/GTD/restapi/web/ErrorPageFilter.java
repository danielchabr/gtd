package GTD.restapi.web;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.context.embedded.AbstractConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.ErrorPage;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**

 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ErrorPageFilter extends AbstractConfigurableEmbeddedServletContainer implements
    Filter, NonEmbeddedServletContainerFactory {

  private static Log logger = LogFactory.getLog(ErrorPageFilter.class);

  // From RequestDispatcher but not referenced to remain compatible with Servlet 2.5

  private static final String ERROR_EXCEPTION = "javax.servlet.error.exception";

  private static final String ERROR_EXCEPTION_TYPE = "javax.servlet.error.exception_type";

  private static final String ERROR_MESSAGE = "javax.servlet.error.message";

  private static final String ERROR_STATUS_CODE = "javax.servlet.error.status_code";

  private String global;

  private final Map<Integer, String> statuses = new HashMap<Integer, String>();

  private final Map<Class<?>, String> exceptions = new HashMap<Class<?>, String>();

  private final Map<Class<?>, Class<?>> subtypes = new HashMap<Class<?>, Class<?>>();

  private final OncePerRequestFilter delegate = new OncePerRequestFilter() {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response, FilterChain chain) throws ServletException,
        IOException {
      ErrorPageFilter.this.doFilter(request, response, chain);
    }

  };

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {
    this.delegate.init(filterConfig);
  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response,
                       FilterChain chain) throws IOException, ServletException {
    this.delegate.doFilter(request, response, chain);
  }

  private void doFilter(HttpServletRequest request, HttpServletResponse response,
                        FilterChain chain) throws IOException, ServletException {

    ErrorWrapperResponse wrapped = new ErrorWrapperResponse(response);
    try {
      chain.doFilter(request, wrapped);
      int status = wrapped.getStatus();
      if (status >= 400) {
        handleErrorStatus(request, response, status, wrapped.getMessage());
        response.flushBuffer();
      }
      else if (!request.isAsyncStarted()) {
        response.flushBuffer();
      }
    }
    catch (Throwable ex) {
      handleException(request, response, wrapped, ex);
      response.flushBuffer();
    }
  }

  private void handleErrorStatus(HttpServletRequest request,
                                 HttpServletResponse response, int status, String message)
      throws ServletException, IOException {

    if (response.isCommitted()) {
      handleCommittedResponse(request, null);
      return;
    }

    String errorPath = getErrorPath(this.statuses, status);
    if (errorPath == null) {
      response.sendError(status, message);
      return;
    }
    response.setStatus(status);
    setErrorAttributes(request, status, message);
    request.getRequestDispatcher(errorPath).forward(request, response);

  }

  private void handleException(HttpServletRequest request,
                               HttpServletResponse response, ErrorWrapperResponse wrapped, Throwable ex)
      throws IOException, ServletException {
    Class<?> type = ex.getClass();
    String errorPath = getErrorPath(type);
    if (errorPath == null) {
      rethrow(ex);
      return;
    }
    if (response.isCommitted()) {
      handleCommittedResponse(request, ex);
      return;
    }

    forwardToErrorPage(errorPath, request, wrapped, ex);
  }

  private void forwardToErrorPage(String path, HttpServletRequest request,
                                  HttpServletResponse response, Throwable ex) throws ServletException,
      IOException {

    if (logger.isErrorEnabled()) {
      String message = "Forwarding to error page from request ["
          + request.getServletPath() + request.getPathInfo()
          + "] due to exception [" + ex.getMessage() + "]";
      logger.error(message, ex);
    }

    setErrorAttributes(request, 500, ex.getMessage());
    request.setAttribute(ERROR_EXCEPTION, ex);
    request.setAttribute(ERROR_EXCEPTION_TYPE, ex.getClass().getName());

    response.reset();
    response.sendError(500, ex.getMessage());
    request.getRequestDispatcher(path).forward(request, response);
  }

  private void handleCommittedResponse(HttpServletRequest request, Throwable ex) {
    String message = "Cannot forward to error page for request to "
        + request.getRequestURI() + " as the response has already been"
        + " committed. As a result, the response may have the wrong status"
        + " code. If your application is running on WebSphere Application"
        + " Server you may be able to resolve this problem by setting "
        + " com.ibm.ws.webcontainer.invokeFlushAfterService to false";
    if (ex == null) {
      logger.error(message);
    }
    else {
      // User might see the error page without all the data here but throwing the
      // exception isn't going to help anyone (we'll log it to be on the safe side)
      logger.error(message, ex);
    }
  }

  private String getErrorPath(Map<Integer, String> map, Integer status) {
    if (map.containsKey(status)) {
      return map.get(status);
    }
    return this.global;
  }

  private String getErrorPath(Class<?> type) {
    if (this.exceptions.containsKey(type)) {
      return this.exceptions.get(type);
    }
    if (this.subtypes.containsKey(type)) {
      return this.exceptions.get(this.subtypes.get(type));
    }
    Class<?> subtype = type;
    while (subtype != Object.class) {
      subtype = subtype.getSuperclass();
      if (this.exceptions.containsKey(subtype)) {
        this.subtypes.put(subtype, type);
        return this.exceptions.get(subtype);
      }
    }
    return this.global;
  }

  private void setErrorAttributes(ServletRequest request, int status, String message) {
    request.setAttribute(ERROR_STATUS_CODE, status);
    request.setAttribute(ERROR_MESSAGE, message);
  }

  private void rethrow(Throwable ex) throws IOException, ServletException {
    if (ex instanceof RuntimeException) {
      throw (RuntimeException) ex;
    }
    if (ex instanceof Error) {
      throw (Error) ex;
    }
    if (ex instanceof IOException) {
      throw (IOException) ex;
    }
    if (ex instanceof ServletException) {
      throw (ServletException) ex;
    }
    throw new IllegalStateException(ex);
  }

  @Override
  public void addErrorPages(ErrorPage... errorPages) {
    for (ErrorPage errorPage : errorPages) {
      if (errorPage.isGlobal()) {
        this.global = errorPage.getPath();
      }
      else if (errorPage.getStatus() != null) {
        this.statuses.put(errorPage.getStatus().value(), errorPage.getPath());
      }
      else {
        this.exceptions.put(errorPage.getException(), errorPage.getPath());
      }
    }
  }

  @Override
  public void destroy() {
  }

  private static class ErrorWrapperResponse extends HttpServletResponseWrapper {

    private int status;

    private String message;

    private boolean errorToSend;

    public ErrorWrapperResponse(HttpServletResponse response) {
      super(response);
      this.status = response.getStatus();
    }

    @Override
    public void sendError(int status) throws IOException {
      sendError(status, null);
    }

    @Override
    public void sendError(int status, String message) throws IOException {
      this.status = status;
      this.message = message;

      this.errorToSend = true;
    }

    @Override
    public int getStatus() {
      return this.status;
    }

    @Override
    public void flushBuffer() throws IOException {
      if (this.errorToSend && !isCommitted()) {
        ((HttpServletResponse) getResponse())
            .sendError(this.status, this.message);
      }
      super.flushBuffer();
    }

    public String getMessage() {
      return this.message;
    }

  }

}
