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
