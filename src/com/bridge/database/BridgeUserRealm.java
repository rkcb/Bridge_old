package com.bridge.database;

import org.apache.shiro.realm.jdbc.JdbcRealm;

public class BridgeUserRealm extends JdbcRealm {
	

	private final String roleQuery = "SELECT name from roles where id"+ 
			" in (select users_roles.roles_id from users_roles where users_id in"+ 
					"(select id from users where username = ?))";
	
	private final String permissionQuery = "select name from permissions where id in "+
			"(select permissions_id from roles_permissions where roles_id in"+
					" (select id from roles where name = ?))";
	
	
	public BridgeUserRealm(){
		setUserRolesQuery(roleQuery);
		setPermissionsLookupEnabled(true);
		setPermissionsQuery(permissionQuery);
		
	}

}
