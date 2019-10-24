package com.youlexuan.user.service.impl;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.alibaba.fastjson.JSON;
import com.youlexuan.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.youlexuan.mapper.TbUserMapper;
import com.youlexuan.pojo.TbUser;
import com.youlexuan.pojo.TbUserExample;
import com.youlexuan.pojo.TbUserExample.Criteria;

import com.youlexuan.entity.PageResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.util.DigestUtils;

import javax.jms.*;

/**
 * 用户表服务实现层
 * @author Administrator
 *
 */
@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private TbUserMapper userMapper;

	@Autowired
	private RedisTemplate redisTemplate;

	@Autowired
	private Destination smsDestination;

	@Autowired
	private JmsTemplate jmsTemplate;

	@Value("${template_code}")
	private String template_code;

	@Value("${sign_name}")
	private String sign_name;
	
	/**
	 * 查询全部
	 */
	@Override
	public List<TbUser> findAll() {
		return userMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbUser> page=   (Page<TbUser>) userMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(TbUser user) {

		user.setCreated(new Date());//创建日期
		user.setUpdated(new Date());//修改日期
		String password = DigestUtils.md5DigestAsHex(user.getPassword().getBytes());//对密码加密
		user.setPassword(password);

		userMapper.insert(user);		
	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(TbUser user){

		userMapper.updateByPrimaryKey(user);
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public TbUser findOne(Long id){
		return userMapper.selectByPrimaryKey(id);
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			userMapper.deleteByPrimaryKey(id);
		}		
	}
	
	
		@Override
	public PageResult findPage(TbUser user, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbUserExample example=new TbUserExample();
		Criteria criteria = example.createCriteria();
		
		if(user!=null){			
						if(user.getUsername()!=null && user.getUsername().length()>0){
				criteria.andUsernameLike("%"+user.getUsername()+"%");
			}			if(user.getPassword()!=null && user.getPassword().length()>0){
				criteria.andPasswordLike("%"+user.getPassword()+"%");
			}			if(user.getPhone()!=null && user.getPhone().length()>0){
				criteria.andPhoneLike("%"+user.getPhone()+"%");
			}			if(user.getEmail()!=null && user.getEmail().length()>0){
				criteria.andEmailLike("%"+user.getEmail()+"%");
			}			if(user.getSourceType()!=null && user.getSourceType().length()>0){
				criteria.andSourceTypeLike("%"+user.getSourceType()+"%");
			}			if(user.getNickName()!=null && user.getNickName().length()>0){
				criteria.andNickNameLike("%"+user.getNickName()+"%");
			}			if(user.getName()!=null && user.getName().length()>0){
				criteria.andNameLike("%"+user.getName()+"%");
			}			if(user.getStatus()!=null && user.getStatus().length()>0){
				criteria.andStatusLike("%"+user.getStatus()+"%");
			}			if(user.getHeadPic()!=null && user.getHeadPic().length()>0){
				criteria.andHeadPicLike("%"+user.getHeadPic()+"%");
			}			if(user.getQq()!=null && user.getQq().length()>0){
				criteria.andQqLike("%"+user.getQq()+"%");
			}			if(user.getIsMobileCheck()!=null && user.getIsMobileCheck().length()>0){
				criteria.andIsMobileCheckLike("%"+user.getIsMobileCheck()+"%");
			}			if(user.getIsEmailCheck()!=null && user.getIsEmailCheck().length()>0){
				criteria.andIsEmailCheckLike("%"+user.getIsEmailCheck()+"%");
			}			if(user.getSex()!=null && user.getSex().length()>0){
				criteria.andSexLike("%"+user.getSex()+"%");
			}	
		}
		
		Page<TbUser> page= (Page<TbUser>)userMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 1.生成验证码
	 * 2.缓存验证码
	 * 3.存验证码(mq中)
	 * @param phone
	 */
	@Override
	public void createSmsCode(String phone) {
		//1.生成验证码

		/*StringBuffer code= new StringBuffer();
		for(int i = 0;i <6;i++){
			code.append((Math.random()*10));
		}*/


		String code =   (Math.random()*1000000)+"";
		if(code.length()>6){
			code=code.substring(0,6);
		}
		String finalCode = code;
		System.out.println("验证码："+finalCode);

		//2.存入缓存中
		redisTemplate.boundHashOps("smscode").put(phone,finalCode);
		redisTemplate.boundHashOps("smscode").expire(5,TimeUnit.MINUTES);
		//3.加工发送消息 mq

		jmsTemplate.send(this.smsDestination, new MessageCreator() {
			@Override
			public Message createMessage(Session session) throws JMSException {


				/**
				 * 你好,你的验正吗为${code}
				 */
				Map param=new HashMap();
				param.put("code", finalCode);

				MapMessage mapMessage = session.createMapMessage();
				mapMessage.setString("mobile",phone);
				mapMessage.setString("template_code",template_code);
				mapMessage.setString("sign_name",sign_name);
				mapMessage.setObject("param",JSON.toJSONString(param));

				return mapMessage;
			}
		});
	}

	/**
	 * 判断验证码是否正确
	 */
	public boolean  checkSmsCode(String phone,String code){
		//得到缓存中存储的验证码
		String sysCode = (String) redisTemplate.boundHashOps("smscode").get(phone);
		if(sysCode==null){
			return false;
		}
		if(!sysCode.equals(code)){
			return false;
		}
		return true;
	}

}
