package com.itheima.mobilesafe.test;

import java.util.List;

import com.itheima.mobilesafe.db.dao.BlackNumberDao;
import com.itheima.mobilesafe.domain.BlackNumberInfo;

import android.content.Context;
import android.test.AndroidTestCase;

public class TestBlackNumberDao extends AndroidTestCase {

	private Context context;
	
	//不能直接获得getContext，生命周期问题，setUp时期，才可以get到context
	@Override
	protected void setUp() throws Exception {
		context=getContext();
		super.setUp();
	}
	
	/**
	 * 测试添加
	 * @throws Exception
	 */
	public void testAdd() throws Exception{
		BlackNumberDao dao=new BlackNumberDao(context);
	//	boolean result=dao.add("18888888888", "1");
	//	assertEquals(true, result);
		
		//添加一组数据用于测试
		for(long i=1;i<301;i++){
			dao.add(18888888000l+i+"", "1");
		}
	}
	/**
	 * 测试删除
	 * @throws Exception
	 */
	public void testDelete() throws Exception{
		BlackNumberDao dao = new BlackNumberDao(context);
		//boolean result = dao.delete("18888888888");
		//assertEquals(true, result);
		for(long i=1;i<301;i++){
			dao.delete(18888888000l+i+"");
		}
	}
	/**
	 * 测试修改
	 * @throws Exception
	 */
	public void testUpdate() throws Exception{
		BlackNumberDao dao = new BlackNumberDao(context);
		boolean result = dao.changeBlockMode("18888888888", "2");
		assertEquals(true, result);
	}
	/**
	 * 测试查询
	 * @throws Exception
	 */
	public void testFind() throws Exception{
		BlackNumberDao dao = new BlackNumberDao(context);
		String mode = dao.findBlockMode("18888888888");
		System.out.println(mode);
	}
	/**
	 * 测试查询全部
	 * @throws Exception
	 */
	public void testFindAll() throws Exception{
		BlackNumberDao dao = new BlackNumberDao(context);
		List<BlackNumberInfo> infos = dao.findAll();
		for(BlackNumberInfo info: infos){
			System.out.println(info.getNumber()+"---"+info.getMode());
		}
	}
}
