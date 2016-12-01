package ie.rkie.sm.db;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Tests the dao and entities using in hsqldb.
 *
 */

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserDaoTest {
	
	@Autowired
	private UserDao dao;
	
	@Test
	public void testFindByUsername() {
		List<User> users = dao.findByUsername("bob");
		assertThat(users, hasSize(1));
	}
	
	@Test
	public void testFindByEmail() {
		List<User> users = dao.findByEmail("bob@test.com");
		assertThat(users, hasSize(1));
	}

}
