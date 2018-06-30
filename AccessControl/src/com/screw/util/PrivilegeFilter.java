package com.screw.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class PrivilegeFilter implements Filter{
	private Properties properties = new Properties();

	@Override
	public void destroy() {
		properties=null;
	}
	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		//��ȡprivilegeFile�����ļ������·�� ===�� fileName: /WEB-INF/privilege.properties
		String fileName=filterConfig.getInitParameter("privilegeFile");
		//��ȡ�����ļ��ľ���·��  realPath: E:\code\Work2018\.metadata\.plugins\org.eclipse.wst.server.core\tmp0\wtpwebapps\AccessControl\WEB-INF\privilege.properties
		String realPath = filterConfig.getServletContext().getRealPath(fileName);
		try
		{
			properties.load(new FileInputStream(realPath));
		}
		catch(Exception e)
		{
			filterConfig.getServletContext().log("��ȡȨ�޿����ļ�ʧ��",e);
		}
		
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
			throws IOException, ServletException
	{
		HttpServletRequest request=(HttpServletRequest)req;
		HttpServletResponse response=(HttpServletResponse)res;
		//��ȡ��Ŀ�����·��  �磺admin/index
		String requestUri=request.getRequestURI().replace(request.getContextPath()+"/", "");
		String role=(String)request.getSession().getAttribute("role");
		role=role==null?"guest":role;
		boolean authen=false;
		//properties.keySet()  �����ļ��е�key
		for(Object obj:properties.keySet())
		{
			String key=(String)obj;
			//����վ·���������ļ��е�key��Ӧ����
			if(requestUri.indexOf(key)!=-1)
			{	
				//�鿴��Ӧ·��Ȩ���Ƿ���ȷ
				if(((String) properties.get(key)).indexOf( role)!=-1)
				{
					authen=true;
					break;
				}
			}
		}
		if(!authen)
		{
			throw new RuntimeException("����Ȩ���ʸ�ҳ�棬���Ժ��ʵ���ݵ�¼��鿴��");
		}
		chain.doFilter(request, response);
	}	
}
