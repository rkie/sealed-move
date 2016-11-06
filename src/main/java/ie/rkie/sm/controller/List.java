package ie.rkie.sm.controller;

import ie.rkie.sm.db.Player;
import ie.rkie.sm.db.PlayerDao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Demonstration of DAO using crud interface.
 *
 */
// TODO: replace/delete this class.
@Controller
public class List {
	
	@Autowired
	private PlayerDao userDao;
	
	/**
	 * Returns a list of all users and their email.
	 * 
	 * @return
	 */
	@RequestMapping(value="/list", method=RequestMethod.GET)
	@ResponseBody
	public String listUsers() {
		StringBuilder builder = new StringBuilder("<html><body><ol>");
		for ( Player user : userDao.findAll() ) {
			builder.append("<li>").append(user.getUsername()).append(": ").append(user.getEmail()).append("</li>");
		}
		builder.append("</ol></body></html>");
		return builder.toString();
	}

}
