package cn.tf.servlets;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspFactory;
import javax.servlet.jsp.PageContext;

import org.apache.commons.lang.RandomStringUtils;

import cn.tf.biz.GoodsBiz;
import cn.tf.biz.GoodstypeBiz;
import cn.tf.biz.IAdminInfoBiz;
import cn.tf.biz.IRolesBiz;
import cn.tf.biz.ShopBiz;
import cn.tf.biz.impl.AdminInfoBizImpl;
import cn.tf.biz.impl.GoodsBizImpl;
import cn.tf.biz.impl.GoodstypeBizImpl;
import cn.tf.biz.impl.RolesBizImpl;
import cn.tf.biz.impl.ShopBizImpl;
import cn.tf.entities.AdminInfo;
import cn.tf.entities.Goods;
import cn.tf.entities.GoodsType;
import cn.tf.entities.Roles;
import cn.tf.entities.Shopping;
import cn.tf.utils.AttributeData;
import cn.tf.utils.SendMailThread;
import cn.tf.utils.UploadUtil;
import cn.tf.utils.WebUtil;


@WebServlet(name="goodsInfoServlet",urlPatterns="/servlet/goodsInfoServlet")
public class goodsInfoServlet extends BasicServlet {

   

	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String op=request.getParameter("op");
		
		if("addgoodsInfo".equals(op)){
			addgoodsInfo(request,response);
		}else if("findgoodsInfoByPage".equals(op)){
			findgoodsInfoByPage(request,response);
		}else if("updategoodsInfo".equals(op)){
			updategoodsInfo(request,response);
		}else if("deletegoods".equals(op)){
			deletegoods(request,response);
		}else if("searchGoodsByPage".equals(op)){
			searchGoodsByPage(request,response);
		}else if("checkSpid".equals(op)){
			checkSpid(request,response);
		}
		
		

	}



	//检查该店铺是否是当前用户的，如果不是则不能修改
	private void checkSpid(HttpServletRequest request,
			HttpServletResponse response) {
		
		String spid1=request.getParameter("spid");
		
		Object obj=request.getSession().getAttribute(AttributeData.CURRENTADMINLOGIN);
		AdminInfo  adminInfo=(AdminInfo) obj;
		String  aid=adminInfo.getAid().toString().trim();
		Object obj1=request.getSession().getAttribute(AttributeData.SHOPPINGINFO);
		
		
		String spid=null;
		if(obj1==null){
			
			ShopBiz shopBiz=new ShopBizImpl();	
			Shopping list=shopBiz.findAll(aid);
			
			spid=list.getSpid().toString().trim();
		}else{
			Shopping  shoppingInfo=(Shopping) obj1;
			 spid=shoppingInfo.getSpid().toString().trim();
		}
		

		
		if(spid1.equals(spid)){
			this.out(response, 1);
		}else{
			this.out(response, 0);
		}
		
		
		
	}




	//条件查询
	private void searchGoodsByPage(HttpServletRequest request,
			HttpServletResponse response) {
		
		Object obj=request.getSession().getAttribute(AttributeData.CURRENTADMINLOGIN);
		AdminInfo  adminInfo=(AdminInfo) obj;
		String  rid=adminInfo.getRid().toString().trim();
		String  aid=adminInfo.getAid().toString().trim();
		Object obj1=request.getSession().getAttribute(AttributeData.SHOPPINGINFO);
		
		String spid=null;
		if(obj1==null){
			
			ShopBiz shopBiz=new ShopBizImpl();	
			Shopping list=shopBiz.findAll(aid);
			
			spid=list.getSpid().toString().trim();
		}else{
			Shopping  shoppingInfo=(Shopping) obj1;
			 spid=shoppingInfo.getSpid().toString().trim();
		}
		
		
		String price=request.getParameter("price");
		String gname=request.getParameter("gname");
		String status=request.getParameter("status");
		String pageNo=request.getParameter("page");
		String pageSize=request.getParameter("rows");
		Map<String,String>  param=new HashMap<String,String>();
		
		if(!"-1".equals(price)){
			
			if("0".equals(price)){
				param.put("   price<=50 and    2=", "2");
			}else if("1".equals(price)){
				param.put("   price>50  and price<=100  and    2=", "2");
			}else if("2".equals(price)){
				param.put("   price>100  and price<=200  and   2=", "2");
			}else if("3".equals(price)){
				param.put("   price>200 and    2=", "2");
			}
		}
		
		
		if(!"-1".equals(status)){
			param.put(" g.status=", status);
		}
		
		if(gname!=null && !"".equals(gname)){
			param.put(" g.gname like ", "%"+gname+"%");
		}
		
		if(rid.equals("1002") || rid.equals("1003")){
		}else{
			
			if(spid!=null){
				param.put("  g.spid=" , spid);
			}		
			
		}
		GoodsBiz goodsBiz=new GoodsBizImpl();	
		List<Goods>  list=goodsBiz.find(param,Integer.parseInt(pageNo), Integer.parseInt(pageSize));
		List<Goods>  list1=goodsBiz.find(param,null,null);
		this.out(response, list,list1.size());
		
		
		
		
	}



	//分页查询店铺信息
	private void findgoodsInfoByPage(HttpServletRequest request,
			HttpServletResponse response) {
		
		
		Object obj2=request.getSession().getAttribute(AttributeData.CURRENTADMINLOGIN);
		AdminInfo  adminInfo=(AdminInfo) obj2;
		String  rid=adminInfo.getRid().toString().trim();
		String  aid=adminInfo.getAid().toString().trim();
		Object obj1=request.getSession().getAttribute(AttributeData.SHOPPINGINFO);
		
		String spid=null;
		if(obj1==null){
			
			ShopBiz shopBiz=new ShopBizImpl();	
			Shopping list=shopBiz.findAll(aid);
			
			spid=list.getSpid().toString().trim();
		}else{
			Shopping  shoppingInfo=(Shopping) obj1;
			 spid=shoppingInfo.getSpid().toString().trim();
		}
		
		String pageNo=request.getParameter("page");
		String pageSize=request.getParameter("rows");	
		
		GoodsBiz goodsBiz=new GoodsBizImpl();		
		
		List<Goods>  list=goodsBiz.find(Integer.parseInt(spid),Integer.parseInt(rid),Integer.parseInt(pageNo),Integer.parseInt(pageSize));
		
		this.out(response, list,goodsBiz.getTotal(null));

	}


	//修改店铺信息
	private void updategoodsInfo(HttpServletRequest request,
			HttpServletResponse response) {	

		UploadUtil upload=new UploadUtil();
		PageContext pagecontext=JspFactory.getDefaultFactory().getPageContext(this,request,response,null,true,2048,true);
		Map<String,String> map=upload.upload(pagecontext);
		
		GoodsBiz goodsBiz=new GoodsBizImpl();
		int result=goodsBiz.update(map.get("gname"),map.get("des"),map.get("price"),map.get("status"),map.get("photo"),map.get("gid"));
		this.out(response, result);

	}

	//删除
	private void deletegoods(HttpServletRequest request,
			HttpServletResponse response) {
		
		
		
		String gid=request.getParameter("gid");
		GoodsBiz goodsBiz=new GoodsBizImpl();
		
		int result=goodsBiz.del(gid);
		this.out(response,result);
		
	}



	
	//添加商品信息
	private void addgoodsInfo(HttpServletRequest request,
			HttpServletResponse response) {
		Object obj1=request.getSession().getAttribute(AttributeData.SHOPPINGINFO);
		
		String spid=null;
		if(obj1==null){
			Object obj=request.getSession().getAttribute(AttributeData.CURRENTADMINLOGIN);
			AdminInfo  adminInfo=(AdminInfo) obj;
			String  aid=adminInfo.getAid().toString().trim();
			
			ShopBiz shopBiz=new ShopBizImpl();	
			Shopping list=shopBiz.findAll(aid);
			
			spid=list.getSpid().toString().trim();
		}else{
			Shopping  shoppingInfo=(Shopping) obj1;
			spid=shoppingInfo.getSpid().toString().trim();
		}
		
		
		UploadUtil upload=new UploadUtil();
		PageContext pagecontext=JspFactory.getDefaultFactory().getPageContext(this,request,response,null,true,2048,true);
		Map<String,String> map=upload.upload(pagecontext);
		
		GoodsBiz goodsBiz=new GoodsBizImpl();
		int result=goodsBiz.add(map.get("gname"),map.get("des"),map.get("price"),map.get("status"),map.get("photo"),spid);
		this.out(response, result);
	}
	
	

}