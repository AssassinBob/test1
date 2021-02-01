package cn.tedu.sp11.filter;

import javax.servlet.http.HttpServletRequest;

import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.stereotype.Component;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;

import cn.tedu.web.util.JsonResult;

@Component
public class AccessFilter extends ZuulFilter {

	// 对指定的serviceID过滤，如果要过滤所有服务，直接返回true
	@Override
	public boolean shouldFilter() {
		RequestContext context = RequestContext.getCurrentContext();
		String serviceId = (String) context.get(FilterConstants.SERVICE_ID_KEY);
		if ("item-service".equals(serviceId)) {
			return true;
		}
		return false;
	}

	//拦截器生效后运行的方法
	@Override
	public Object run() throws ZuulException {
		RequestContext context = RequestContext.getCurrentContext();
		HttpServletRequest request = context.getRequest();
		String token = request.getParameter("token");
		if(token==null) {
			context.setSendZuulResponse(false);//设置为false会阻止请求被路由到后台服务
			context.setResponseStatusCode(200);//设置响应码
			context.setResponseBody(JsonResult.err().code(JsonResult.NOT_LOGIN).toString());//设置响应体，返回错误信息
		}
		return null;//返回值目前没有实际意义
	}

	@Override
	public String filterType() {
		
		return FilterConstants.PRE_TYPE;//设置为前置拦截器
	}

	@Override
	public int filterOrder() {
		
		return 6;//过滤器设置大于5才能得到serviceID
	}

}
