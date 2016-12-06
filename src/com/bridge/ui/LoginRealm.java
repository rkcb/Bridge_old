package com.bridge.ui;

import org.apache.shiro.realm.jdbc.JdbcRealm;


public class LoginRealm extends JdbcRealm {
	private final String roleQuery = "SELECT name from userRoles where userRoles.id"+ 
			" in (select users_roles.roles_id from users_roles where users_roles.users_id in"+ 
					"(select id from users where username = ?))";
	
	private final String permissionQuery = "select name from permissions where id in "+
			"(select permissions_id from roles_permissions where roles_id in"+
					" (select id from userRoles where userRoles.name = ?))";
	
	public LoginRealm(){
		setUserRolesQuery(roleQuery);
		setPermissionsLookupEnabled(true);
		setPermissionsQuery(permissionQuery);
	}
	
	
}
