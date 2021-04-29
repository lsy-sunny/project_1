package com.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletRequest;

import com.utils.ValidatorUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.annotation.IgnoreAuth;

import com.entity.PutongyonghuEntity;
import com.entity.view.PutongyonghuView;

import com.service.PutongyonghuService;
import com.service.TokenService;
import com.utils.PageUtils;
import com.utils.R;
import com.utils.MD5Util;
import com.utils.MPUtil;
import com.utils.CommonUtil;


/**
 * 普通用户
 * 后端接口
 * @author 
 * @email 
 * @date 2021-04-14 15:36:41
 */
@RestController
@RequestMapping("/putongyonghu")
public class PutongyonghuController {
    @Autowired
    private PutongyonghuService putongyonghuService;
    
	@Autowired
	private TokenService tokenService;
	
	/**
	 * 登录
	 */
	@IgnoreAuth
	@RequestMapping(value = "/login")
	public R login(String username, String password, String captcha, HttpServletRequest request) {
		PutongyonghuEntity user = putongyonghuService.selectOne(new EntityWrapper<PutongyonghuEntity>().eq("yonghuzhanghao", username));
		if(user==null || !user.getMima().equals(password)) {
			return R.error("账号或密码不正确");
		}
		
		String token = tokenService.generateToken(user.getId(), username,"putongyonghu",  "普通用户" );
		return R.ok().put("token", token);
	}
	
	/**
     * 注册
     */
	@IgnoreAuth
    @RequestMapping("/register")
    public R register(@RequestBody PutongyonghuEntity putongyonghu){
    	//ValidatorUtils.validateEntity(putongyonghu);
    	PutongyonghuEntity user = putongyonghuService.selectOne(new EntityWrapper<PutongyonghuEntity>().eq("yonghuzhanghao", putongyonghu.getYonghuzhanghao()));
		if(user!=null) {
			return R.error("注册用户已存在");
		}
		Long uId = new Date().getTime();
		putongyonghu.setId(uId);
        putongyonghuService.insert(putongyonghu);
        return R.ok();
    }
	
	/**
	 * 退出
	 */
	@RequestMapping("/logout")
	public R logout(HttpServletRequest request) {
		request.getSession().invalidate();
		return R.ok("退出成功");
	}
	
	/**
     * 获取用户的session用户信息
     */
    @RequestMapping("/session")
    public R getCurrUser(HttpServletRequest request){
    	Long id = (Long)request.getSession().getAttribute("userId");
        PutongyonghuEntity user = putongyonghuService.selectById(id);
        return R.ok().put("data", user);
    }
    
    /**
     * 密码重置
     */
    @IgnoreAuth
	@RequestMapping(value = "/resetPass")
    public R resetPass(String username, HttpServletRequest request){
    	PutongyonghuEntity user = putongyonghuService.selectOne(new EntityWrapper<PutongyonghuEntity>().eq("yonghuzhanghao", username));
    	if(user==null) {
    		return R.error("账号不存在");
    	}
        user.setMima("123456");
        putongyonghuService.updateById(user);
        return R.ok("密码已重置为：123456");
    }


    /**
     * 后端列表
     */
    @RequestMapping("/page")
    public R page(@RequestParam Map<String, Object> params,PutongyonghuEntity putongyonghu,
		HttpServletRequest request){
        EntityWrapper<PutongyonghuEntity> ew = new EntityWrapper<PutongyonghuEntity>();
		PageUtils page = putongyonghuService.queryPage(params, MPUtil.sort(MPUtil.between(MPUtil.likeOrEq(ew, putongyonghu), params), params));

        return R.ok().put("data", page);
    }
    
    /**
     * 前端列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params,PutongyonghuEntity putongyonghu, HttpServletRequest request){
        EntityWrapper<PutongyonghuEntity> ew = new EntityWrapper<PutongyonghuEntity>();
		PageUtils page = putongyonghuService.queryPage(params, MPUtil.sort(MPUtil.between(MPUtil.likeOrEq(ew, putongyonghu), params), params));
        return R.ok().put("data", page);
    }

	/**
     * 列表
     */
    @RequestMapping("/lists")
    public R list( PutongyonghuEntity putongyonghu){
       	EntityWrapper<PutongyonghuEntity> ew = new EntityWrapper<PutongyonghuEntity>();
      	ew.allEq(MPUtil.allEQMapPre( putongyonghu, "putongyonghu")); 
        return R.ok().put("data", putongyonghuService.selectListView(ew));
    }

	 /**
     * 查询
     */
    @RequestMapping("/query")
    public R query(PutongyonghuEntity putongyonghu){
        EntityWrapper< PutongyonghuEntity> ew = new EntityWrapper< PutongyonghuEntity>();
 		ew.allEq(MPUtil.allEQMapPre( putongyonghu, "putongyonghu")); 
		PutongyonghuView putongyonghuView =  putongyonghuService.selectView(ew);
		return R.ok("查询普通用户成功").put("data", putongyonghuView);
    }
	
    /**
     * 后端详情
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
        PutongyonghuEntity putongyonghu = putongyonghuService.selectById(id);
        return R.ok().put("data", putongyonghu);
    }

    /**
     * 前端详情
     */
    @RequestMapping("/detail/{id}")
    public R detail(@PathVariable("id") Long id){
        PutongyonghuEntity putongyonghu = putongyonghuService.selectById(id);
        return R.ok().put("data", putongyonghu);
    }
    



    /**
     * 后端保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody PutongyonghuEntity putongyonghu, HttpServletRequest request){
    	putongyonghu.setId(new Date().getTime()+new Double(Math.floor(Math.random()*1000)).longValue());
    	//ValidatorUtils.validateEntity(putongyonghu);
    	PutongyonghuEntity user = putongyonghuService.selectOne(new EntityWrapper<PutongyonghuEntity>().eq("yonghuzhanghao", putongyonghu.getYonghuzhanghao()));
		if(user!=null) {
			return R.error("用户已存在");
		}
		putongyonghu.setId(new Date().getTime());
        putongyonghuService.insert(putongyonghu);
        return R.ok();
    }
    
    /**
     * 前端保存
     */
    @RequestMapping("/add")
    public R add(@RequestBody PutongyonghuEntity putongyonghu, HttpServletRequest request){
    	putongyonghu.setId(new Date().getTime()+new Double(Math.floor(Math.random()*1000)).longValue());
    	//ValidatorUtils.validateEntity(putongyonghu);
    	PutongyonghuEntity user = putongyonghuService.selectOne(new EntityWrapper<PutongyonghuEntity>().eq("yonghuzhanghao", putongyonghu.getYonghuzhanghao()));
		if(user!=null) {
			return R.error("用户已存在");
		}
		putongyonghu.setId(new Date().getTime());
        putongyonghuService.insert(putongyonghu);
        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody PutongyonghuEntity putongyonghu, HttpServletRequest request){
        //ValidatorUtils.validateEntity(putongyonghu);
        putongyonghuService.updateById(putongyonghu);//全部更新
        return R.ok();
    }
    

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){
        putongyonghuService.deleteBatchIds(Arrays.asList(ids));
        return R.ok();
    }
    
    /**
     * 提醒接口
     */
	@RequestMapping("/remind/{columnName}/{type}")
	public R remindCount(@PathVariable("columnName") String columnName, HttpServletRequest request, 
						 @PathVariable("type") String type,@RequestParam Map<String, Object> map) {
		map.put("column", columnName);
		map.put("type", type);
		
		if(type.equals("2")) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			Calendar c = Calendar.getInstance();
			Date remindStartDate = null;
			Date remindEndDate = null;
			if(map.get("remindstart")!=null) {
				Integer remindStart = Integer.parseInt(map.get("remindstart").toString());
				c.setTime(new Date()); 
				c.add(Calendar.DAY_OF_MONTH,remindStart);
				remindStartDate = c.getTime();
				map.put("remindstart", sdf.format(remindStartDate));
			}
			if(map.get("remindend")!=null) {
				Integer remindEnd = Integer.parseInt(map.get("remindend").toString());
				c.setTime(new Date());
				c.add(Calendar.DAY_OF_MONTH,remindEnd);
				remindEndDate = c.getTime();
				map.put("remindend", sdf.format(remindEndDate));
			}
		}
		
		Wrapper<PutongyonghuEntity> wrapper = new EntityWrapper<PutongyonghuEntity>();
		if(map.get("remindstart")!=null) {
			wrapper.ge(columnName, map.get("remindstart"));
		}
		if(map.get("remindend")!=null) {
			wrapper.le(columnName, map.get("remindend"));
		}


		int count = putongyonghuService.selectCount(wrapper);
		return R.ok().put("count", count);
	}
	


}
