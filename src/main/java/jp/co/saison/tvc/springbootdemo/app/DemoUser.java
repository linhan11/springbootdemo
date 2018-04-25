package jp.co.saison.tvc.springbootdemo.app;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "demo_user")
//@NamedQuery(name = "findByName", query = "select u.name,u.password from demo_user u where u.name = :name")
public class DemoUser {

	@Id
	@Column(name = "name", nullable = false)
	private String name;
	@Column(name = "password", nullable = false)
	private String password;

	protected DemoUser() {}

	public DemoUser(String name, String password) {
		this.name = name;
		this.password = password;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		if (name == null) {
			name = "";
		}
		this.name = name;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		if (password == null) {
			password = "";
		}
		this.password = password;
	}
}
