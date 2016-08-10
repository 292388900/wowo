package cn.tf.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import cn.tf.biz.IAdminInfoBiz;
import cn.tf.biz.impl.AdminInfoBizImpl;
import cn.tf.entities.AdminInfo;
import cn.tf.utils.AttributeData;


@WebServlet(name="adminInfoServlet",urlPatterns="/servlet/adminInfoServlet")
public class adminInfoServlet extends BasicServlet {

   

	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String op=request.getParameter("op");
		
		if("adminLogin".equals(op)){
			adminLogin(request,response);
		}
		
		

	}

	private void adminLogin(HttpServletRequest request,
			HttpServletResponse response) {
		String role=request.getParameter("role");
		String name=request.getParameter("name");
		String pwd=request.getParameter("pwd");
		String code=request.getParameter("code");
		
		HttpSession session=request.getSession();
		String codes=(String) session.getAttribute("rand");
		int result=0;
		
		if(codes.equals(code)){
			//验证码输入正确，则调用业务层
			IAdminInfoBiz adminInfoBiz=new AdminInfoBizImpl();
			AdminInfo adminInfo=adminInfoBiz.login(name, pwd, role);
			if(adminInfo==null){
				result=2;   //用户名或密码错误
			}else{
				result=3; //登录成功
				
				//将当前登录用户存起来，以便后面的页面使用当前用户信息
				session.setAttribute(AttributeData.CURRENTADMINLOGIN, adminInfo);
			}	
			
		}else{
			result=1;  //验证码错误
		}
		this.out(response, result);
		
	}

}