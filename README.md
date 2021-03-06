
#### pom.xml
``` xml
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
	<modelVersion>4.0.0</modelVersion>

	<groupId>name.luoyong.hibernate</groupId>
	<artifactId>hibernate-JPA</artifactId>
	<version>0.0.1-SNAPSHOT</version>
	<packaging>jar</packaging>

	<name>hibernate-JPA</name>

	<properties>
		<project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
	</properties>

	<dependencies>
	
		<dependency>
			<groupId>junit</groupId>
			<artifactId>junit</artifactId>
			<version>4.11</version>
			<scope>test</scope>
		</dependency>

		<dependency>
			<groupId>org.hibernate</groupId>
			<artifactId>hibernate-entitymanager</artifactId>
			<version>4.3.6.Final</version>
		</dependency>
		
		<dependency>
    		<groupId>mysql</groupId>
    		<artifactId>mysql-connector-java</artifactId>
    		<version>5.1.6</version>
    	</dependency>
		
	</dependencies>
</project>
```

#### persistence.xml
``` xml
<persistence xmlns="http://java.sun.com/xml/ns/persistence"
             xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
             xsi:schemaLocation="http://java.sun.com/xml/ns/persistence http://java.sun.com/xml/ns/persistence/persistence_2_0.xsd"
             version="2.0">

    <persistence-unit name="org.hibernate.tutorial.jpa">
        <description>
            Persistence unit for the JPA tutorial of the Hibernate Getting Started Guide
        </description>

        <properties>
            <property name="javax.persistence.jdbc.driver" value="com.mysql.jdbc.Driver" />
            <property name="javax.persistence.jdbc.url" value="jdbc:mysql://localhost:3306/hibernate" />
            <property name="javax.persistence.jdbc.user" value="root" />
            <property name="javax.persistence.jdbc.password" value="****" />
            
            <property name="hibernate.hbm2ddl.auto" value="update" />
            
            <property name="hibernate.show_sql" value="true" />
            <property name="hibernate.format_sql" value="true" />
            
        </properties>

    </persistence-unit>
</persistence>
```

#### java test
``` java
package name.luoyong.hibernate.entitymanager;

import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.hibernate.SQLQuery;
import org.hibernate.transform.Transformers;

import name.luoyong.hibernate.basic.entity.Group;
import name.luoyong.hibernate.basic.entity.User;

public class App {

	private static EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("org.hibernate.tutorial.jpa");

	public static void main(String[] args) {

		persist();
		// find();
		// getReference();
		// remove();
		// queryResultList();
		// querySingleResult();
		// nativeQuery();
		// namedQuery();
		// testGetUserByFields();

		entityManagerFactory.close();
	}

	private static void persist() {
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		entityManager.getTransaction().begin();

		User ly = new User();
		ly.setUsername("shulei");
		ly.setPassword("asdfasf");

		Group group = new Group();
		group.setName("second group");

		ly.setGroup(group);
		group.getUsers().add(ly);

		entityManager.persist(ly);
		entityManager.persist(group);

		entityManager.getTransaction().commit();
		entityManager.close();
	}
	
	private static void find() {
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		entityManager.getTransaction().begin();

		User user = entityManager.find(User.class, 1L); // 1,缓存，2，若没有，立即去数据库加载,若没有，则返回null
		// User user2 = entityManager.getReference(User.class, 1L);
		// 1,缓存，2，若没有，延迟去数据库加载，若没有，EntityNotFoundException

		System.out.println(user.getUsername());
		System.out.println(user.getPassword());

		// entityManager.clear();
		// entityManager.merge(user);

		entityManager.getTransaction().commit();
		entityManager.close();
	}

	private static void getReference() {
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		entityManager.getTransaction().begin();

		User user = entityManager.getReference(User.class, 1L); 
		// 1,缓存，2，若没有，延迟去数据库加载，若没有，EntityNotFoundException

		user.setPassword("se"); // 当 commit 或 flush 时，更新到数据库

		System.out.println(user.getUsername());
		System.out.println(user.getPassword());

		entityManager.getTransaction().commit();
		entityManager.close();
	}
	
	private static void remove() {
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		entityManager.getTransaction().begin();

		User user = entityManager.find(User.class, 1L);
		entityManager.remove(user);

		entityManager.getTransaction().commit();
		entityManager.close();
	}
	
	private static void queryResultList() {
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		entityManager.getTransaction().begin();

		Query query = entityManager.createQuery("from User");
		List<User> users = query.getResultList();

		for (User user : users) {
			System.out.println(user.getUsername());
			System.out.println(user.getGroup().getName());
		}

		entityManager.getTransaction().commit();
		entityManager.close();
	}
	
	private static void querySingleResult() {
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		entityManager.getTransaction().begin();

		TypedQuery<User> query = entityManager.createQuery("from User where username=:username", User.class);
		query.setParameter("username", "luoyong");
		User user = query.getSingleResult(); // 若实际的结果不止 一个，则异常。

		System.out.println(user.getUsername());
		System.out.println(user.getGroup().getName());

		entityManager.getTransaction().commit();
		entityManager.close();
	}

	
	private static void nativeQuery() {
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		entityManager.getTransaction().begin();

		Query query = entityManager.createNativeQuery("select username, password from user2");
		query.unwrap(SQLQuery.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);

		List<Map> users = query.getResultList();

		for (Map userMap : users) {
			System.out.println(userMap.get("username"));

		}

		entityManager.getTransaction().commit();
		entityManager.close();
	}

	private static void namedQuery() {
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		entityManager.getTransaction().begin();

		Query query = entityManager.createQuery("from User");
		entityManagerFactory.addNamedQuery("fromUser", query);

		Query query2 = entityManager.createNamedQuery("fromUser");

		entityManager.getTransaction().commit();
		entityManager.close();
	}

	private static void testGetUserByFields() {
		User user = getUserByFields("version", 1);
		System.out.println(user.getUsername());
		System.out.println(user.getPassword());
	}

	private static User getUserByFields(Object... params) {
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		entityManager.getTransaction().begin();

		String sql = "from User where ";

		int paramsLength = params.length;

		int index = 0;
		while (index < paramsLength) {
			sql += " " + params[index] + "=:" + params[index];
			index += 2;
		}

		TypedQuery<User> query = entityManager.createQuery(sql, User.class);

		index = 0;
		while (index < paramsLength) {
			query.setParameter((String) params[index], params[index + 1]);
			index += 2;
		}

		User user = query.getSingleResult(); // 若实际的结果不止 一个，则异常。

		entityManager.getTransaction().commit();
		entityManager.close();

		return user;
	}

}
```