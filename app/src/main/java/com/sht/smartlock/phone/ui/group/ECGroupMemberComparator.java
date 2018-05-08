package com.sht.smartlock.phone.ui.group;

import com.yuntongxun.ecsdk.im.ECGroupMember;

import java.util.Comparator;

public class ECGroupMemberComparator implements Comparator<ECGroupMember> {

	@Override
	public int compare(ECGroupMember lhs, ECGroupMember rhs) {

		if(lhs.getRole()>rhs.getRole()){
			
			return 1;
		}
		
		return 0;
	}

}
