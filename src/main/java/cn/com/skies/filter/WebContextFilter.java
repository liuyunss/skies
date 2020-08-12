package cn.com.skies.filter;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @Description 过滤器
 * @Date 2020/7/28
 * @Created by sunmeng
 */

@WebFilter(urlPatterns = "/*")
public class WebContextFilter implements Filter {
    @Override
	public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;
        httpServletResponse.setHeader("Access-Control-Allow-Headers","X-Requested-With, accept, content-type");
        httpServletResponse.setHeader("Access-Control-Allow-Origin", "*");
        httpServletResponse.setHeader("Access-Control-Allow-Headers", "Authentication");
        httpServletResponse.setHeader("Access-Control-Allow-Credentials", "true");
//        httpServletResponse.setHeader("Access-Control-Allow-Origin", AccessControlAllowOrigin);
        filterChain.doFilter(servletRequest, httpServletResponse);

    }

    @Override
	public void destroy() {

    }
}
